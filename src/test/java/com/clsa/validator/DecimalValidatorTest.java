package com.clsa.validator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.ErrorEntity;
import com.clsa.util.ApiUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DecimalValidatorTest {

	private final int COL_NUM = 1;
	private final int ROW_NUM = 1;
	
	private final String DECIMAL_VALUE = "123.4567";
	private final String INTEGER_VALUE = "1234567";
	private final String NON_NUMERIC_VALUE = "123xxx";
	
	
	@Autowired
	private DecimalValidator decimalValidator;
	
	@Test
	public void validate_ConfigAllowEmpty_ReturnNoError() {
		ErrorEntity errorEntity = decimalValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(true));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ConfigNotAllowEmpty_ReturnError() {
		ErrorEntity errorEntity = decimalValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(false));
		assertNotNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsDecimal_ReturnNoError() {
		ErrorEntity errorEntity = decimalValidator.validate(COL_NUM, ROW_NUM, DECIMAL_VALUE, getConfigEntity(false));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsInteger_ReturnNoError() {
		ErrorEntity errorEntity = decimalValidator.validate(COL_NUM, ROW_NUM, INTEGER_VALUE, getConfigEntity(false));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsNonNumeric_ReturnError() {
		ErrorEntity errorEntity = decimalValidator.validate(COL_NUM, ROW_NUM, NON_NUMERIC_VALUE, getConfigEntity(false));
		assertNotNull(errorEntity);
	}
	
	private ConfigEntity getConfigEntity(boolean allowEmpty) {
		ConfigEntity configEntity = new ConfigEntity();
		configEntity.setAllowEmpty(allowEmpty);
		configEntity.setColumnType(ApiUtil.FIELD_DECIMAL);
		return configEntity;
	}
}
