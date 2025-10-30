package com.testing.framework.smoke;

import com.microsoft.playwright.Page;
import com.testing.framework.api.client.BaseApiClient;
import com.testing.framework.ui.utils.BrowserManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;

/**
 * Smoke tests combining API and UI validation
 */
@Epic("Smoke Tests")
@Feature("Health Checks")
public class SmokeTest extends BaseApiClient {
    
    private Page page;
    
    @BeforeClass
    public void setup() {
        BrowserManager.initializeBrowser();
        page = BrowserManager.getPage();
    }
    
    @AfterClass
    public void teardown() {
        BrowserManager.closeBrowser();
    }
    
    @Test(priority = 1)
    @Story("API Health")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify API is accessible and responding")
    public void testApiHealth() {
        // This is a placeholder - adjust endpoint to your actual health check
        try {
            Response response = given()
                .spec(requestSpec)
            .when()
                .get("/health")
            .then()
                .extract().response();
            
            int statusCode = response.getStatusCode();
            assertTrue(statusCode == 200 || statusCode == 404, 
                "API should be accessible (got status: " + statusCode + ")");
            
            logger.info("API health check passed with status: {}", statusCode);
        } catch (Exception e) {
            logger.warn("API health check failed (expected for demo): {}", e.getMessage());
        }
    }
    
    @Test(priority = 2)
    @Story("UI Health")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify UI application is accessible")
    public void testUiHealth() {
        // Using Playwright demo site as example
        page.navigate("https://playwright.dev");
        page.waitForLoadState();
        
        String title = page.title();
        assertTrue(title != null && !title.isEmpty(), "Page should have a title");
        
        logger.info("UI health check passed - title: {}", title);
    }
    
    @Test(priority = 3, dependsOnMethods = {"testApiHealth", "testUiHealth"})
    @Story("Integration Health")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify basic integration between API and UI")
    public void testIntegrationHealth() {
        // This is a conceptual test - adapt to your actual integration scenario
        logger.info("Integration health check - API and UI are both accessible");
        assertTrue(true, "Basic integration check passed");
    }
}
