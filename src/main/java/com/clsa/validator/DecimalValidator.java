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
 * This is the validator for decimal cells
 */
@Component
@Qualifier("DecimalValidator")
public class DecimalValidator extends BaseValidator {
	
	private static final Logger logger = LogManager.getLogger(DecimalValidator.class);
	
	public ErrorEntity validate(int colNum, int rowNum, String value, ConfigEntity configEntity) {
		//logger.info("entering validate | colNum: {}, rowNum: {}, value: {}", colNum, rowNum, value);
		
		ErrorEntity errorEntity = null;
		
		// if value is null and allow empty there is no need to further check
		if (configEntity.isAllowEmpty() && (value == null || value.isEmpty())) {
			return errorEntity;
		}
		
		// check if value is empty
		errorEntity = isEmptyValidated(colNum, rowNum, value, configEntity);
		
		if (errorEntity == null) {
			
			// check if value is decimal
			if (!isDecimal(value)) {
				errorEntity = ErrorUtil.getErrorEntity(rowNum, colNum, ApiErrorDetail.CELL_VALUE_IS_NOT_DECIMAL);
			}
			
		}
		
		return errorEntity;
	}
	
	/*
	 * check if value is integer
	 * alternative way is to use regular expression
	 * but regex maybe slow, so we use parse here
	 */
	public boolean isDecimal(String value) {
	    try {
	        double d = Double.parseDouble(value);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

}
