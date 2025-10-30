package com.testing.framework.api.client;

import com.testing.framework.core.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base API client with RestAssured configuration
 */
public class BaseApiClient {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected static final ConfigManager config = ConfigManager.getInstance();
    
    protected RequestSpecification requestSpec;
    
    public BaseApiClient() {
        setupRequestSpecification();
    }
    
    protected void setupRequestSpecification() {
        RestAssured.baseURI = config.getApiBaseUrl();
        
        requestSpec = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new AllureRestAssured())
            .setRelaxedHTTPSValidation()
            .build();
    }
    
    /**
     * Add bearer token authentication
     */
    public void setBearerAuth(String token) {
        requestSpec = new RequestSpecBuilder()
            .addRequestSpecification(requestSpec)
            .addHeader("Authorization", "Bearer " + token)
            .build();
    }
    
    /**
     * Add basic authentication
     */
    public void setBasicAuth(String username, String password) {
        requestSpec = new RequestSpecBuilder()
            .addRequestSpecification(requestSpec)
            .setAuth(RestAssured.basic(username, password))
            .build();
    }
    
    /**
     * Add custom header
     */
    public void addHeader(String key, String value) {
        requestSpec = new RequestSpecBuilder()
            .addRequestSpecification(requestSpec)
            .addHeader(key, value)
            .build();
    }
    
    /**
     * Reset request specification to defaults
     */
    public void resetRequestSpec() {
        setupRequestSpecification();
    }
}
