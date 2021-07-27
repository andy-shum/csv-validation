package com.clsa.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ApiUtil;
import com.clsa.util.ErrorUtil;

/**
 * This is the validator for string cells
 */
@Component
@Qualifier("StringValidator")
public class StringValidator extends BaseValidator {
	
	private static final Logger logger = LogManager.getLogger(StringValidator.class);
	
	public ErrorEntity validate(int colNum, int rowNum, String value, ConfigEntity configEntity) {
		//logger.info("entering validate | colNum: {}, rowNum: {}, value: {}", colNum, rowNum, value);
		
		ErrorEntity errorEntity = null;
		
		// if value is null and allow empty there is no need to further check
		if (configEntity.isAllowEmpty() && (value == null || value.isEmpty())) {
			return errorEntity;
		}
		
		// check if value is empty
		errorEntity = isEmptyValidated(colNum, rowNum, value, configEntity);
		
		// check if value match letter case
		if ((errorEntity == null) && (configEntity.getLetterCase() != null) 
				&& (!configEntity.getLetterCase().isEmpty())) {
			
			if (!isValueMatchDefinedCase(value, configEntity.getLetterCase())) {
				errorEntity = ErrorUtil.getErrorEntity(rowNum, colNum, ApiErrorDetail.CELL_VALUE_NOT_MATCH_LETTER_CASE);
			}
			
		}
		
		return errorEntity;
	}
	
	/*
	 * check if value matches defined letter case
	 * U for only upper case, L for only lower case, any other value is for mixed case
	 */
	public boolean isValueMatchDefinedCase(String value, String letterCase) {
		
		// to make it simple, we only check U / L
		// we will skip the checking for all other values defined in letter case
		if (!(letterCase.equals(ApiUtil.UPPERCASE) || letterCase.equals(ApiUtil.LOWERCASE))) {
			return true;
		}
		
		//convert value from string to char array
        char[] charArray = value.toCharArray();
        
        for(int i=0; i < charArray.length; i++){
        	
        	switch (letterCase) {
        		case ApiUtil.UPPERCASE:
        			if( !Character.isUpperCase(charArray[i])) {
        				return false;
        			}
        			break;
        		case ApiUtil.LOWERCASE:
        			if( !Character.isLowerCase(charArray[i])) {
        				return false;
        			}
        			break;
        	}
            
        }
        
        return true;
	}

}
