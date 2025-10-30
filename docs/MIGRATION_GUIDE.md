# Migration Guide: Jenkins/Bitbucket to GitLab CI/CD

## Overview
This guide helps you migrate your existing API and UI tests from Jenkins/Bitbucket to GitLab CI/CD.

## Prerequisites
- GitLab account with CI/CD enabled
- GitLab Runner configured
- Access to Jenkins jobs configuration
- Bitbucket repository access

## Migration Steps

### 1. Repository Migration

#### Export from Bitbucket
```bash
# Clone your Bitbucket repository
git clone https://bitbucket.org/your-org/your-repo.git
cd your-repo

# Add GitLab remote
git remote add gitlab https://gitlab.com/your-org/your-repo.git

# Push to GitLab
git push -u gitlab --all
git push -u gitlab --tags
```

### 2. Pipeline Configuration

#### Jenkins Pipeline → GitLab CI/CD Mapping

| Jenkins | GitLab CI/CD |
|---------|--------------|
| Jenkinsfile | .gitlab-ci.yml |
| stage | stage |
| steps | script |
| agent | tags/image |
| post | after_script |
| environment | variables |

#### Example Conversion

**Jenkins (Jenkinsfile):**
```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}
```

**GitLab (.gitlab-ci.yml):**
```yaml
stages:
  - build
  - test

build:
  stage: build
  script:
    - mvn clean compile

test:
  stage: test
  script:
    - mvn test
```

### 3. Environment Variables Migration

#### In Jenkins
Variables are stored in:
- Jenkins credentials
- Environment variables
- Properties files

#### In GitLab
Navigate to: **Settings → CI/CD → Variables**

Add these variables:
- `API_BASE_URL`
- `API_TOKEN`
- `DB_PASSWORD` (mark as protected and masked)
- `SLACK_WEBHOOK_URL`

### 4. Test Execution Migration

#### Jenkins Approach
```groovy
stage('API Tests') {
    steps {
        sh 'mvn test -Dtest=ApiTests'
    }
}
stage('UI Tests') {
    steps {
        sh 'mvn test -Dtest=UiTests'
    }
}
```

#### GitLab Approach
```yaml
api_tests:
  stage: test
  script:
    - mvn test -Dtest.suite=api

ui_tests:
  stage: test
  script:
    - mvn test -Dtest.suite=ui
```

### 5. Artifact Management

#### Jenkins
```groovy
post {
    always {
        archiveArtifacts artifacts: 'target/surefire-reports/**'
        junit 'target/surefire-reports/*.xml'
    }
}
```

#### GitLab
```yaml
test:
  artifacts:
    when: always
    paths:
      - target/surefire-reports/
    reports:
      junit: target/surefire-reports/TEST-*.xml
```

### 6. Notifications

#### Jenkins (Slack Plugin)
```groovy
post {
    failure {
        slackSend message: "Build failed"
    }
}
```

#### GitLab (Webhook)
Navigate to: **Settings → Integrations → Slack notifications**

Or use `.gitlab-ci.yml`:
```yaml
notify:
  stage: .post
  script:
    - 'curl -X POST -H "Content-type: application/json" --data "{\"text\":\"Build failed\"}" $SLACK_WEBHOOK_URL'
  when: on_failure
```

### 7. Scheduled Runs

#### Jenkins
- Configure through Jenkins UI
- Use cron syntax

#### GitLab
Navigate to: **CI/CD → Schedules**
- Click "New schedule"
- Set cron expression
- Target branch: main
- Variables: `TEST_ENV=production`

### 8. Parallel Execution

#### Jenkins
```groovy
parallel {
    stage('API Tests') {
        steps { sh 'mvn test -Dtest=ApiTests' }
    }
    stage('UI Tests') {
        steps { sh 'mvn test -Dtest=UiTests' }
    }
}
```

#### GitLab
```yaml
api_tests:
  stage: test
  script: mvn test -Dtest.suite=api

ui_tests:
  stage: test
  script: mvn test -Dtest.suite=ui
```
(Jobs in same stage run in parallel by default)

### 9. Docker Integration

#### Jenkins (Docker Plugin)
```groovy
agent {
    docker {
        image 'maven:3.9-eclipse-temurin-17'
    }
}
```

#### GitLab
```yaml
image: maven:3.9-eclipse-temurin-17
```

### 10. Test Reports

#### Allure Reports

**Jenkins (Allure Plugin):**
```groovy
post {
    always {
        allure includeProperties: false,
               jdk: '',
               results: [[path: 'target/allure-results']]
    }
}
```

**GitLab:**
```yaml
generate_report:
  stage: report
  script:
    - mvn allure:report
  artifacts:
    paths:
      - target/allure-report/

pages:
  script:
    - cp -r target/allure-report/* public/
  artifacts:
    paths:
      - public
```

## Checklist

- [ ] Repository migrated from Bitbucket to GitLab
- [ ] `.gitlab-ci.yml` created and tested
- [ ] Environment variables configured in GitLab
- [ ] GitLab Runner configured and tagged
- [ ] Pipeline stages defined (build, test, report, deploy)
- [ ] Artifacts properly configured
- [ ] Test reports accessible
- [ ] Notifications configured (Slack/Email)
- [ ] Scheduled pipelines configured
- [ ] Docker images configured
- [ ] Access permissions reviewed
- [ ] Documentation updated
- [ ] Team trained on GitLab CI/CD
- [ ] Jenkins jobs deprecated

## Common Issues

### Issue: "Runner not found"
**Solution:** Configure GitLab Runner and add proper tags

### Issue: "Playwright browsers not found"
**Solution:** Add browser installation step in pipeline:
```yaml
before_script:
  - mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Issue: "Tests fail in CI but pass locally"
**Solution:** Check environment variables and headless mode settings

### Issue: "Pipeline too slow"
**Solution:** 
- Use Docker layer caching
- Enable parallel execution
- Cache dependencies

## Best Practices

1. **Use Docker images** for consistent environments
2. **Cache dependencies** to speed up builds
3. **Run smoke tests first** for fast feedback
4. **Use parallel execution** for faster test runs
5. **Store sensitive data** in GitLab CI/CD variables
6. **Tag runners** for specific workloads
7. **Use artifacts** efficiently
8. **Monitor pipeline performance**
9. **Document custom configurations**
10. **Regular pipeline maintenance**

## Resources

- [GitLab CI/CD Documentation](https://docs.gitlab.com/ee/ci/)
- [GitLab Runners](https://docs.gitlab.com/runner/)
- [Pipeline Configuration Reference](https://docs.gitlab.com/ee/ci/yaml/)

## Support

Contact the DevOps team for:
- Runner configuration
- Pipeline optimization
- Migration assistance
