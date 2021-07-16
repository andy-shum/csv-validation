package com.clsa.validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ErrorUtil;

/**
 * This is the validator for datetime cells
 */
@Component
@Qualifier("DatetimeValidator")
public class DatetimeValidator extends BaseValidator {
	
	private static final Logger logger = LogManager.getLogger(DatetimeValidator.class);
	
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
			
			// check if value matches date format
			if (!isMatchDateFormat(value, configEntity.getDateFormat())) {
				errorEntity = ErrorUtil.getErrorEntity(rowNum, colNum, ApiErrorDetail.CELL_VALUE_NOT_MATCH_DATE_FORMAT);
			}
			
		}
		
		return errorEntity;
	}
	
	/*
	 * check if value is date
	 * and only return true if matches the defined date format
	 */
	public boolean isMatchDateFormat(String value, String dateFormat) {
		DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
	}

}
