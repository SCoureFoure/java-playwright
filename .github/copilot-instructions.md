# GitHub Copilot Instructions for java-playwright

## Project Overview
This is an API Automation Testing Framework combining:
- **Application**: Spring Boot demo REST API for user management
- **Testing Framework**: RestAssured (API) + Playwright (UI) + TestNG

## Project Structure
```
src/main/java/com/
├── demo/                    # Demo Spring Boot application
│   ├── DemoApplication.java # Main Spring Boot app
│   ├── controller/          # REST controllers
│   ├── model/               # Domain models (User, etc.)
│   └── service/             # Business logic
└── testing/framework/       # Reusable testing framework
    ├── api/                 # RestAssured utilities
    ├── ui/                  # Playwright utilities
    └── core/                # Config, logging, listeners

src/test/java/com/
├── demo/tests/              # Application-specific tests
│   ├── api/                 # API tests for demo app
│   └── ui/                  # UI tests for demo app
└── testing/framework/       # Framework example tests
    ├── api/functional/      # API test samples
    ├── ui/functional/       # UI test samples
    └── smoke/               # Smoke test suite
```

## Coding Standards

### Java Application Code (src/main/java/com/demo/)
- Use Spring Boot annotations (@RestController, @Service, etc.)
- Follow REST API best practices
- Use proper HTTP status codes (200, 201, 204, 404, etc.)
- Include JavaDoc for public methods
- Use constructor injection for dependencies

### Testing Framework Code (src/main/java/com/testing/framework/)
- Create reusable, framework-level utilities
- Use Page Object Model pattern for UI tests
- Implement builder patterns for test data
- Add comprehensive logging with SLF4J
- Use Allure annotations for reporting

### Test Code (src/test/java/)
- Use TestNG annotations (@Test, @BeforeMethod, etc.)
- Add Allure annotations (@Epic, @Feature, @Story, @Severity)
- Make tests independent and isolated
- Use descriptive test method names (testCreateUser_WithValidData_ReturnsCreatedUser)
- Include assertions with meaningful messages
- Group tests appropriately (@Test(groups = {"smoke", "api"}))

## Key Dependencies & Patterns

### API Testing (RestAssured)
```java
// Use BaseApiClient for all API calls
BaseApiClient client = new BaseApiClient();
Response response = client.get("/users");
response.then()
    .statusCode(200)
    .body("size()", greaterThan(0));
```

### UI Testing (Playwright)
```java
// Use BasePage for Page Object Model
public class LoginPage extends BasePage {
    private final String loginButton = "#login-btn";
    
    public void login(String username, String password) {
        page.fill("#username", username);
        page.fill("#password", password);
        page.click(loginButton);
    }
}
```

### Test Structure
```java
@Epic("User Management")
@Feature("User CRUD Operations")
public class UserApiTest {
    
    private BaseApiClient apiClient;
    
    @BeforeMethod
    public void setup() {
        apiClient = new BaseApiClient();
    }
    
    @Test(groups = {"smoke", "api"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify user creation with valid data")
    public void testCreateUser_WithValidData_ReturnsCreatedUser() {
        // Arrange
        User user = new User(null, "John Doe", "john@example.com", "user");
        
        // Act
        Response response = apiClient.post("/users", user);
        
        // Assert
        response.then()
            .statusCode(201)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john@example.com"));
    }
}
```

## Configuration Management

- Use ConfigManager.getInstance() for all configuration
- Environment configs: dev.properties, staging.properties, prod.properties
- Run tests with: mvn test -Denv=staging
- Configure browser: browser=chromium, headless=true/false

## Best Practices

1. **Test Independence**: Each test should be able to run independently
2. **Data Creation**: Create test data in @BeforeMethod or within tests
3. **Clean Up**: Delete created data in @AfterMethod if needed
4. **Assertions**: Use RestAssured's built-in assertions for API, TestNG assertions for others
5. **Logging**: Use logger.info() for important steps, logger.debug() for details
6. **Error Handling**: Handle expected errors gracefully, fail fast on unexpected ones
7. **Documentation**: Add JavaDoc to all public methods and classes

## Common Patterns

### Creating Test Data
```java
private User createTestUser() {
    return new User(null, "Test User", "test@example.com", "user");
}
```

### API Test Pattern
```java
// 1. Create test data
// 2. Make API call
// 3. Verify response status
// 4. Verify response body
// 5. Clean up if needed
```

### UI Test Pattern
```java
// 1. Navigate to page
// 2. Perform actions
// 3. Wait for expected state
// 4. Verify results
// 5. Take screenshot on failure (automatic)
```

## Testing Goals (Milestone 3)
- Migrate existing API and UI tests to this framework
- Integrate with GitLab CI/CD
- Generate Allure reports
- Achieve stable test execution
- Create comprehensive documentation

## When Suggesting Code

- Follow the existing project structure
- Use the established patterns and utilities
- Add appropriate annotations (Spring, TestNG, Allure)
- Include error handling and logging
- Make tests independent and reusable
- Add clear comments for complex logic
- Suggest test improvements for better coverage

## Run Commands
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run all tests
mvn test

# Run specific suite
mvn test -Dgroups=smoke

# Run with environment
mvn test -Denv=staging

# Generate Allure report
mvn allure:serve
```
