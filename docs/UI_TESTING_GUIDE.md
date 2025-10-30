# UI Testing Guide

## Current Status

✅ **Playwright browsers are installed and working**  
⏸️ **UI tests are temporarily disabled** (see reasons below)

## Why UI Tests Are Disabled

The UI tests are disabled in `testng.xml` because they require:

1. **External network stability** - `SampleUiTest` accesses `https://playwright.dev`
2. **Local frontend server running** - `DemoUserUiTest` accesses `http://localhost:3000`

## How to Enable UI Tests

### Option 1: Enable All UI Tests

1. Start the frontend server:
   ```bash
   cd demo-app/frontend
   python3 -m http.server 3000
   ```

2. Edit `src/test/resources/testng.xml`:
   ```xml
   <test name="UI Tests" enabled="true">
   <test name="Demo User UI Tests" enabled="true">
   ```

3. Run tests:
   ```bash
   mvn test -Denv=dev
   ```

### Option 2: Run UI Tests Manually

Run specific UI test classes without modifying testng.xml:

```bash
# Run only Demo User UI tests (requires frontend on port 3000)
mvn test -Dtest=DemoUserUiTest

# Run only Sample UI tests (requires network access)
mvn test -Dtest=SampleUiTest
```

### Option 3: Run UI Tests Selectively by Group

```bash
# Run just UI smoke tests
mvn test -Dgroups=ui-smoke
```

## Browser Installation

Browsers are automatically installed when you first run UI tests. If you need to reinstall manually:

```bash
mvn test-compile exec:java -Dexec.mainClass="com.testing.framework.setup.InstallBrowsers" -Dexec.classpathScope=test
```

This utility will:
- ✅ Download Chromium, Firefox, and WebKit browsers
- ✅ Verify installation by launching a browser
- ✅ Confirm everything is ready

## Browser Configuration

Configure browsers in `src/test/resources/config/dev.properties`:

```properties
# Browser settings
browser=chromium              # Options: chromium, firefox, webkit
headless=false                # Set to true for CI/CD pipelines
ui.base.url=http://localhost:3000

# Timeout settings
page.load.timeout=30000
element.wait.timeout=10000
```

## Troubleshooting UI Tests

### Network Errors (ERR_ABORTED)

If you see `net::ERR_ABORTED` errors:
- Check your internet connection
- Consider using a VPN if corporate firewall blocks access
- Modify tests to use local URLs instead of external sites

### Frontend Not Running

If tests fail with `http://localhost:3000`:
```bash
# Start the demo frontend
cd demo-app/frontend
python3 -m http.server 3000

# Or use a different port
python3 -m http.server 8080

# Then update dev.properties:
ui.base.url=http://localhost:8080
```

### Browser Launch Failures

If browsers won't launch:
```bash
# Reinstall browsers
mvn test-compile exec:java \
  -Dexec.mainClass="com.testing.framework.setup.InstallBrowsers" \
  -Dexec.classpathScope=test
```

## Recommended Setup for Development

For local development, keep UI tests disabled and run them manually when needed:

```bash
# Normal development workflow (API tests only)
mvn test

# When testing UI changes
cd demo-app/frontend && python3 -m http.server 3000 &
mvn test -Dtest=DemoUserUiTest
```

## CI/CD Pipeline

For automated pipelines, you'll want to:

1. Set `headless=true` in production config
2. Start frontend as background service
3. Enable UI tests in pipeline-specific testng.xml
4. Use Docker for isolated browser environment

Example GitLab CI configuration is available in `.gitlab-ci.yml`.

## Current Test Results

**API Tests: 15/15 PASSED ✅**
- Smoke Tests: 3/3 ✅
- Sample API Tests: 6/6 ✅  
- Demo User API Tests: 6/6 ✅

**UI Tests: Disabled** ⏸️
- Can be enabled when frontend server is running
- Playwright browsers are installed and ready

---

**Next Steps:**
1. Start frontend server if you want to run Demo UI tests
2. Ensure stable network if you want to run Sample UI tests
3. Edit `testng.xml` to enable the test suites you need
