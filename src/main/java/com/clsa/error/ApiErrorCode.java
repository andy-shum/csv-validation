package com.clsa.error;

/**
 * This is for API error code
 */
public enum ApiErrorCode {

	GENERAL_ERROR("CSV-ERR-001", "General Exception"),
	INPUT_ERROR_FILEPATH_EMPTY("CSV-ERR-002", "File path is a mandatory field"),
	INPUT_ERROR_DELIMITER_EMPTY("CSV-ERR-003", "Delimiter is a mandatory field"),
	INPUT_ERROR_DELIMITER_INCORRECT("CSV-ERR-004", "Delimiter only support single character"),
	INPUT_ERROR_CONFIG_EMPTY("CSV-ERR-005", "Configuration is a mandatory field"),
	CSV_FILE_NOT_FOUND("CSV-ERR-006", "CSV file does not exist or not a file"),
	CSV_PARSE_ERROR("CSV-ERR-007", "CSV format is wrong"),
	CSV_COLUMN_NOT_MATCH("CSV-ERR-008", "The number of columns found not matching config"),
	CSV_VALIDATION_ERROR("CSV-ERR-009", "The CSV file does not pass validation");
	
	
	private String errorId;
	
	private String errorMsg;
	
	ApiErrorCode(final String errorId, final String errorMsg) {
		this.errorId = errorId;
		this.errorMsg = errorMsg;	
	}
	
	public String errorId() {
		return this.errorId;
	}
	
	public String errorMsg() {
		return this.errorMsg;
	}
}
