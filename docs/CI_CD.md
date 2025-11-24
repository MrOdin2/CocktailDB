# CI/CD Pipeline Documentation

This document describes the Continuous Integration and Continuous Deployment (CI/CD) pipeline for CocktailDB.

## Overview

CocktailDB uses GitHub Actions for automated building, testing, and deployment. The pipeline ensures code quality and enables rapid, reliable deployments.

## Pipeline Architecture

```
Code Push/PR → GitHub Actions → Tests → Build → Docker Images → GitHub Registry → Server Deployment
```

## GitHub Actions Workflows

### 1. Backend CI (`ci-backend.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches
- Changes to `backend/**` files

**Jobs:**

#### Test Job
- Checks out code
- Sets up JDK 17
- Runs Gradle tests
- Uploads test results as artifacts

#### Build Job
- Depends on successful test job
- Builds the backend JAR file
- Uploads JAR as artifact (7-day retention)

**Configuration:**
```yaml
Environment: ubuntu-latest
Java: OpenJDK 17 (Temurin)
Build Tool: Gradle with daemon disabled
Cache: Gradle dependencies
```

### 2. Frontend CI (`ci-frontend.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches
- Changes to `frontend/**` files

**Jobs:**

#### Test Job
- Checks out code
- Sets up Node.js 22
- Installs dependencies with `npm ci`
- Runs tests with ChromeHeadless

#### Lint Job
- Runs in parallel with test job
- Executes Angular linting
- Gracefully handles missing lint configuration

#### Build Job
- Depends on successful test and lint jobs
- Builds production bundle
- Uploads dist folder as artifact (7-day retention)

**Configuration:**
```yaml
Environment: ubuntu-latest
Node.js: Version 22
Package Manager: npm with ci
Cache: npm dependencies
Browser: ChromeHeadless for tests
```

### 3. Docker Build and Push (`docker-build-push.yml`)

**Triggers:**
- Push to `main` branch
- Git tags matching `v*` pattern
- Manual workflow dispatch

**Permissions Required:**
- `contents: read` - Read repository code
- `packages: write` - Push to GitHub Container Registry

**Jobs:**

#### Build and Push
- Checks out code
- Sets up Docker Buildx for multi-platform builds
- Logs into GitHub Container Registry
- Extracts metadata for each image (tags, labels)
- Builds backend JAR with Gradle
- Builds frontend bundle with npm
- Builds and pushes Docker images:
  - Backend image
  - Frontend image
  - PostgreSQL image with backup support

**Image Tagging Strategy:**
- `latest` - Latest build from main branch
- `main` - Builds from main branch
- `v1.2.3` - Semantic version tags
- `v1.2` - Major.minor version
- `main-abc1234` - Branch name with commit SHA

**Registry:**
```
ghcr.io/mrodin2/cocktaildb/backend:latest
ghcr.io/mrodin2/cocktaildb/frontend:latest
ghcr.io/mrodin2/cocktaildb/postgres:latest
```

## Workflow Best Practices

### Branch Strategy

**Main Branch:**
- Protected branch
- Requires PR reviews
- All tests must pass
- Triggers production Docker builds

**Develop Branch:**
- Integration branch
- Runs CI tests
- Does not trigger Docker builds

**Feature Branches:**
- Created from develop
- CI runs on PRs
- Merged via PR to develop

### Pull Request Workflow

1. Create feature branch from `develop`
2. Make changes and commit
3. Push and create PR to `develop`
4. GitHub Actions runs CI checks
5. Review and approve PR
6. Merge to `develop`
7. When ready for release, merge `develop` to `main`
8. Docker images are built and pushed

### Release Process

**Version Release:**
```bash
# Create and push a version tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

This triggers:
- Docker image builds
- Multiple tags (v1.0.0, v1.0, latest)
- Available for deployment

**Hotfix Release:**
```bash
# Create hotfix branch from main
git checkout -b hotfix/critical-fix main

# Make fixes and test
git commit -am "Fix critical issue"

# Merge back to main and develop
git checkout main
git merge hotfix/critical-fix
git push origin main

