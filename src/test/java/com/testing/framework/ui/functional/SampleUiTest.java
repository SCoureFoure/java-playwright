package com.testing.framework.ui.functional;

import com.microsoft.playwright.Page;
import com.testing.framework.ui.utils.BrowserManager;
import io.qameta.allure.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Sample UI test using Playwright with local demo app
 */
@Epic("UI Testing")
@Feature("Web Navigation")
public class SampleUiTest {
    
    private Page page;
    private static final String BASE_URL = "http://localhost:3000";
    
    @BeforeClass
    public void setupBrowser() {
        BrowserManager.initializeBrowser();
        page = BrowserManager.getPage();
    }
    
    @AfterClass
    public void teardownBrowser() {
        BrowserManager.closeBrowser();
    }
    
    @AfterMethod
    public void takeScreenshotOnFailure(org.testng.ITestResult result) {
        if (!result.isSuccess()) {
            try {
                BrowserManager.takeScreenshot("failed-" + result.getName());
            } catch (Exception e) {
                System.err.println("Could not take screenshot: " + e.getMessage());
            }
        }
    }
    
    @Test
    @Story("Homepage Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can navigate to the demo homepage")
    public void testHomepageNavigation() {
        page.navigate(BASE_URL);
        page.waitForLoadState();
        
        String title = page.title();
        assertTrue(title.contains("User Management"), "Page title should contain 'User Management'");
        
        Allure.step("Homepage loaded successfully with title: " + title);
    }
    
    @Test
    @Story("Page Elements")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that key page elements are visible")
    public void testPageElements() {
        page.navigate(BASE_URL);
        page.waitForLoadState();
        
        // Check for user cards container
        page.waitForSelector("#user-cards", new Page.WaitForSelectorOptions().setTimeout(5000));
        assertTrue(page.isVisible("#user-cards"), "User cards container should be visible");
        
        // Check for add user form
        assertTrue(page.isVisible("#add-user-form"), "Add user form should be visible");
        
        Allure.step("All key page elements are visible");
    }
    
    @Test
    @Story("User List Display")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that user list is displayed")
    public void testUserListDisplay() {
        page.navigate(BASE_URL);
        page.waitForLoadState();
        
        // Wait for users to load
        page.waitForSelector(".user-card", new Page.WaitForSelectorOptions().setTimeout(5000));
        
        int userCount = page.locator(".user-card").count();
        assertTrue(userCount > 0, "Should display at least one user");
        
        Allure.step("User list displayed with " + userCount + " users");
    }
    
    @Test
    @Story("Responsive Design")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify mobile viewport rendering")
    public void testMobileViewport() {
        // Set mobile viewport
        page.setViewportSize(375, 667);
        page.navigate(BASE_URL);
        page.waitForLoadState();
        
        // Check if main content is visible
        assertTrue(page.isVisible("h1"), "Page header should be visible on mobile");
        assertTrue(page.isVisible("#user-cards"), "User cards should be visible on mobile");
        
        Allure.step("Mobile viewport rendered correctly");
    }
    
    @Test(groups = {"smoke"})
    @Story("Page Load Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify page loads within acceptable time")
    public void testPageLoadPerformance() {
        long startTime = System.currentTimeMillis();
        
        page.navigate(BASE_URL);
        page.waitForLoadState();
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        assertTrue(loadTime < 5000, "Page should load in less than 5 seconds");
        
        Allure.step("Page loaded in " + loadTime + "ms");
    }
}
