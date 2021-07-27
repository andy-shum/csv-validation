package com.clsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.CsvEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ApiUtil;
import com.clsa.util.ErrorUtil;
import com.clsa.util.MapUtil;
import com.clsa.validator.BaseValidator;
import com.clsa.validator.BooleanValidator;
import com.clsa.validator.DatetimeValidator;
import com.clsa.validator.DecimalValidator;
import com.clsa.validator.IntegerValidator;
import com.clsa.validator.StringValidator;

@Service
public class ValidatorService {
	
	private static final Logger logger = LogManager.getLogger(ValidatorService.class);
	
	@Autowired
	private BooleanValidator booleanValidator;
	
	@Autowired
	private DatetimeValidator datetimeValidator;
	
	@Autowired
	private DecimalValidator decimalValidator;
	
	@Autowired
	private IntegerValidator integerValidator;
	
	@Autowired
	private StringValidator stringValidator;
	
	@Async
	public CompletableFuture<List<ErrorEntity>> validateLine(CsvEntity body, String line, int lineNum, UUID uuid) {
		//logger.info("entering validateLine | lineNum: {}, line: {}", lineNum, line);

		List<ErrorEntity> errors = new ArrayList<ErrorEntity>();
		
		String delimiter = body.getDelimiter().equals("|") ? "\\|" : body.getDelimiter();
		String[] values = line.split(delimiter);
		
		// try to stop process if error exceed limit
		if (MapUtil.isExceed(uuid)) {
			return CompletableFuture.completedFuture(errors);
		}

		// check if number of columns does not match number of configs
		if (body.getConfig().size() > values.length) {
			errors.add(ErrorUtil.getErrorEntity(lineNum, body.getConfig().size(), ApiErrorDetail.CONFIG_COLUMN_NOT_FOUND));
			MapUtil.add(uuid);
		} else if (values.length > body.getConfig().size()) {
			errors.add(ErrorUtil.getErrorEntity(lineNum, values.length, ApiErrorDetail.CSV_COLUMN_NOT_FOUND));
			MapUtil.add(uuid);
		}
		
		int colIndex = 0;
		for (ConfigEntity config : body.getConfig()) {
			
			// for case of config size larger than available columns found in row
			if (colIndex >= values.length) {
				break;
			}
			
			// try to stop process if error exceed limit
			if (MapUtil.isExceed(uuid)) {
				break;
			}
			
			// mapping validator by column type
			BaseValidator baseValidator = validationMapper(config.getColumnType());
			
			if (baseValidator == null) {
				errors.add(ErrorUtil.getErrorEntity(lineNum, colIndex + 1, ApiErrorDetail.CONFIG_COLUMN_TYPE_INCORRECT));
			} else {
				// call validator to validate
				ErrorEntity error = baseValidator.validate(colIndex + 1, lineNum, values[colIndex], config);
				if (error != null) {
					errors.add(error);
				}
			}
			
			colIndex++;
		}

		return CompletableFuture.completedFuture(errors);
	}
	
	// mapping validator by column type
	private BaseValidator validationMapper(String colType) {
		BaseValidator baseValidator;
		switch (colType.toUpperCase()) {
			case ApiUtil.FIELD_BOOLEAN:
				baseValidator = booleanValidator;
				break;
			case ApiUtil.FIELD_DATETIME:
				baseValidator = datetimeValidator;
				break;
			case ApiUtil.FIELD_DECIMAL:
				baseValidator = decimalValidator;
				break;
			case ApiUtil.FIELD_INTEGER:
				baseValidator = integerValidator;
				break;
			case ApiUtil.FIELD_STRING:
				baseValidator = stringValidator;
				break;
			default:
				baseValidator = null;
				break;
		}
		return baseValidator;
	}

}
