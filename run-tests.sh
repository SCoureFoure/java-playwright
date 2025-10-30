#!/bin/bash

# Script to run different test suites

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
ENV="dev"
SUITE="all"
PARALLEL="false"
HEADLESS="true"

# Function to display usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env        Environment (dev|staging|prod) [default: dev]"
    echo "  -s, --suite      Test suite (all|api|ui|smoke|integration) [default: all]"
    echo "  -p, --parallel   Enable parallel execution [default: false]"
    echo "  -h, --headless   Run UI tests in headless mode [default: true]"
    echo "  --help           Display this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -e staging -s api"
    echo "  $0 --env prod --suite smoke --headless true"
    echo "  $0 -s ui -p true"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENV="$2"
            shift 2
            ;;
        -s|--suite)
            SUITE="$2"
            shift 2
            ;;
        -p|--parallel)
            PARALLEL="$2"
            shift 2
            ;;
        -h|--headless)
            HEADLESS="$2"
            shift 2
            ;;
        --help)
            usage
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            usage
            ;;
    esac
done

echo -e "${GREEN}=== Test Execution Configuration ===${NC}"
echo "Environment: $ENV"
echo "Test Suite: $SUITE"
echo "Parallel Execution: $PARALLEL"
echo "Headless Mode: $HEADLESS"
echo ""

# Install Playwright browsers if not already installed
if [ ! -d "$HOME/.cache/ms-playwright" ]; then
    echo -e "${YELLOW}Installing Playwright browsers...${NC}"
    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
fi

# Build the Maven command
MAVEN_CMD="mvn clean test -Denv=$ENV"

# Add test suite parameter
case $SUITE in
    api)
        MAVEN_CMD="$MAVEN_CMD -Dtest.suite=api"
        ;;
    ui)
        MAVEN_CMD="$MAVEN_CMD -Dtest.suite=ui"
        ;;
    smoke)
        MAVEN_CMD="$MAVEN_CMD -Dtest.suite=smoke"
        ;;
    integration)
        MAVEN_CMD="$MAVEN_CMD -Dtest.suite=integration"
        ;;
    all)
        # Run all tests
        ;;
    *)
        echo -e "${RED}Invalid test suite: $SUITE${NC}"
        usage
        ;;
esac

# Add parallel execution if enabled
if [ "$PARALLEL" = "true" ]; then
    MAVEN_CMD="$MAVEN_CMD -Dparallel=methods -DthreadCount=4"
fi

# Add headless mode for UI tests
MAVEN_CMD="$MAVEN_CMD -Dheadless=$HEADLESS"

# Execute tests
echo -e "${GREEN}Running tests...${NC}"
echo "Command: $MAVEN_CMD"
echo ""

if eval $MAVEN_CMD; then
    echo -e "${GREEN}✓ Tests completed successfully${NC}"
    
    # Generate Allure report
    echo -e "${YELLOW}Generating Allure report...${NC}"
    mvn allure:serve
else
    echo -e "${RED}✗ Tests failed${NC}"
    exit 1
fi
