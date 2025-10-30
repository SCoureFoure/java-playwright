# Quick Reference Guide

## 🚀 Essential Commands

### First-Time Setup
```bash
# 1. Check Java version
java -version
# Need: Java 17+ (tested with Java 21.0.7)

# 2. Install Maven (if not present)
brew install maven
# Installs: Maven 3.9.11

# 3. Install dependencies and compile
mvn clean install -DskipTests
# Downloads 50+ dependencies, compiles code
# Time: ~10-20 seconds
# Expected: BUILD SUCCESS

# 4. Install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
# Downloads: Chromium, Firefox, WebKit
# Time: 2-3 minutes
```

### Running Tests
```bash
# Run all tests
mvn clean test

# Run specific suite
mvn test -Dgroups=smoke        # Smoke tests
mvn test -Dgroups=api          # API tests only
mvn test -Dgroups=ui           # UI tests only

# Run with specific environment
mvn test -Denv=dev             # Development
mvn test -Denv=staging         # Staging
mvn test -Denv=prod            # Production

# Run with specific browser
mvn test -Dbrowser=chromium    # Chrome/Chromium
mvn test -Dbrowser=firefox     # Firefox
mvn test -Dbrowser=webkit      # Safari/WebKit

# Run in headless mode (no browser window)
mvn test -Dheadless=true

# Run with parallel execution
mvn test -Dparallel=methods -DthreadCount=4

# Run specific test class
mvn test -Dtest=SampleApiTest

# Run specific test method
mvn test -Dtest=SampleApiTest#testGetUsers
```

### Using Run Script
```bash
# Make script executable (first time only)
chmod +x run-tests.sh

# Run with default settings (dev, all tests)
./run-tests.sh

# Run API tests in staging
./run-tests.sh -e staging -s api

# Run smoke tests in production (headless)
./run-tests.sh -e prod -s smoke -h true

# Run UI tests with parallel execution
./run-tests.sh -s ui -p true -t 4

# Available options:
# -e <env>      Environment: dev, staging, prod (default: dev)
# -s <suite>    Suite: api, ui, smoke, all (default: all)
# -h <bool>     Headless: true, false (default: false)
# -p <bool>     Parallel: true, false (default: false)
# -t <num>      Thread count (default: 4)
```

### Reporting
```bash
# Generate and open report in browser
mvn allure:serve

# Generate report (saved to target/allure-report/)
mvn allure:report

# Open existing report
open target/allure-report/index.html

# Clean old reports
rm -rf target/allure-results target/allure-report
```

### Docker Commands
```bash
# Build Docker image
docker build -t api-automation-framework .

# Run all tests in Docker
docker run --rm api-automation-framework

# Run with specific environment
docker run --rm -e TEST_ENV=staging api-automation-framework

# Run specific suite
docker run --rm -e TEST_SUITE=api api-automation-framework

# Run with custom thread count
docker run --rm -e THREAD_COUNT=2 api-automation-framework
```

## 🛠️ Maintenance Commands

### Cleaning
```bash
# Clean build artifacts
mvn clean

# Clean and rebuild
mvn clean install -DskipTests

# Clean Maven cache (if dependency issues)
rm -rf ~/.m2/repository/com/testing/framework
mvn clean install -DskipTests

# Clean test results
rm -rf target/surefire-reports target/allure-results
```

### Dependency Management
```bash
# Update dependencies
mvn versions:display-dependency-updates

# Display dependency tree
mvn dependency:tree

# Download sources and javadocs
mvn dependency:resolve -Dclassifier=sources
mvn dependency:resolve -Dclassifier=javadoc
```

### Code Quality
```bash
# Compile only
mvn compile

# Compile tests
mvn test-compile

# Check for compilation errors
mvn verify -DskipTests
```

## 📝 Configuration Quick Reference

### Environment Files
```
src/test/resources/config/
├── dev.properties       # Development environment
├── staging.properties   # Staging environment
└── prod.properties      # Production environment
```

### Key Properties
```properties
# API Configuration
api.base.url=https://api.example.com

# UI Configuration
ui.base.url=https://example.com
browser=chromium              # chromium, firefox, webkit
headless=false               # true, false

# Test Configuration
parallel.enabled=true
thread.count=4
timeout.seconds=30
```

### System Property Overrides
```bash
# Override any property via command line
mvn test -Dapi.base.url=https://custom-api.com
mvn test -Dbrowser=firefox
mvn test -Dheadless=true
mvn test -Dthread.count=8
```

## 🎯 Common Workflows

### Daily Testing Workflow
```bash
# 1. Pull latest code
git pull origin main

# 2. Update dependencies (if pom.xml changed)
mvn clean install -DskipTests

# 3. Run smoke tests
./run-tests.sh -s smoke

# 4. If smoke passes, run full suite
mvn test

# 5. View report
mvn allure:serve
```

### Before Committing Changes
```bash
# 1. Run all tests
mvn clean test

# 2. Check for compilation issues
mvn verify -DskipTests

# 3. Review test results
mvn allure:serve

# 4. Commit if all pass
git add .
git commit -m "Your message"
git push
```

