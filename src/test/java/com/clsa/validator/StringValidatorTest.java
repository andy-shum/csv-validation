package com.clsa.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.error.ApiErrorDetail;
import com.clsa.util.ApiUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StringValidatorTest {
	
	private final int COL_NUM = 1;
	private final int ROW_NUM = 1;
	
	private final String EMPTY_VALUE = "";
	private final String UPPER_VALUE = "TEXT";
	private final String LOWER_VALUE = "text";
	private final String MIXED_VALUE = "Text";
	
	
	@Autowired
	private StringValidator stringValidator;
	
	// Test allow empty
	
	@Test
	public void validate_ConfigAllowEmpty_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(true, ApiUtil.UPPERCASE));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ConfigAllowEmptyString_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, EMPTY_VALUE, getConfigEntity(true, ApiUtil.UPPERCASE));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ConfigNotAllowEmpty_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(false, ApiUtil.UPPERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_IS_EMPTY, errorEntity.getErrorDesc());
	}
	
	@Test
	public void validate_ConfigNotAllowEmptyString_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, EMPTY_VALUE, getConfigEntity(false, ApiUtil.UPPERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_IS_EMPTY, errorEntity.getErrorDesc());
	}
	
	// Test UPPER as config
	
	@Test
	public void validate_ExpectUpperAndValueIsUpper_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, UPPER_VALUE, getConfigEntity(false, ApiUtil.UPPERCASE));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ExpectUpperButValueIsLower_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, LOWER_VALUE, getConfigEntity(false, ApiUtil.UPPERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_NOT_MATCH_LETTER_CASE, errorEntity.getErrorDesc());
	}
	
	@Test
	public void validate_ExpectUpperButValueIsMixed_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, MIXED_VALUE, getConfigEntity(false, ApiUtil.UPPERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_NOT_MATCH_LETTER_CASE, errorEntity.getErrorDesc());
	}
	
	// Test LOWER as config
	
	@Test
	public void validate_ExpectLowerAndValueIsLower_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, LOWER_VALUE, getConfigEntity(false, ApiUtil.LOWERCASE));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ExpectLowerButValueIsUpper_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, UPPER_VALUE, getConfigEntity(false, ApiUtil.LOWERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_NOT_MATCH_LETTER_CASE, errorEntity.getErrorDesc());
	}
	
	@Test
	public void validate_ExpectLowerButValueIsMixed_ReturnError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, MIXED_VALUE, getConfigEntity(false, ApiUtil.LOWERCASE));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_NOT_MATCH_LETTER_CASE, errorEntity.getErrorDesc());
	}
	
	// Test MIXED as config
	
	@Test
	public void validate_ExpectMixedAndValueIsMixed_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, MIXED_VALUE, getConfigEntity(false, "M"));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ExpectMixedButValueIsUpper_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, UPPER_VALUE, getConfigEntity(false, "M"));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ExpectMixedButValueIsLower_ReturnNoError() {
		ErrorEntity errorEntity = stringValidator.validate(COL_NUM, ROW_NUM, LOWER_VALUE, getConfigEntity(false, "M"));
		assertNull(errorEntity);
	}
	
	private ConfigEntity getConfigEntity(boolean allowEmpty, String letterCase) {
		ConfigEntity configEntity = new ConfigEntity();
		configEntity.setAllowEmpty(allowEmpty);
		configEntity.setColumnType(ApiUtil.FIELD_STRING);
		configEntity.setLetterCase(letterCase);
		return configEntity;
	}

}
