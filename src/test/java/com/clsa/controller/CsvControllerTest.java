package com.clsa.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import com.clsa.csvservice.models.ConfigEntity;
import com.clsa.csvservice.models.CsvEntity;
import com.clsa.error.ApiErrorCode;
import com.clsa.util.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CsvControllerTest {
	
	private final String POST_PATH = "/validation";
	private final String DELIMITER_SEMICOLON = ";";
	private final String DELIMITER_COMMA = ",";
	private final String DELIMITER_PIPE = "|";
	
	private final ApiErrorCode expectedErrorCode = ApiErrorCode.CSV_VALIDATION_ERROR;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void validateSomeColType_ShouldReturnOk() throws Exception {
		
		File file = ResourceUtils.getFile("classpath:testdata_3col_ok.csv");
		
		List<ConfigEntity> configEntity = new ArrayList<ConfigEntity>();
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_INTEGER, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
        
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setFilePath(file.getPath());
        csvEntity.setDelimiter(DELIMITER_SEMICOLON);
        csvEntity.setConfig(configEntity);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(csvEntity);
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson);
        
        mockMvc.perform(mockRequest)
        .andExpect(status().isOk());
        
	}
	
	@Test
	public void validateSomeColType_ShouldReturnException() throws Exception {
		
		File file = ResourceUtils.getFile("classpath:testdata_3col_notok.csv");
		
		List<ConfigEntity> configEntity = new ArrayList<ConfigEntity>();
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_INTEGER, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
        
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setFilePath(file.getPath());
        csvEntity.setDelimiter(DELIMITER_SEMICOLON);
        csvEntity.setConfig(configEntity);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(csvEntity);
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson);
        
        // expected to find six defects
        mockMvc.perform(mockRequest)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.returnCode").value(expectedErrorCode.errorId()))
        .andExpect(jsonPath("$.returnDesc").value(expectedErrorCode.errorMsg()))
        .andExpect(jsonPath("$.errorDetail", hasSize(6)));
       
	}
	
	@Test
	public void validateAllCol_ShouldReturnOk() throws Exception {
		
		File file = ResourceUtils.getFile("classpath:testdata_5col_ok.csv");
		
		List<ConfigEntity> configEntity = new ArrayList<ConfigEntity>();
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_BOOLEAN, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DATETIME, "dd/MM/yyyy", null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DECIMAL, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_INTEGER, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, ApiUtil.UPPERCASE));
        
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setFilePath(file.getPath());
        csvEntity.setDelimiter(DELIMITER_COMMA);
        csvEntity.setConfig(configEntity);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(csvEntity);
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson);
        
        mockMvc.perform(mockRequest)
        .andExpect(status().isOk());
        
	}
	
	@Test
	public void validate_TenThousandsOfRow_ShouldReturnOk() throws Exception {
		
		File file = ResourceUtils.getFile("classpath:testdata_100k_row_ok.csv");
		
		List<ConfigEntity> configEntity = new ArrayList<ConfigEntity>();
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_BOOLEAN, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DATETIME, "dd/MM/yyyy", null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DECIMAL, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_INTEGER, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, ApiUtil.UPPERCASE));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
        
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setFilePath(file.getPath());
        csvEntity.setDelimiter(DELIMITER_PIPE);
        csvEntity.setConfig(configEntity);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(csvEntity);
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson);
        
        mockMvc.perform(mockRequest)
        .andExpect(status().isOk());
        
	}
	
	@Test
	public void validate_TenThousandsOfRow_ShouldReturnNotOk() throws Exception {
		
		File file = ResourceUtils.getFile("classpath:testdata_100k_row_notok.csv");
		
		List<ConfigEntity> configEntity = new ArrayList<ConfigEntity>();
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_BOOLEAN, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DATETIME, "dd/MM/yyyy", null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_DECIMAL, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_INTEGER, null, null));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, ApiUtil.UPPERCASE));
		configEntity.add(buildConfigEntity(false, ApiUtil.FIELD_STRING, null, null));
        
        CsvEntity csvEntity = new CsvEntity();
        csvEntity.setFilePath(file.getPath());
        csvEntity.setDelimiter(DELIMITER_PIPE);
        csvEntity.setConfig(configEntity);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(csvEntity);
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(POST_PATH)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson);
        
        // expected to find six defects
        mockMvc.perform(mockRequest)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.returnCode").value(expectedErrorCode.errorId()))
        .andExpect(jsonPath("$.returnDesc").value(expectedErrorCode.errorMsg()))
        .andExpect(jsonPath("$.errorDetail", hasSize(5)));
        
	}
	
	private ConfigEntity buildConfigEntity(boolean allowEmpty, String columnType, String dateFormat, String letterCase) {
		ConfigEntity config = new ConfigEntity();
		config.setAllowEmpty(allowEmpty);
		config.setColumnType(columnType);
		config.setDateFormat(dateFormat);
		config.setLetterCase(letterCase);
		return config;
	}

}
