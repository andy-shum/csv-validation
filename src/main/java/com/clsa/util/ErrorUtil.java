package com.clsa.util;

import com.clsa.csvservice.models.ErrorEntity;

/**
 * This is utility for error handling
 */
public class ErrorUtil {
	
	// builder of error entity
	public static ErrorEntity getErrorEntity(int rowNum, int colNum, String desc) {
		ErrorEntity error = new ErrorEntity();
		error.setRowNumber(rowNum);
		error.setColumnNumber(colNum);
		error.setErrorDesc(desc);
		return error;
	}

}
