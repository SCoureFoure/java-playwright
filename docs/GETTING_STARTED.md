# Getting Started Guide

Welcome to the API Automation Testing Framework! This guide will help you get up and running quickly.

## Quick Start (10 minutes)

### 1. Prerequisites Check
```bash
# Check Java version (need 17+)
java -version
# Expected: Java 17 or higher (tested with Java 21.0.7)

# Check if Maven is installed
mvn -version

# If Maven is not installed, install it:
brew install maven
# This installs Maven 3.9.11 (takes ~1 minute)
```

### 2. Clone and Setup
```bash
# Clone the repository
git clone <your-repo-url>
cd java-playwright

# Install dependencies and compile code
mvn clean install -DskipTests
# This downloads 50+ dependencies and compiles the framework
# Takes ~10-20 seconds with good internet connection
# Expected output: BUILD SUCCESS

# Install Playwright browsers (required for UI tests)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
# This downloads Chromium, Firefox, and WebKit browsers
# Takes ~2-3 minutes depending on internet speed
```

### 3. Run Your First Test

**⚠️ Important Setup Note**: Before running tests, you need to configure the TestNG suite in pom.xml.

The Maven Surefire plugin needs explicit configuration to find TestNG tests. Make sure your `pom.xml` has:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

Then run tests:
```bash
# Run all tests
mvn clean test

# Run smoke tests (fastest)
mvn clean test -Dgroups=smoke

# Or use the convenience script
chmod +x run-tests.sh
./run-tests.sh -s smoke
```

### 4. View Report
```bash
# Generate and view Allure report
mvn allure:serve
# This opens the report in your default browser
```

## Detailed Setup

### Environment Setup

#### Option 1: Using Local Java
1. Install Java 17 or higher
2. Install Maven 3.6+
3. Follow Quick Start steps above

#### Option 2: Using Docker
```bash
# Build Docker image
docker build -t api-automation-framework .

# Run tests in Docker
docker run --rm api-automation-framework

# Run with custom environment
docker run --rm -e TEST_ENV=staging api-automation-framework
```

### IDE Setup

#### IntelliJ IDEA
1. Open project: `File → Open → Select java-playwright folder`
2. Maven will auto-import dependencies
3. Install plugins:
   - TestNG
   - Playwright
   - Allure

#### VS Code
1. Open folder: `File → Open Folder → Select java-playwright`
2. Install extensions:
   - Extension Pack for Java
   - TestNG Runner
   - Playwright Test for VS Code

## Project Structure

```
java-playwright/
├── src/
│   ├── main/java/               # Framework code
│   │   └── com/testing/framework/
│   │       ├── api/             # API utilities
│   │       ├── ui/              # UI utilities
│   │       ├── core/            # Core framework
│   │       └── utils/           # Common utilities
│   └── test/                    # Test code
│       ├── java/                # Test classes
│       └── resources/           # Test resources
│           ├── config/          # Environment configs
│           ├── testdata/        # Test data
│           └── testng.xml       # TestNG suite
├── docs/                        # Documentation
├── .gitlab-ci.yml              # CI/CD pipeline
├── pom.xml                     # Maven config
└── README.md                   # Main documentation
```

## Writing Your First Test

### API Test Example

Create a new file: `src/test/java/com/testing/framework/api/functional/MyFirstApiTest.java`

```java
package com.testing.framework.api.functional;

import com.testing.framework.api.client.BaseApiClient;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("My API Tests")
@Feature("User API")
public class MyFirstApiTest extends BaseApiClient {
    
    @Test
    @Description("Verify user list retrieval")
    public void testGetUsers() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
        
        logger.info("Test passed!");
    }
}
```

### UI Test Example

Create a new file: `src/test/java/com/testing/framework/ui/functional/MyFirstUiTest.java`

```java
package com.testing.framework.ui.functional;

import com.microsoft.playwright.Page;
import com.testing.framework.ui.utils.BrowserManager;
import io.qameta.allure.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

@Epic("My UI Tests")
@Feature("Homepage")
public class MyFirstUiTest {
    
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
    
    @Test
    @Description("Verify homepage loads")
    public void testHomepage() {
        page.navigate("https://example.com");
        String title = page.title();
        
        assertTrue(title.contains("Example"), 
            "Title should contain 'Example'");
        
        logger.info("Test passed!");
    }
}
```

## Running Tests

### Basic Commands

```bash
# Run all tests
mvn clean test

# Run specific suite
mvn clean test -Dtest.suite=api
mvn clean test -Dtest.suite=ui
mvn clean test -Dtest.suite=smoke

# Run with specific environment
mvn clean test -Denv=staging

# Run with parallel execution
mvn clean test -Dparallel=methods -DthreadCount=4

# Run specific test class
mvn clean test -Dtest=SampleApiTest

# Run specific test method
mvn clean test -Dtest=SampleApiTest#testGetUsers
```

### Using the Run Script

```bash
# Make script executable (first time only)
chmod +x run-tests.sh

# Run with default settings (dev environment, all tests)
./run-tests.sh

# Run API tests in staging
./run-tests.sh -e staging -s api

# Run UI tests in parallel with headless mode
./run-tests.sh -s ui -p true -h true

# Run smoke tests in production (headless)
./run-tests.sh -e prod -s smoke -h true
```

## Configuration

### Updating Environment Settings

Edit files in `src/test/resources/config/`:

