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
public class BooleanValidatorTest {
	
	private final int COL_NUM = 1;
	private final int ROW_NUM = 1;
	
	private final String TRUE_VALUE = "true";
	private final String FALSE_VALUE = "false";
	private final String NOT_BOOLEAN_VALUE = "xxx";
	
	
	@Autowired
	private BooleanValidator booleanValidator;
	
	@Test
	public void validate_ConfigAllowEmpty_ReturnNoError() {
		ErrorEntity errorEntity = booleanValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(true));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ConfigNotAllowEmpty_ReturnError() {
		ErrorEntity errorEntity = booleanValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(false));
		assertNotNull(errorEntity);
		assertEquals(ApiErrorDetail.CELL_VALUE_IS_EMPTY, errorEntity.getErrorDesc());
	}
	
	@Test
	public void validate_ValueIsTrue_ReturnNoError() {
		ErrorEntity errorEntity = booleanValidator.validate(COL_NUM, ROW_NUM, TRUE_VALUE, getConfigEntity(false));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsFalse_ReturnNoError() {
		ErrorEntity errorEntity = booleanValidator.validate(COL_NUM, ROW_NUM, FALSE_VALUE, getConfigEntity(false));
		assertNull(errorEntity);
	}
	

	@Test
	public void validate_ValueIsFalse_ReturnError() {
		ErrorEntity errorEntity = booleanValidator.validate(COL_NUM, ROW_NUM, NOT_BOOLEAN_VALUE, getConfigEntity(false));
		assertNotNull(errorEntity);
	}
	
	private ConfigEntity getConfigEntity(boolean allowEmpty) {
		ConfigEntity configEntity = new ConfigEntity();
		configEntity.setAllowEmpty(allowEmpty);
		configEntity.setColumnType(ApiUtil.FIELD_BOOLEAN);
		return configEntity;
	}

}
