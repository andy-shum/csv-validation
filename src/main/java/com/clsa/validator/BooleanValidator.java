package com.clsa.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ErrorUtil;

/**
 * This is the validator for boolean cells
 */
@Component
@Qualifier("BooleanValidator")
public class BooleanValidator extends BaseValidator {
	
	private static final Logger logger = LogManager.getLogger(BooleanValidator.class);
	
	public ErrorEntity validate(int colNum, int rowNum, String value, ConfigEntity configEntity) {
		logger.info("entering validate | colNum: {}, rowNum: {}, value: {}", colNum, rowNum, value);
		
		ErrorEntity errorEntity = null;
		
		// if value is null and allow empty there is no need to further check
		if (configEntity.isAllowEmpty() && (value == null || value.isEmpty())) {
			return errorEntity;
		}
		
		// check if value is empty
		errorEntity = isEmptyValidated(colNum, rowNum, value, configEntity);
		
		if (errorEntity == null) {
			
			// check if value is boolean
			if (!isBoolean(value)) {
				errorEntity = ErrorUtil.getErrorEntity(rowNum, colNum, ApiErrorDetail.CELL_VALUE_IS_NOT_BOOLEAN);
			}
			
		}
		
		return errorEntity;
	}
	
	/*
	 * check if value is boolean
	 * only true or false will pass this check
	 * other values are not allowed
	 */
	public boolean isBoolean(String value) {
	    return value.toUpperCase().equals("TRUE") || value.toUpperCase().equals("FALSE");
	}

}
