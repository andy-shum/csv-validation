package com.clsa.service;

//import java.io.BufferedReader;
import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clsa.csvservice.models.CsvEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.csvservice.models.ValidationResult;
import com.clsa.error.ApiErrorCode;
import com.clsa.util.ApiUtil;
import com.clsa.util.FileUtil;
import com.clsa.util.MapUtil;

@Service
/**
 * This is the main service for CSV API
 */
public class CsvService {

	private static final Logger logger = LogManager.getLogger(CsvService.class);
	
	@Autowired
	private ValidatorService validatorService;

	// to check if input is valid
	private boolean isInputValid(CsvEntity body, ValidationResult response) {
		logger.info("entering isInputValid | body.filePath: {}", body.getFilePath());

		if (null == body.getFilePath() || body.getFilePath().isEmpty()) {
			ApiUtil.setResponse(response, ApiErrorCode.INPUT_ERROR_FILEPATH_EMPTY);
			return false;
		} else if (null == body.getDelimiter() || body.getDelimiter().isEmpty()) {
			ApiUtil.setResponse(response, ApiErrorCode.INPUT_ERROR_DELIMITER_EMPTY);
			return false;
		} else if (null == body.getConfig() || body.getConfig().isEmpty()) {
			ApiUtil.setResponse(response, ApiErrorCode.INPUT_ERROR_CONFIG_EMPTY);
			return false;
		} else if (body.getDelimiter().length() > 1) {
			ApiUtil.setResponse(response, ApiErrorCode.INPUT_ERROR_DELIMITER_INCORRECT);
			return false;
		}
		return true;
	}
	
	// to check if the file path is valid
	private boolean isPathAndFileExist(CsvEntity body, ValidationResult response) {
		logger.info("entering isPathAndFileExist | body.filePath: {}", body.getFilePath());

		if (!FileUtil.isFileExist(body.getFilePath())) {
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_NOT_FOUND);
			return false;
		}
		return true;
	}

	// to check if csv file content is valid
	private boolean isContentValid(CsvEntity body, ValidationResult response) {
		logger.info("entering isContentValid | body.filePath: {}", body.getFilePath());
		
		long startTime1 = System.currentTimeMillis();
		
		RandomAccessFile writer = null;
		FileLock lock = null;
		List<String> lines = null;
		
		try {
			// open file in read-write mode
		    writer = new RandomAccessFile(body.getFilePath(), "rw");
	
		    // lock file
		    lock = writer.getChannel().tryLock();
		    
		    // read file content into lines object
		    lines = Files.readAllLines(Paths.get(body.getFilePath()));
	
		    // release lock
		    lock.release();
	
		    // close the file
		    writer.close();

		} catch (FileNotFoundException e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_NOT_FOUND);
			return false;
		} catch (OverlappingFileLockException e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_IS_LOCKED);
			return false;
		} catch (Exception e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			ApiUtil.setResponse(response, ApiErrorCode.GENERAL_ERROR);
			return false;
		}
		
		try {

			List<CompletableFuture<List<ErrorEntity>>> errorList = new ArrayList<CompletableFuture<List<ErrorEntity>>>();

			int lineNum = 1;
			
			// insert uuid in map for counting the number of errors
			UUID uuid = UUID.randomUUID();
			MapUtil.add(uuid);
			
			for (String line : lines) {
				
				// validate line by line
				CompletableFuture<List<ErrorEntity>> error = validatorService.validateLine(body, line, lineNum++, uuid);
				errorList.add(error);
			}
			CompletableFuture<?>[] errorArray = new CompletableFuture<?>[errorList.size()];
			errorList.toArray(errorArray);
			CompletableFuture<Void> allFutures = CompletableFuture.allOf(errorArray);

			CompletableFuture<Object> allCompletableFuture = allFutures.thenApplyAsync(future -> {
				return errorList.stream().map(completableFuture -> completableFuture.join())
						.collect(Collectors.toList());
			});
			
			long endTime1 = System.currentTimeMillis();
			long duration1 = endTime1 - startTime1;
			logger.info("Time Spent on validate content part 1: {}", String.valueOf(duration1));
			
			long startTime2 = System.currentTimeMillis();
			
			// get the result from multi thread processing
			List<List<ErrorEntity>> allErrors = (List<List<ErrorEntity>>) allCompletableFuture.get();
			
			// final result is put into here
			List<ErrorEntity> finalErrors = new ArrayList<ErrorEntity>();
			
			for (List<ErrorEntity> errList : allErrors) {
				if (errList.size() > 0) {
					finalErrors.addAll(errList);
				}
			}
			
			// clean up the map after finish
			MapUtil.remove(uuid);
			
			long endTime2 = System.currentTimeMillis();
			long duration2 = endTime2 - startTime2;
			logger.info("Time Spent on validate content part 2: {}", String.valueOf(duration2));
		      
			if (finalErrors.size() > 0) {
				logger.info("processing isContentValid | finalErrors.size(): {}", finalErrors.size());
				response.setErrorDetail(finalErrors);
				ApiUtil.setResponse(response, ApiErrorCode.CSV_VALIDATION_ERROR);
				return false;
			}

		} catch (OverlappingFileLockException e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_IS_LOCKED);
			return false;
		} catch (Exception e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			ApiUtil.setResponse(response, ApiErrorCode.GENERAL_ERROR);
			return false;
		}

		return true;
	}

	public boolean isCsvValid(CsvEntity body, ValidationResult response) {
		logger.info("entering isCsvValid | filePath: {}", body.getFilePath());
		
		long startTime1 = System.currentTimeMillis();
		if (!isInputValid(body, response)) {
			return false;
		}
		long endTime1 = System.currentTimeMillis();
		long duration1 = endTime1 - startTime1;
		logger.info("Total Time Spent on validate input: {}", String.valueOf(duration1));
		
		long startTime2 = System.currentTimeMillis();
		if (!isPathAndFileExist(body, response)) {
			return false;
		}
		long endTime2 = System.currentTimeMillis();
		long duration2 = endTime2 - startTime2;
		logger.info("Total Time Spent on validate filepath: {}", String.valueOf(duration2));

		if (!isContentValid(body, response)) {
			return false;
		}
		
		return true;
	}

	

}
