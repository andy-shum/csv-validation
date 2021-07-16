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
public class DatetimeValidatorTest {
	
	private final int COL_NUM = 1;
	private final int ROW_NUM = 1;
	private final String DATEFORMAT_DDMMYYYY = "dd-MM-yyyy";
	private final String DATEFORMAT_DDMMYYYY_MATCH_VALUE = "30-04-2021";
	private final String DATEFORMAT_DDMMYYYY_NOT_MATCH_VALUE = "04-30-2021";
	
	private final String DATEFORMAT_YYYYMMMDD = "yyyy/MMM/dd";
	private final String DATEFORMAT_YYYYMMMDD_MATCH_VALUE = "2021/Jul/10";
	private final String DATEFORMAT_YYYYMMMDD_NOT_MATCH_VALUE = "04-30-2021";
	
	@Autowired
	private DatetimeValidator datetimeValidator;
	
	@Test
	public void validate_ConfigAllowEmpty_ReturnNoError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(true, DATEFORMAT_DDMMYYYY));
		assertNull(errorEntity);
	}

	@Test
	public void validate_ConfigNotAllowEmpty_ReturnError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, null, getConfigEntity(false, DATEFORMAT_DDMMYYYY));
		assertNotNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsDDMMYYYY_ReturnNoError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, DATEFORMAT_DDMMYYYY_MATCH_VALUE, getConfigEntity(false, DATEFORMAT_DDMMYYYY));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsNotDDMMYYYY_ReturnError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, DATEFORMAT_DDMMYYYY_NOT_MATCH_VALUE, getConfigEntity(false, DATEFORMAT_DDMMYYYY));
		assertNotNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsYYYYMMMDD_ReturnNoError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, DATEFORMAT_YYYYMMMDD_MATCH_VALUE, getConfigEntity(false, DATEFORMAT_YYYYMMMDD));
		assertNull(errorEntity);
	}
	
	@Test
	public void validate_ValueIsNotYYYYMMMDD_ReturnError() {
		ErrorEntity errorEntity = datetimeValidator.validate(COL_NUM, ROW_NUM, DATEFORMAT_YYYYMMMDD_NOT_MATCH_VALUE, getConfigEntity(false, DATEFORMAT_YYYYMMMDD));
		assertNotNull(errorEntity);
	}
	
	private ConfigEntity getConfigEntity(boolean allowEmpty, String dateFormat) {
		ConfigEntity configEntity = new ConfigEntity();
		configEntity.setAllowEmpty(allowEmpty);
		configEntity.setColumnType(ApiUtil.FIELD_DATETIME);
		configEntity.setDateFormat(dateFormat);
		return configEntity;
	}
}
