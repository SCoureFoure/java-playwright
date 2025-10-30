package com.example.tests.ui;

import com.microsoft.playwright.*;
import com.testing.framework.core.config.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DemoUserUiTest {
    
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private String baseUrl;
    
    @BeforeClass
    public void setup() {
        baseUrl = ConfigManager.getInstance().getProperty("ui.base.url");
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
        
        System.out.println("Testing Demo UI at: " + baseUrl);
    }
    
    @Test(priority = 1, description = "Load demo application homepage")
    public void testLoadHomePage() {
        page.navigate(baseUrl);
        page.waitForLoadState();
        
        // Check for the main heading
        String heading = page.locator("h1").first().textContent();
        System.out.println("Page heading: " + heading);
        Assert.assertTrue(heading.contains("User Management"), "Page should have User Management heading");
        
        System.out.println("✅ Successfully loaded demo application");
    }
    
    @Test(priority = 2, description = "Verify users are displayed")
    public void testUsersDisplayed() {
        page.navigate(baseUrl);
        page.waitForLoadState();
        
        // Wait for user list to load
        page.waitForSelector(".user-card", new Page.WaitForSelectorOptions().setTimeout(5000));
        
        // Count user cards
        int userCount = page.locator(".user-card").count();
        System.out.println("Found " + userCount + " user cards on the page");
        Assert.assertTrue(userCount > 0, "Should display at least one user");
        
        System.out.println("✅ Users are displayed correctly");
    }
    
    @Test(priority = 3, description = "Add a new user through UI")
    public void testAddNewUser() {
        page.navigate(baseUrl);
        page.waitForLoadState();
        
        // Fill in the form
        page.fill("input[placeholder='Enter name']", "UI Test User");
        page.fill("input[placeholder='Enter email']", "uitest@example.com");
        page.fill("input[placeholder='Enter role']", "QA Engineer");
        
        // Submit the form
        page.click("button:has-text('Add User')");
        
        // Wait a bit for the API call
        page.waitForTimeout(1000);
        
        // Verify the new user appears in the list
        page.waitForSelector(".user-card:has-text('UI Test User')");
        
        System.out.println("✅ Successfully added new user through UI");
    }
    
    @Test(priority = 4, description = "Delete a user through UI")
    public void testDeleteUser() {
        page.navigate(baseUrl);
        page.waitForLoadState();
        page.waitForSelector(".user-card");
        
        // Get initial count
        int initialCount = page.locator(".user-card").count();
        System.out.println("Initial user count: " + initialCount);
        
        // Click the first delete button
        page.locator(".user-card .delete-btn").first().click();
        
        // Wait for deletion
        page.waitForTimeout(1000);
        
        // Verify count decreased
        int finalCount = page.locator(".user-card").count();
        System.out.println("Final user count: " + finalCount);
        Assert.assertEquals(finalCount, initialCount - 1, "User count should decrease by 1");
        
        System.out.println("✅ Successfully deleted user through UI");
    }
    
    @AfterClass
    public void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
    }
}
