package com.testing.framework.ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.testing.framework.core.config.ConfigManager;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base page class implementing Page Object Model pattern
 */
public abstract class BasePage {
    
    protected final Page page;
    protected final ConfigManager config;
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    
    public BasePage(Page page) {
        this.page = page;
        this.config = ConfigManager.getInstance();
    }
    
    /**
     * Navigate to a URL
     */
    public void navigateTo(String url) {
        logger.info("Navigating to: {}", url);
        page.navigate(url);
        waitForPageLoad();
    }
    
    /**
     * Navigate to base URL with path
     */
    public void navigateToPath(String path) {
        String fullUrl = config.getUiBaseUrl() + path;
        navigateTo(fullUrl);
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    /**
     * Get page title
     */
    public String getTitle() {
        return page.title();
    }
    
    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }
    
    /**
     * Take screenshot for Allure report
     */
    public void takeScreenshot(String name) {
        byte[] screenshot = page.screenshot();
        Allure.addAttachment(name, "image/png", 
            new java.io.ByteArrayInputStream(screenshot), "png");
    }
    
    /**
     * Click element with retry
     */
    protected void clickWithRetry(String selector, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                page.click(selector);
                return;
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
                logger.warn("Click failed, retrying... ({}/{})", i + 1, maxRetries);
                page.waitForTimeout(1000);
            }
        }
    }
    
    /**
     * Type text with delay for more realistic input
     */
    protected void typeSlowly(String selector, String text, int delayMs) {
        page.locator(selector).pressSequentially(text, new com.microsoft.playwright.Locator.PressSequentiallyOptions().setDelay(delayMs));
    }
    
    /**
     * Check if element is visible
     */
    protected boolean isVisible(String selector) {
        try {
            return page.isVisible(selector);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Wait for element to be visible
     */
    protected void waitForElement(String selector) {
        page.waitForSelector(selector);
    }
    
    /**
     * Wait for element to be visible with timeout
     */
    protected void waitForElement(String selector, int timeoutMs) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(timeoutMs));
    }
}