**dev.properties:**
```properties
api.base.url=https://api.dev.example.com
ui.base.url=https://dev.example.com
browser=chromium
headless=false
```

### Adding New Environments

1. Create new properties file: `src/test/resources/config/qa.properties`
2. Add configuration values
3. Run with: `mvn test -Denv=qa`

## Reporting

### Local Allure Reports

```bash
# Generate and serve report
mvn allure:serve

# Just generate report (saved to target/allure-report/)
mvn allure:report
```

### CI/CD Reports

Reports are automatically generated in GitLab CI/CD and published to GitLab Pages.

Access at: `https://your-gitlab-domain/your-group/your-project/-/pages`

## Debugging

### Enable Verbose Logging

Update `src/test/resources/logback-test.xml`:
```xml
<logger name="com.testing.framework" level="DEBUG"/>
```

### UI Test Debugging

```java
// Take screenshot
BrowserManager.takeScreenshot("debug-screenshot");

// Slow down execution
page.waitForTimeout(3000); // 3 seconds

// Run in headed mode
# Edit config file: headless=false
```

### API Test Debugging

```java
// Log full request/response
Response response = given()
    .spec(requestSpec)
    .log().all()  // Log request
.when()
    .get("/users")
.then()
    .log().all()  // Log response
    .extract().response();
```

## Common Tasks

### Adding Dependencies

Edit `pom.xml`:
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library-name</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then run: `mvn clean install`

### Creating Page Objects

```java
public class LoginPage extends BasePage {
    private static final String USERNAME = "#username";
    private static final String PASSWORD = "#password";
    private static final String SUBMIT = "button[type='submit']";
    
    public LoginPage(Page page) {
        super(page);
    }
    
    public void login(String user, String pass) {
        page.fill(USERNAME, user);
        page.fill(PASSWORD, pass);
        page.click(SUBMIT);
    }
}
```

### Creating API Models

```java
public class User {
    private String id;
    private String name;
    private String email;
    
    // Getters and setters
    // Constructor
}
```

## Troubleshooting

### Issue: Tests run: 0 (TestNG tests not executing)
**Problem:** Maven Surefire auto-detecting JUnit instead of TestNG
```
Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
Tests run: 0, Failures: 0, Errors: 0, Skipped: 0
```

**Solution:** Add explicit TestNG configuration to pom.xml:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

Then run: `mvn clean test`

### Issue: Maven command not found
**Problem:** Maven not installed on system

**Solution:**
```bash
# macOS
brew install maven

# Verify installation
mvn -version
# Should show Maven 3.9.11 or higher
```

### Issue: Tests fail with "Browser not found" or "Playwright not installed"
**Solution:**
```bash
# Install all browsers with system dependencies
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Or install specific browser
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"

# Verify installation
ls -la ~/Library/Caches/ms-playwright/
```

### Issue: Compilation errors or dependency issues
**Solution:**
```bash
# Clean and reinstall all dependencies
mvn clean install -DskipTests

# If issues persist, clear local Maven cache
rm -rf ~/.m2/repository/com/testing/framework
mvn clean install -DskipTests
```

### Issue: "Port already in use"
**Solution:** Kill the process using the port or change the port in configuration

### Issue: Tests are slow
**Solutions:**
- Enable parallel execution: `mvn test -Dparallel=methods -DthreadCount=4`
- Use headless mode: `mvn test -Dheadless=true`
- Optimize waits and timeouts in test code
- Run specific test suites instead of all tests

### Issue: Allure report not generating
**Solution:**
```bash
# Clean and regenerate
rm -rf target/allure-results target/allure-report
mvn clean test allure:report

# Or serve directly (opens in browser)
mvn allure:serve
```

### Issue: Deprecated API warnings during compilation
**Problem:** Some Playwright APIs show deprecation warnings

**Impact:** Low - warnings only, code still works  
**Solution:** Will be addressed in future framework updates  
**Note:** These warnings don't affect test execution

## Best Practices

### Test Writing
✅ Keep tests independent and isolated  
✅ Use meaningful test names  
✅ Add @Description annotations  
✅ Use proper assertions  
✅ Handle test data properly  

### Framework Usage
✅ Extend base classes (BaseApiClient, BasePage)  
✅ Use ConfigManager for configuration  
✅ Add Allure annotations for better reporting  
✅ Log important test steps  
✅ Capture screenshots on failures  

### Code Organization
✅ Follow package structure  
✅ Keep related tests together  
✅ Use constants for selectors/endpoints  
✅ Document complex logic  
✅ Review code before committing  

## Next Steps

1. ✅ Complete this Getting Started guide
2. 📖 Read [Architecture Documentation](docs/ARCHITECTURE.md)
3. 🔄 Review [Migration Guide](docs/MIGRATION_GUIDE.md)
4. 📝 Write your first test
5. 🚀 Set up GitLab CI/CD
6. 👥 Share with team

## Resources

- [RestAssured Documentation](https://rest-assured.io/)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [TestNG Documentation](https://testng.org/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [GitLab CI/CD](https://docs.gitlab.com/ee/ci/)

## Getting Help

- Check documentation in `/docs` folder
- Review example tests in `/src/test/java`
- Contact Test Automation team
- Create an issue in GitLab

## Contributing

1. Create a feature branch
2. Write/update tests
3. Update documentation
4. Run all tests locally
5. Submit merge request

---

Happy Testing! 🎉
