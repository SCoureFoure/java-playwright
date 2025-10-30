# API Automation Testing Framework

## Overview
This is a comprehensive testing framework that integrates **RestAssured** for API testing and **Playwright** for UI testing, built with Java and designed for GitLab CI/CD integration.

## Project Structure
```
java-playwright/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/testing/framework/
│   │           ├── api/              # API testing utilities
│   │           │   ├── client/       # RestAssured clients
│   │           │   ├── models/       # API request/response models
│   │           │   └── utils/        # API utilities
│   │           ├── ui/               # UI testing utilities
│   │           │   ├── pages/        # Page Object Models
│   │           │   ├── components/   # Reusable UI components
│   │           │   └── utils/        # UI utilities
│   │           ├── core/             # Core framework components
│   │           │   ├── config/       # Configuration management
│   │           │   ├── listeners/    # Test listeners
│   │           │   └── reporting/    # Custom reporting
│   │           └── utils/            # Common utilities
│   └── test/
│       ├── java/
│       │   └── com/testing/framework/
│       │       ├── api/              # API test cases
│       │       │   ├── functional/
│       │       │   ├── integration/
│       │       │   └── contract/
│       │       ├── ui/               # UI test cases
│       │       │   ├── functional/
│       │       │   └── e2e/
│       │       └── smoke/            # Smoke tests
│       └── resources/
│           ├── testng.xml            # TestNG configuration
│           ├── config/               # Environment configs
│           ├── testdata/             # Test data files
│           └── schemas/              # JSON schemas for validation
├── .gitlab-ci.yml                    # GitLab CI/CD pipeline
├── pom.xml                           # Maven dependencies
└── README.md                         # This file
```

## Features

### API Testing (RestAssured)
- ✅ RESTful API automation
- ✅ JSON schema validation
- ✅ Response assertions
- ✅ Authentication handling (OAuth, JWT, Basic Auth)
- ✅ Request/Response logging
- ✅ Data-driven testing

### UI Testing (Playwright)
- ✅ Cross-browser testing (Chrome, Firefox, Safari)
- ✅ Page Object Model pattern
- ✅ Auto-waiting for elements
- ✅ Screenshot capture on failure
- ✅ Video recording
- ✅ Mobile emulation

### Framework Features
- ✅ TestNG test orchestration
- ✅ Parallel execution support
- ✅ Allure reporting integration
- ✅ Environment-based configuration
- ✅ Comprehensive logging
- ✅ GitLab CI/CD ready
- ✅ Docker support

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- GitLab Runner (for CI/CD)

## Installation

### 1. Check Prerequisites
```bash
# Verify Java (need 17+)
java -version

# Install Maven if not present
brew install maven
# Installs Maven 3.9.11
```

### 2. Clone the repository
```bash
git clone <repository-url>
cd java-playwright
```

### 3. Install dependencies and compile
```bash
mvn clean install -DskipTests
# Downloads 50+ dependencies
# Compiles framework and test code
# Expected: BUILD SUCCESS in ~10-20 seconds
```

### 4. Install Playwright browsers
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
# Downloads Chromium, Firefox, and WebKit
# Takes 2-3 minutes
```

### 5. Configure TestNG (Important!)
Ensure your `pom.xml` has the TestNG configuration:
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

## Configuration

### Environment Configuration
Edit configuration files in `src/test/resources/config/`:
- `dev.properties` - Development environment
- `staging.properties` - Staging environment
- `prod.properties` - Production environment

### Test Configuration
Modify `src/test/resources/testng.xml` to configure test suites and parallel execution.

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run API tests only
```bash
mvn clean test -Dtest.suite=api
```

### Run UI tests only
```bash
mvn clean test -Dtest.suite=ui
```

### Run with specific environment
```bash
mvn clean test -Denv=staging
```

### Run with parallel execution
```bash
mvn clean test -Dparallel=methods -DthreadCount=4
```

## Reporting

### Generate Allure Report
```bash
mvn allure:serve
```

### View latest report
```bash
mvn allure:report
```

Reports are generated in `target/allure-results/`

## GitLab CI/CD Integration

The framework includes a `.gitlab-ci.yml` configuration with:
- Multiple stages (build, test, report, deploy)
- Parallel test execution
- Artifact management
- Allure report publishing
- Docker support

### Pipeline Stages
1. **Build** - Compile and package
2. **Test** - Run tests (API, UI, Smoke)
3. **Report** - Generate and publish reports
4. **Deploy** - Deploy test results (optional)

## Best Practices

### API Testing
1. Use data providers for parameterized tests
2. Validate response schemas
3. Implement proper authentication
4. Use meaningful assertions
5. Log requests and responses

### UI Testing
1. Follow Page Object Model pattern
2. Use meaningful selectors (data-testid)
3. Implement explicit waits
4. Capture screenshots on failure
5. Keep tests independent

### Framework Maintenance
1. Regular dependency updates
2. Code reviews for test additions
3. Maintain comprehensive documentation
4. Monitor test execution metrics
5. Refactor duplicated code

## Migration Guide

### From Jenkins to GitLab
1. Review `.gitlab-ci.yml` configuration
2. Update environment variables in GitLab
3. Configure GitLab Runners
4. Migrate Jenkins credentials to GitLab CI/CD variables
5. Test pipeline execution

### Migrating Existing Tests
1. Adapt test structure to framework conventions
2. Update imports and dependencies
3. Implement Page Objects for UI tests
4. Add Allure annotations
5. Validate test execution

## Troubleshooting

### Common Issues

**Issue**: Tests run: 0 (TestNG not executing)
```bash
# Add TestNG suite configuration to pom.xml (see Installation step 5)
# Then run: mvn clean test
```

**Issue**: Maven not found
```bash
brew install maven
mvn -version  # Verify installation
```

**Issue**: Playwright browsers not installed
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

**Issue**: Dependency download failures
```bash
# Clear cache and retry
rm -rf ~/.m2/repository/com/testing/framework
mvn clean install -DskipTests
```

**Issue**: Tests failing in CI/CD
- Check GitLab Runner configuration
- Verify environment variables
- Review Docker container setup
- Ensure browsers installed in CI environment

**Issue**: Parallel execution failures
- Reduce thread count: `-DthreadCount=2`
- Check for test dependencies
- Review shared resources
- Use thread-safe test data

## Contributing
1. Create feature branch
2. Follow coding standards
3. Add tests for new features
4. Update documentation
5. Submit merge request

## 📚 Documentation

All documentation is organized in the `docs/` folder. Each document serves a unique purpose with no duplication.

**Start here**: [docs/DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md)

### Quick Links
- � **[Quick Start](docs/QUICK_START.md)** - What you have (5 min)
- �📖 **[Getting Started](docs/GETTING_STARTED.md)** - Complete setup (30 min)
- ⚡ **[Quick Reference](docs/QUICK_REFERENCE.md)** - Commands (bookmark!)
- 🏗️ **[Architecture](docs/ARCHITECTURE.md)** - Technical design (20 min)
- 🔄 **[Migration Guide](docs/MIGRATION_GUIDE.md)** - CI/CD setup (25 min)

## Support
For questions or issues, please contact the Test Automation team.

## License
Proprietary - Internal Use Only

---

**Milestone 3 Timeline**: October 27 - December 31, 2025
- Weeks 1-2: Framework architecture and GitLab setup
- Weeks 3-6: API test migration to RestAssured/Playwright
- Weeks 6-7: UI test integration with Playwright
- Weeks 8-9: Stabilization and documentation
