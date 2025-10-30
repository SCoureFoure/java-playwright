package com.testing.framework.setup;

import com.microsoft.playwright.*;

/**
 * Utility class to install Playwright browsers
 * Run this before running UI tests for the first time
 */
public class InstallBrowsers {
    
    public static void main(String[] args) {
        System.out.println("Installing Playwright browsers...");
        
        try {
            // Create playwright instance which will trigger browser download if needed
            Playwright playwright = Playwright.create();
            
            System.out.println("✅ Playwright initialized successfully!");
            System.out.println("Launching Chromium to verify installation...");
            
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            System.out.println("✅ Chromium browser launched successfully!");
            
            browser.close();
            playwright.close();
            
            System.out.println("\n🎉 All browsers installed and verified!");
            System.out.println("You can now run your UI tests with: mvn test");
            
        } catch (Exception e) {
            System.err.println("❌ Error installing browsers: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
