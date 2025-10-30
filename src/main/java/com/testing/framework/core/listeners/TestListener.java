package com.testing.framework.core.listeners;

import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for enhanced logging and reporting
 */
public class TestListener implements ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    
    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Starting Test Suite: {} ===", context.getName());
        logger.info("Total tests to run: {}", context.getAllTestMethods().length);
    }
    
    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Finished Test Suite: {} ===", context.getName());
        logger.info("Tests passed: {}", context.getPassedTests().size());
        logger.info("Tests failed: {}", context.getFailedTests().size());
        logger.info("Tests skipped: {}", context.getSkippedTests().size());
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}.{}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✓ Test PASSED: {}.{}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("✗ Test FAILED: {}.{}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
        logger.error("Failure reason: {}", result.getThrowable().getMessage());
        
        // Attach failure details to Allure report
        saveTextLog(result.getThrowable().toString());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⊘ Test SKIPPED: {}.{}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: {}.{}", 
            result.getTestClass().getName(), 
            result.getMethod().getMethodName());
    }
    
    @Attachment(value = "Failure Log", type = "text/plain")
    public String saveTextLog(String message) {
        return message;
    }
}
