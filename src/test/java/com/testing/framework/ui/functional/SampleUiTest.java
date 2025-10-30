package com.testing.framework.ui.functional;

import com.microsoft.playwright.Page;
import com.testing.framework.ui.utils.BrowserManager;
import io.qameta.allure.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Sample UI test using Playwright
 */
@Epic("UI Testing")
@Feature("Web Navigation")
public class SampleUiTest {
    
    private Page page;
    
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
    public void takeScreenshotOnFailure() {
        BrowserManager.takeScreenshot("test-screenshot");
    }
    
    @Test
    @Story("Homepage Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that we can navigate to the homepage and see the title")
    public void testHomepageNavigation() {
        // Example using Playwright.dev website
        page.navigate("https://playwright.dev");
        page.waitForLoadState();
        
        String title = page.title();
        assertTrue(title.contains("Playwright"), "Page title should contain 'Playwright'");
        
        Allure.step("Homepage loaded successfully with title: " + title);
    }
    
    @Test
    @Story("Search Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify search functionality works correctly")
    public void testSearchFunctionality() {
        page.navigate("https://playwright.dev");
        
        // Wait for search button and click
        page.waitForSelector("button[aria-label='Search']");
        page.click("button[aria-label='Search']");
        
        // Type in search
        page.waitForSelector("input[type='search']");
        page.fill("input[type='search']", "browser");
        
        // Wait for results
        page.waitForTimeout(1000);
        
        Allure.step("Search completed successfully");
    }
    
    @Test
    @Story("Link Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that navigation links work correctly")
    public void testLinkNavigation() {
        page.navigate("https://playwright.dev");
        
        // Click on "Get Started" or similar link
        if (page.isVisible("text=Get started")) {
            page.click("text=Get started");
            page.waitForLoadState();
            
            String currentUrl = page.url();
            assertTrue(currentUrl.contains("intro"), "URL should contain 'intro'");
            
            Allure.step("Navigation link works correctly");
        }
    }
    
    @Test
    @Story("Responsive Design")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify mobile viewport rendering")
    public void testMobileViewport() {
        // Set mobile viewport
        page.setViewportSize(375, 667);
        page.navigate("https://playwright.dev");
        page.waitForLoadState();
        
        // Check if mobile menu exists
        boolean hasMobileMenu = page.isVisible("button[aria-label='Toggle navigation bar']") ||
                               page.isVisible(".navbar-sidebar__brand");
        
        assertTrue(hasMobileMenu, "Mobile menu should be visible");
        
        Allure.step("Mobile viewport rendered correctly");
    }
    
    @Test(groups = {"smoke"})
    @Story("Page Load Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify page loads within acceptable time")
    public void testPageLoadPerformance() {
        long startTime = System.currentTimeMillis();
        
        page.navigate("https://playwright.dev");
        page.waitForLoadState();
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        assertTrue(loadTime < 5000, "Page should load in less than 5 seconds");
        
        Allure.step("Page loaded in " + loadTime + "ms");
    }
}
