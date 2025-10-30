# Framework Architecture

## Overview
This document describes the architecture of the API Automation Testing Framework.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Test Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  API Tests   │  │   UI Tests   │  │ Smoke Tests  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                  Framework Core Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ BaseApiClient│  │  BasePage    │  │ConfigManager │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │BrowserManager│  │TestListener  │  │  Utilities   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                  Library Layer                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ RestAssured  │  │  Playwright  │  │   TestNG     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │   Allure     │  │   Jackson    │                        │
│  └──────────────┘  └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

## Layers Description

### 1. Test Layer
Contains actual test implementations:
- **API Tests**: RESTful API testing using RestAssured
- **UI Tests**: Web UI testing using Playwright
- **Smoke Tests**: Quick validation tests
- **Integration Tests**: Combined API and UI scenarios

### 2. Framework Core Layer

#### Configuration Management
- **ConfigManager**: Centralized configuration management
- Environment-specific property files
- Runtime property override support

#### API Components
- **BaseApiClient**: RestAssured configuration and utilities
- Request/Response logging
- Authentication handling
- JSON schema validation

#### UI Components
- **BasePage**: Page Object Model base class
- Common page operations
- Screenshot capture
- Element interaction utilities

#### Browser Management
- **BrowserManager**: Playwright browser lifecycle
- Multi-browser support (Chromium, Firefox, WebKit)
- Context management
- Video recording

#### Listeners & Reporting
- **TestListener**: TestNG listener for enhanced logging
- Allure integration for rich reporting
- Failure capture and attachment

### 3. Library Layer
Third-party dependencies:
- **RestAssured**: API testing framework
- **Playwright**: Browser automation
- **TestNG**: Test orchestration
- **Allure**: Reporting framework
- **Jackson**: JSON processing

## Design Patterns

### 1. Page Object Model (POM)
```java
public class LoginPage extends BasePage {
    private static final String USERNAME_FIELD = "#username";
    private static final String PASSWORD_FIELD = "#password";
    
    public void login(String username, String password) {
        page.fill(USERNAME_FIELD, username);
        page.fill(PASSWORD_FIELD, password);
        page.click("button[type='submit']");
    }
}
```

### 2. Singleton Pattern
Used in ConfigManager for single instance:
```java
public static ConfigManager getInstance() {
    if (instance == null) {
        synchronized (ConfigManager.class) {
            if (instance == null) {
                instance = new ConfigManager();
            }
        }
    }
    return instance;
}
```

### 3. Builder Pattern
Used in RestAssured request specification:
```java
requestSpec = new RequestSpecBuilder()
    .setContentType(ContentType.JSON)
    .addFilter(new AllureRestAssured())
    .build();
```

### 4. Factory Pattern
Browser creation based on configuration:
```java
private static BrowserType getBrowserType() {
    return switch (config.getBrowser()) {
        case "firefox" -> playwright.get().firefox();
        case "webkit" -> playwright.get().webkit();
        default -> playwright.get().chromium();
    };
}
```

## Configuration Management

### Environment Configuration
```
config/
├── dev.properties      # Development
├── staging.properties  # Staging
└── prod.properties     # Production
```

### Runtime Override
```bash
mvn test -Denv=staging -Dapi.base.url=https://custom-url.com
```

## Test Organization

```
src/test/java/
└── com/testing/framework/
    ├── api/
    │   ├── functional/    # Functional API tests
    │   ├── integration/   # Integration tests
    │   └── contract/      # Contract tests
    ├── ui/
    │   ├── functional/    # Functional UI tests
    │   └── e2e/          # End-to-end tests
    └── smoke/            # Smoke tests
```

## Reporting Architecture

### Allure Reports
1. Tests generate results in `target/allure-results/`
2. Maven plugin generates HTML report
3. GitLab CI publishes to GitLab Pages

### Report Contents
- Test execution overview
- Pass/Fail statistics
- Timeline view
- Test case history
- Screenshots & videos
- Request/Response logs
- Error stack traces

## CI/CD Integration

### Pipeline Stages
```
Build → Test (API/UI/Smoke) → Report → Deploy
```

### Parallel Execution
- Multiple test classes in parallel
- Thread-safe browser instances
- Isolated test data

## Extensibility

### Adding New Test Types
1. Create new package under `src/test/java/`
2. Extend appropriate base class
3. Add to `testng.xml`
4. Update GitLab CI pipeline

### Adding New API Clients
1. Extend `BaseApiClient`
2. Add endpoint-specific methods
3. Create corresponding test class

### Adding New Page Objects
1. Extend `BasePage`
2. Define locators as constants
3. Implement page-specific methods

## Best Practices

### API Testing
- Use request/response models
- Validate JSON schemas
- Test error scenarios
- Verify status codes and headers

### UI Testing
- Follow POM pattern
- Use meaningful selectors
- Implement explicit waits
- Capture failures with screenshots

### Test Data
- Store in separate files
- Use TestNG data providers
- Environment-specific test data
- No hardcoded credentials

### Maintenance
- Regular dependency updates
- Code review for new tests
- Refactor common code
- Document complex scenarios

## Security Considerations

- Credentials in CI/CD variables only
- Masked sensitive data in logs
- Secure test data management
- No credentials in code

## Performance Optimization

- Parallel test execution
- Browser context reuse
- Dependency caching
- Selective test execution

## Troubleshooting

### Common Issues
1. **Browser not found**: Run Playwright install
2. **Tests flaky**: Add proper waits
3. **Slow execution**: Enable parallel mode
4. **Report not generated**: Check Allure version

## Future Enhancements

- [ ] Contract testing with Pact
- [ ] Performance testing with JMeter
- [ ] API mocking with WireMock
- [ ] Visual regression testing
- [ ] Accessibility testing
- [ ] Load testing integration
- [ ] Database validation utilities
- [ ] Multi-language support