# Tag the hotfix
git tag -a v1.0.1 -m "Hotfix 1.0.1"
git push origin v1.0.1
```

## Secrets and Configuration

### Required Secrets

GitHub Actions uses these built-in secrets:
- `GITHUB_TOKEN` - Automatically provided, used for registry authentication

### Environment Variables

Configured in workflow files:
- `REGISTRY` - Container registry (ghcr.io)
- `IMAGE_NAME_*` - Image names for each service

## Manual Workflow Triggers

### Triggering Docker Build Manually

1. Go to GitHub repository
2. Click **Actions** tab
3. Select **Build and Push Docker Images**
4. Click **Run workflow**
5. Select branch and optionally specify tag
6. Click **Run workflow**

## Monitoring Workflows

### Viewing Workflow Runs

1. Navigate to repository on GitHub
2. Click **Actions** tab
3. View all workflow runs
4. Click on specific run for details

### Workflow Status Badges

Add to README.md:
```markdown
![Backend CI](https://github.com/MrOdin2/CocktailDB/workflows/Backend%20CI/badge.svg)
![Frontend CI](https://github.com/MrOdin2/CocktailDB/workflows/Frontend%20CI/badge.svg)
![Docker Build](https://github.com/MrOdin2/CocktailDB/workflows/Build%20and%20Push%20Docker%20Images/badge.svg)
```

### Notifications

**Enable notifications:**
1. GitHub Settings → Notifications
2. Configure workflow failure notifications
3. Optionally integrate with Slack/Discord

## Caching Strategy

### Gradle Cache
- Caches Gradle wrapper and dependencies
- Shared across backend workflows
- Expires after 7 days of inactivity

### npm Cache
- Caches node_modules
- Uses package-lock.json as key
- Shared across frontend workflows

### Docker Build Cache
- Uses GitHub Actions cache
- Stores layer cache for faster rebuilds
- Managed by Docker Buildx

## Performance Optimization

### Current Optimizations

1. **Dependency Caching:**
   - Gradle dependencies cached
   - npm dependencies cached
   - Docker layers cached

2. **Parallel Jobs:**
   - Frontend test and lint run in parallel
   - Multiple workflows can run simultaneously

3. **Conditional Execution:**
   - Workflows only run when relevant files change
   - Docker builds only on main branch/tags

### Future Improvements

- [ ] Matrix builds for multiple Java versions
- [ ] End-to-end tests in separate workflow
- [ ] Performance benchmarking
- [ ] Security scanning (CodeQL, Trivy)
- [ ] Automated changelog generation

## Troubleshooting CI/CD

### Common Issues

**Build Fails - Gradle:**
```
Solution: Check Java version compatibility
Verify: Gradle wrapper is executable
Fix: chmod +x gradlew
```

**Build Fails - npm:**
```
Solution: Delete package-lock.json and regenerate
Verify: Node version compatibility
Fix: Use npm ci instead of npm install
```

**Docker Push Fails:**
```
Solution: Check package permissions
Verify: Workflow has packages: write permission
Fix: Enable in repository settings
```

**Tests Fail:**
```
Solution: Check test logs in artifacts
Verify: Tests pass locally
Fix: Ensure test dependencies are installed
```

### Debugging Workflows

**Enable debug logging:**
```bash
# Add repository secret
ACTIONS_STEP_DEBUG = true
ACTIONS_RUNNER_DEBUG = true
```

**Access workflow logs:**
1. Click on failed workflow
2. Expand failed step
3. Review detailed logs
4. Download logs for offline analysis

## Integration with Deployment

### Deployment Flow

1. **Push to main** → Triggers Docker build
2. **Wait for build** → Check Actions tab
3. **SSH to server** → Connect to deployment server
4. **Run deploy script** → `./scripts/deploy.sh`
5. **Verify deployment** → Check application status

### Automated Deployment (Future)

Consider implementing:
- GitHub Actions deploy job
- SSH into server from Actions
- Run deployment script automatically
- Notify on deployment status

**Example (future implementation):**
```yaml
deploy:
  needs: build-and-push
  runs-on: ubuntu-latest
  steps:
    - name: Deploy to server
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        script: |
          cd ~/cocktaildb
          ./scripts/deploy.sh
```

## Security Considerations

### Container Registry Security

1. **Package Visibility:**
   - Public: Anyone can pull images
   - Private: Requires authentication

2. **Access Tokens:**
   - Use fine-grained tokens
   - Minimal required permissions
   - Rotate regularly

### Workflow Security

1. **Secrets Management:**
   - Never commit secrets
   - Use GitHub Secrets
   - Scope secrets appropriately

2. **Pull Request Validation:**
   - Require reviews
   - Run tests on PRs
   - Block merge on failures

## Metrics and Analytics

### Key Metrics

- Build success rate
- Average build time
- Test pass rate
- Deployment frequency
- Time to deployment

### Accessing Metrics

1. Actions tab → View all runs
2. Insights tab → Dependency graph
3. Third-party tools: CircleCI, Jenkins dashboards

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [Deployment Guide](DEPLOYMENT.md)
- [Server Setup Guide](SERVER_SETUP.md)

## Support

For CI/CD issues:
1. Check workflow logs
2. Review this documentation
3. Check GitHub Actions status page
4. Open issue with workflow run link
