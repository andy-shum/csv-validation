package com.clsa.util;

import com.clsa.csvservice.models.ValidationResult;
import com.clsa.error.ApiErrorCode;

/**
 * This is utility for API calls
 */
public class ApiUtil {
	
	public static final String FIELD_BOOLEAN = "BOOLEAN";
	public static final String FIELD_DATETIME = "DATETIME";
	public static final String FIELD_DECIMAL = "DECIMAL";
	public static final String FIELD_INTEGER = "INTEGER";
	public static final String FIELD_STRING = "STRING";
	
	public static final String UPPERCASE = "U";
	public static final String LOWERCASE = "L";
	
	public static void setResponse(ValidationResult response, ApiErrorCode errorCode) {
		response.setReturnCode(errorCode.errorId());
		response.setReturnDesc(errorCode.errorMsg());
	}

}
