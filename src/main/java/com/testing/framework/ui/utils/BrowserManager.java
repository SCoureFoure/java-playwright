package com.testing.framework.ui.utils;

import com.microsoft.playwright.*;
import com.testing.framework.core.config.ConfigManager;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Browser manager for Playwright
 */
public class BrowserManager {
    
    private static final Logger logger = LoggerFactory.getLogger(BrowserManager.class);
    private static final ConfigManager config = ConfigManager.getInstance();
    
    private static ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static ThreadLocal<Page> page = new ThreadLocal<>();
    
    /**
     * Initialize browser
     */
    public static void initializeBrowser() {
        logger.info("Initializing browser: {}", config.getBrowser());
        
        playwright.set(Playwright.create());
        
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(config.isHeadless());
        
        browser.set(getBrowserType().launch(launchOptions));
        
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setRecordVideoDir(Paths.get("target/playwright-videos"));
        
        context.set(browser.get().newContext(contextOptions));
        
        // Enable tracing for debugging
        context.get().tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true)
            .setSources(true));
        
        page.set(context.get().newPage());
        logger.info("Browser initialized successfully");
    }
    
    /**
     * Get browser type based on configuration
     */
    private static BrowserType getBrowserType() {
        String browserName = config.getBrowser().toLowerCase();
        
        return switch (browserName) {
            case "firefox" -> playwright.get().firefox();
            case "webkit", "safari" -> playwright.get().webkit();
            default -> playwright.get().chromium();
        };
    }
    
    /**
     * Get current page
     */
    public static Page getPage() {
        if (page.get() == null) {
            initializeBrowser();
        }
        return page.get();
    }
    
    /**
     * Create new page in same context
     */
    public static Page createNewPage() {
        if (context.get() == null) {
            initializeBrowser();
        }
        return context.get().newPage();
    }
    
    /**
     * Close current page
     */
    public static void closePage() {
        if (page.get() != null) {
            page.get().close();
        }
    }
    
    /**
     * Take screenshot
     */
    public static void takeScreenshot(String name) {
        if (page.get() != null) {
            byte[] screenshot = page.get().screenshot(
                new Page.ScreenshotOptions()
                    .setPath(Paths.get("target/playwright-screenshots/" + name + ".png"))
                    .setFullPage(true)
            );
            
            Allure.addAttachment(name, "image/png", 
                new java.io.ByteArrayInputStream(screenshot), "png");
        }
    }
    
    /**
     * Close browser and cleanup
     */
    public static void closeBrowser() {
        try {
            if (context.get() != null) {
                // Stop tracing and save
                context.get().tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("target/trace.zip")));
            }
            
            if (page.get() != null) {
                page.get().close();
                page.remove();
            }
            
            if (context.get() != null) {
                context.get().close();
                context.remove();
            }
            
            if (browser.get() != null) {
                browser.get().close();
                browser.remove();
            }
            
            if (playwright.get() != null) {
                playwright.get().close();
                playwright.remove();
            }
            
            logger.info("Browser closed successfully");
        } catch (Exception e) {
            logger.error("Error closing browser", e);
        }
    }
}
