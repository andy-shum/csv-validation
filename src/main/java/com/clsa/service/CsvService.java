package com.clsa.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		logger.info("entering isInputValid | body: {}", body);

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
		logger.info("entering isPathAndFileExist | body: {}", body);

		if (!FileUtil.isFileExist(body.getFilePath())) {
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_NOT_FOUND);
			return false;
		}
		return true;
	}

	// to check if csv file content is valid
	private boolean isContentValid(CsvEntity body, ValidationResult response) {
		logger.info("entering isContentValid | body: {}", body);

		// read the csv file and validate the content
		try (BufferedReader br = new BufferedReader(new FileReader(body.getFilePath()))) {
			List<CompletableFuture<List<ErrorEntity>>> errorList = new ArrayList<CompletableFuture<List<ErrorEntity>>>();
			String line;
			int lineNum = 1;
			
			// insert uuid in map for counting the number of errors
			UUID uuid = UUID.randomUUID();
			MapUtil.add(uuid);
			
			while ((line = br.readLine()) != null) {
				String delimiter = body.getDelimiter().equals("|") ? "\\|" : body.getDelimiter();
				String[] values = line.split(delimiter);
				// validate line by line
				CompletableFuture<List<ErrorEntity>> error = validatorService.validateLine(body, values, lineNum++, uuid);
				errorList.add(error);
			}
			CompletableFuture<?>[] errorArray = new CompletableFuture<?>[errorList.size()];
			errorList.toArray(errorArray);
			CompletableFuture<Void> allFutures = CompletableFuture.allOf(errorArray);

			CompletableFuture<Object> allCompletableFuture = allFutures.thenApply(future -> {
				return errorList.stream().map(completableFuture -> completableFuture.join())
						.collect(Collectors.toList());
			});
			
			
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
		      
			if (finalErrors.size() > 0) {
				logger.info("processing isContentValid | finalErrors.size(): {}", finalErrors.size());
				response.setErrorDetail(finalErrors);
				ApiUtil.setResponse(response, ApiErrorCode.CSV_VALIDATION_ERROR);
				return false;
			}

		} catch (FileNotFoundException e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			e.printStackTrace();
			ApiUtil.setResponse(response, ApiErrorCode.CSV_FILE_NOT_FOUND);
			return false;
		} catch (IOException e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			e.printStackTrace();
			ApiUtil.setResponse(response, ApiErrorCode.CSV_PARSE_ERROR);
			return false;
		} catch (Exception e) {
			logger.error("processing isContentValid | exception: {}", e.getMessage());
			e.printStackTrace();
			ApiUtil.setResponse(response, ApiErrorCode.GENERAL_ERROR);
			return false;
		}

		return true;
	}

	public boolean isCsvValid(CsvEntity body, ValidationResult response) {
		logger.info("entering isCsvValid | body: {}", body);
		
		if (!isInputValid(body, response)) {
			return false;
		}
		
		if (!isPathAndFileExist(body, response)) {
			return false;
		}

		if (!isContentValid(body, response)) {
			return false;
		}
		
		return true;
	}

	

}
