package com.example.tests.ui;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.testing.framework.ui.utils.BrowserManager;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DemoUserUiTest {
    
    private Page page;
    private static final String BASE_URL = "http://localhost:3000";
    
    @BeforeClass
    public void setup() {
        BrowserManager.initializeBrowser();
        page = BrowserManager.getPage();
        System.out.println("Testing Demo UI at: " + BASE_URL);
    }
    
    @Test(priority = 1, description = "Load demo application homepage")
    public void testLoadHomePage() {
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        
        // Wait for the main heading
        page.waitForSelector("h1", new Page.WaitForSelectorOptions().setTimeout(5000));
        String heading = page.locator("h1").first().textContent();
        System.out.println("Page heading: " + heading);
        Assert.assertTrue(heading.contains("User Management"), "Page should have User Management heading");
        
        System.out.println("✅ Successfully loaded demo application");
    }
    
    @Test(priority = 2, description = "Verify users are displayed")
    public void testUsersDisplayed() {
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        
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
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        
        // Wait for form to be ready
        page.waitForSelector("input[placeholder='Enter name']", new Page.WaitForSelectorOptions().setTimeout(5000));
        
        // Fill in the form
        page.fill("input[placeholder='Enter name']", "UI Test User");
        page.fill("input[placeholder='Enter email']", "uitest@example.com");
        page.fill("input[placeholder='Enter role']", "QA Engineer");
        
        // Submit the form
        page.click("button:has-text('Add User')");
        
        // Wait for the new user to appear
        page.waitForSelector(".user-card:has-text('UI Test User')", new Page.WaitForSelectorOptions().setTimeout(5000));
        
        System.out.println("✅ Successfully added new user through UI");
    }
    
    @Test(priority = 4, description = "Delete a user through UI")
    public void testDeleteUser() {
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForSelector(".user-card", new Page.WaitForSelectorOptions().setTimeout(5000));
        
        // Get initial count
        int initialCount = page.locator(".user-card").count();
        System.out.println("Initial user count: " + initialCount);
        
        // Click the first delete button
        page.locator(".user-card .delete-btn").first().click();
        
        // Wait for deletion to complete
        page.waitForTimeout(1000);
        
        // Verify count decreased
        int finalCount = page.locator(".user-card").count();
        System.out.println("Final user count: " + finalCount);
        Assert.assertEquals(finalCount, initialCount - 1, "User count should decrease by 1");
        
        System.out.println("✅ Successfully deleted user through UI");
    }
    
    @AfterClass
    public void tearDown() {
        BrowserManager.closeBrowser();
    }
}