### CI/CD Pipeline Trigger
```bash
# Push to trigger pipeline
git push origin feature-branch

# View pipeline
# Go to: GitLab → CI/CD → Pipelines

# Download artifacts
# Go to: Pipeline → Job → Browse artifacts
```

### Debugging Failed Tests
```bash
# 1. Run failed test with logging
mvn test -Dtest=FailedTestClass -Dlogback.configurationFile=src/test/resources/logback-test.xml

# 2. Check logs
cat target/logs/test-execution.log

# 3. Review screenshots (for UI tests)
open target/screenshots/

# 4. Run in headed mode (see browser)
mvn test -Dtest=FailedTestClass -Dheadless=false

# 5. Add debug breakpoint and run from IDE
```

## 🔍 Verification Commands

### Check Installation
```bash
# Java
java -version
# Expected: 17+ (tested with 21.0.7)

# Maven
mvn -version
# Expected: 3.6+ (tested with 3.9.11)

# Git
git --version

# Playwright browsers
ls -la ~/Library/Caches/ms-playwright/
# Should show: chromium-*, firefox-*, webkit-*
```

### Verify Build Status
```bash
# Quick verification
mvn verify -DskipTests
# Expected: BUILD SUCCESS

# Check compiled classes
ls -la target/classes/com/testing/framework/
ls -la target/test-classes/com/testing/framework/

# Verify dependencies downloaded
ls -la ~/.m2/repository/io/rest-assured/
ls -la ~/.m2/repository/com/microsoft/playwright/
```

### Test Execution Verification
```bash
# Dry run (compile but don't execute)
mvn test-compile

# Count test methods
grep -r "@Test" src/test/java/ | wc -l

# List test classes
find src/test/java -name "*Test.java"

# Check TestNG suite configuration
cat src/test/resources/testng.xml
```

## ⚠️ Troubleshooting Quick Fixes

### Tests Run: 0
```bash
# Problem: Maven Surefire not finding TestNG tests
# Fix: Add to pom.xml:
# <suiteXmlFiles>
#   <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
# </suiteXmlFiles>
# Then: mvn clean test
```

### Maven Not Found
```bash
brew install maven
```

### Browser Not Found
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

### Dependency Issues
```bash
rm -rf ~/.m2/repository/com/testing/framework
mvn clean install -DskipTests
```

### Port Already in Use
```bash
# Find process using port 8080
lsof -ti:8080
# Kill it
kill -9 $(lsof -ti:8080)
```

## 📊 Useful Commands for Reporting

### Test Results Location
```bash
# Surefire reports (XML/TXT)
open target/surefire-reports/

# Allure results (JSON)
open target/allure-results/

# Generated Allure report (HTML)
open target/allure-report/index.html

# Logs
cat target/logs/test-execution.log

# Screenshots (if UI tests failed)
open target/screenshots/
```

### CI/CD Artifact Paths
```
Pipeline Artifacts:
├── test-results/          # TestNG/Surefire XML reports
├── allure-results/        # Raw Allure data
├── allure-report/         # Generated HTML report
├── logs/                  # Test execution logs
└── screenshots/           # Failure screenshots
```

## 🎓 Test Writing Quick Reference

### API Test Template
```java
@Test
@Description("Your test description")
public void testApiEndpoint() {
    given()
        .spec(requestSpec)
    .when()
        .get("/endpoint")
    .then()
        .statusCode(200)
        .body("field", equalTo("value"));
}
```

### UI Test Template
```java
@Test
@Description("Your test description")
public void testUiFeature() {
    page.navigate("https://example.com");
    page.click("button#submit");
    assertTrue(page.isVisible("div.success"));
}
```

### Test Annotations
```java
@Epic("Epic Name")        // High-level feature
@Feature("Feature Name")  // Specific feature
@Story("Story Name")      // User story
@Severity(CRITICAL)       // Test importance
@Description("Details")   // Test description
@Test(groups = "smoke")   // Test group
```

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| README.md | Main overview |
| GETTING_STARTED.md | Detailed setup guide |
| ARCHITECTURE.md | Framework design |
| MIGRATION_GUIDE.md | Jenkins → GitLab |
| IMPLEMENTATION_SUMMARY.md | Technical details |
| PROJECT_SUMMARY.md | Complete readout |
| QUICK_REFERENCE.md | This file |

## 🔗 External Resources

- [RestAssured Docs](https://rest-assured.io/)
- [Playwright Java](https://playwright.dev/java/)
- [TestNG Guide](https://testng.org/doc/documentation-main.html)
- [Allure Reports](https://docs.qameta.io/allure/)
- [GitLab CI/CD](https://docs.gitlab.com/ee/ci/)
- [Maven Guide](https://maven.apache.org/guides/)

---

**Last Updated**: October 30, 2025  
**Framework Version**: 1.0.0-SNAPSHOT  
**Tested With**: Java 21.0.7, Maven 3.9.11
