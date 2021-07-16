package com.clsa.error;

/**
 * This is for error detail on cell check based on config provided
 */
public class ApiErrorDetail {
	
	public static final String CONFIG_COLUMN_NOT_FOUND = "config column is not found from CSV line";
	public static final String CSV_COLUMN_NOT_FOUND = "CSV line column is not found from config";
	public static final String CONFIG_COLUMN_TYPE_INCORRECT = "config column type is incorrect or not supported";
	public static final String CELL_VALUE_IS_EMPTY = "Cell value is not allowed empty";
	public static final String CELL_VALUE_IS_NOT_INTEGER = "Cell value is not integer";
	public static final String CELL_VALUE_IS_NOT_DECIMAL = "Cell value is not decimal";
	public static final String CELL_VALUE_IS_NOT_BOOLEAN = "Cell value is not boolean";
	public static final String CELL_VALUE_NOT_MATCH_LETTER_CASE = "Cell value does not match the defined letter case";
	public static final String CELL_VALUE_NOT_MATCH_DATE_FORMAT = "Cell value does not match the defined date format";

}
