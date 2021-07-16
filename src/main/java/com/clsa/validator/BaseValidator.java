package com.clsa.validator;

import org.springframework.stereotype.Component;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ErrorUtil;

/**
 * This is the base validator
 */
@Component
public abstract class BaseValidator {
	
	public abstract ErrorEntity validate(int colNum, int rowNum, String value, ConfigEntity configEntity);
	
	// validate cell value is not empty
	ErrorEntity isEmptyValidated(int colNum, int rowNum, String value, ConfigEntity configEntity) {
		ErrorEntity errorEntity = null;
		if (!configEntity.isAllowEmpty()) {
			if (value == null || value.isEmpty()) {
				errorEntity = ErrorUtil.getErrorEntity(rowNum, colNum, ApiErrorDetail.CELL_VALUE_IS_EMPTY);
			}
		}
		
		return errorEntity;
	}

}
