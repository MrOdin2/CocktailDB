# CocktailDB Deployment Guide

This guide provides comprehensive instructions for deploying CocktailDB to a production server using automated CI/CD pipelines and deployment scripts.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Server Setup](#server-setup)
4. [GitHub Actions CI/CD Setup](#github-actions-cicd-setup)
5. [Deployment Process](#deployment-process)
6. [Manual Deployment](#manual-deployment)
7. [Monitoring and Maintenance](#monitoring-and-maintenance)
8. [Troubleshooting](#troubleshooting)

## Overview

CocktailDB uses a modern CI/CD pipeline with GitHub Actions to:
- Automatically build and test code on every push
- Build Docker images and push them to GitHub Container Registry
- Deploy to your server with a simple script

### Architecture

```
GitHub Repository
    ↓
GitHub Actions (CI/CD)
    ↓
GitHub Container Registry (ghcr.io)
    ↓
Your Ubuntu Server
    ↓
Docker Containers (Frontend, Backend, Database)
```

## Prerequisites

### On Your Development Machine
- Git
- Text editor for configuration files

### On Your Server
- Ubuntu Server (20.04 LTS or later recommended)
- Docker (version 20.10 or later)
- Docker Compose (version 2.0 or later)
- At least 2GB RAM (4GB recommended)
- 10GB free disk space
- Internet connection
- Static IP or domain name (optional but recommended)

## Server Setup

### 1. Install Docker

If Docker is not already installed on your Ubuntu server:

```bash
# Update package index
sudo apt update

# Install prerequisites
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Add Docker's official GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Set up the stable repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Add your user to the docker group
sudo usermod -aG docker $USER

# Log out and back in for group changes to take effect
```

Verify Docker installation:
```bash
docker --version
docker compose version
```

### 2. Configure Firewall

If you're using UFW (Uncomplicated Firewall):

```bash
# Allow SSH (if not already allowed)
sudo ufw allow 22/tcp

# Allow HTTP
sudo ufw allow 80/tcp

# Allow HTTPS (for future SSL setup)
sudo ufw allow 443/tcp

# Allow backend API (if you want external access)
sudo ufw allow 8080/tcp

# Enable firewall
sudo ufw enable
```

### 3. Create Deployment Directory

```bash
# Create directory for the application
mkdir -p ~/cocktaildb
cd ~/cocktaildb

# Clone the repository (or just copy necessary files)
git clone https://github.com/MrOdin2/CocktailDB.git .
```

### 4. Configure Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# Edit the .env file with your configuration
nano .env
```

**Important:** Update the following in your `.env` file:

```bash
# Change default passwords!
ADMIN_PASSWORD_HASH=<generate-new-hash>
BARKEEPER_PASSWORD_HASH=<generate-new-hash>

# Generate session secret
SESSION_SECRET=<generate-random-secret>

# Configure CORS for your domain/IP
CORS_ALLOWED_ORIGINS=http://your-server-ip,http://your-domain.com
```

Generate password hashes:
```bash
python3 -c "import bcrypt; print(bcrypt.hashpw(b'your-password', bcrypt.gensalt(12)).decode().replace('\$2b\$', '\$2a\$'))"
```

Generate session secret:
```bash
openssl rand -base64 64
```

### 5. Set Up GitHub Container Registry Authentication

To pull Docker images from GitHub Container Registry, you need to authenticate:

```bash
# Create a GitHub Personal Access Token (PAT)
# Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
# Create a token with 'read:packages' scope

# Login to GitHub Container Registry
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```

Alternatively, make your packages public in GitHub to avoid authentication.

## GitHub Actions CI/CD Setup

The repository includes three GitHub Actions workflows:

### 1. Backend CI (`ci-backend.yml`)
- Runs on every push to `main` or `develop` branches
- Builds and tests the backend
- Uploads test results

### 2. Frontend CI (`ci-frontend.yml`)
- Runs on every push to `main` or `develop` branches
- Builds and tests the frontend
- Runs linting checks

### 3. Docker Build and Push (`docker-build-push.yml`)
- Runs on pushes to `main` branch and on tags
- Builds Docker images for all services
- Pushes images to GitHub Container Registry (ghcr.io)
- Can be triggered manually via GitHub UI

### Enabling GitHub Actions

1. Go to your repository on GitHub
2. Click on **Settings** → **Actions** → **General**
3. Ensure Actions are enabled
4. Under **Workflow permissions**, select "Read and write permissions"

### Making Packages Public (Optional)

To avoid authentication when pulling images:

1. Go to your GitHub profile
2. Click on **Packages**
3. Select each package (backend, frontend, postgres)
4. Click **Package settings**
5. Scroll to **Danger Zone** → **Change visibility**
6. Change to **Public**

## Deployment Process

### Automated Deployment

The simplest way to deploy is using the provided deployment script:

```bash
# Navigate to your deployment directory
cd ~/cocktaildb

# Make the deployment script executable
chmod +x scripts/deploy.sh

# Run the deployment
./scripts/deploy.sh
```

This script will:
1. Check prerequisites
2. Backup the database
3. Pull the latest Docker images
4. Stop current services
5. Start new services
6. Verify deployment
7. Clean up old images

### Customizing Deployment

You can customize the deployment with environment variables:

```bash
# Deploy a specific tag
TAG=v1.0.0 ./scripts/deploy.sh

# Use a different registry
REGISTRY=docker.io REPO=youruser/cocktaildb ./scripts/deploy.sh

# Use a different compose file
COMPOSE_FILE=docker-compose.custom.yml ./scripts/deploy.sh
```

### Triggering a New Build and Deploy

1. **Push to main branch:**
   ```bash
   git push origin main
   ```
   This triggers the Docker build workflow automatically.

2. **Wait for GitHub Actions to complete:**
   - Go to your repository on GitHub
   - Click on **Actions** tab
   - Watch the workflow progress

3. **Deploy on your server:**
   ```bash
   cd ~/cocktaildb
   ./scripts/deploy.sh
   ```

### Using Tags for Versioned Releases

For production deployments, it's recommended to use version tags:

```bash
# Create a version tag
git tag -a v1.0.0 -m "Version 1.0.0"
git push origin v1.0.0

# Deploy the tagged version
TAG=v1.0.0 ./scripts/deploy.sh
```

## Manual Deployment

If you prefer to deploy manually without the script:

### 1. Pull Images

```bash
docker pull ghcr.io/mrodin2/cocktaildb/backend:latest
docker pull ghcr.io/mrodin2/cocktaildb/frontend:latest
docker pull ghcr.io/mrodin2/cocktaildb/postgres:latest
```

### 2. Stop Current Services

```bash
docker compose -f docker-compose.prod.yml down
```

### 3. Start New Services

```bash
docker compose -f docker-compose.prod.yml up -d
```

### 4. Check Status

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs
```

## Monitoring and Maintenance

### Viewing Logs

```bash
# View all logs
docker compose logs

# View specific service logs
docker compose logs backend
docker compose logs frontend
docker compose logs postgres

# Follow logs in real-time
docker compose logs -f

# View last 100 lines
docker compose logs --tail=100
```

### Checking Service Health

```bash
# Check running containers
docker compose ps

# Check health status
docker inspect cocktaildb-backend --format='{{.State.Health.Status}}'
docker inspect cocktaildb-postgres --format='{{.State.Health.Status}}'
```

### Database Backups

The PostgreSQL container includes automated daily backups (default: 2 AM).

Manual backup:
```bash
# Create backup
docker exec cocktaildb-postgres pg_dump -U cocktaildb cocktaildb > backup_$(date +%Y%m%d).sql

# Restore backup
docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < backup_20241124.sql
```

See [Database Management Guide](DATABASE_MANAGEMENT.md) for more details.

### Updating the Application

```bash
# Pull latest code (if needed)
git pull origin main

# Update environment variables if needed
nano .env

# Run deployment script
./scripts/deploy.sh
```

### Disk Space Management

```bash
# Check disk usage
df -h

# Remove unused Docker resources
docker system prune -a

# Remove old backups (keep last 7 days)
find . -name "backup_*.sql" -mtime +7 -delete
```

## Troubleshooting

For detailed troubleshooting information, see the **[Troubleshooting Guide](TROUBLESHOOTING.md)**.

### Quick Fixes

#### Services Won't Start

```bash
# Check logs
docker compose logs

# Check if ports are already in use
sudo netstat -tulpn | grep -E '80|8080|5432'

# Check Docker daemon
sudo systemctl status docker
```

### Cannot Pull Images

```bash
# Check authentication
docker login ghcr.io

# Check network connectivity
ping ghcr.io

# Try pulling manually
docker pull ghcr.io/mrodin2/cocktaildb/backend:latest
```

### Database Connection Issues

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check database logs
docker compose logs postgres

# Test database connection
docker exec -it cocktaildb-postgres psql -U cocktaildb

# Check database health
docker inspect cocktaildb-postgres --format='{{.State.Health.Status}}'
```

### Frontend Not Accessible

```bash
# Check if frontend is running
docker ps | grep frontend

# Check nginx logs
docker compose logs frontend

# Check if port 80 is accessible
curl http://localhost

# Check firewall
sudo ufw status
```

### Backend API Not Responding

```bash
# Check backend status
docker ps | grep backend

# Check backend logs
docker compose logs backend

# Test API endpoint
curl http://localhost:8080/api/auth/status

# Check Java process inside container
docker exec cocktaildb-backend ps aux
```

### Rolling Back a Failed Deployment

If the deployment script fails, it will automatically attempt to rollback. To manually rollback:

```bash
# Stop current deployment
docker compose down

# Restore from backup
docker compose up -d postgres
sleep 10
docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < backup_YYYYMMDD_HHMMSS.sql

# Start all services
docker compose up -d
```

### Checking GitHub Actions Status

If builds are failing:

1. Go to GitHub repository → **Actions** tab
2. Click on the failed workflow
3. Check the logs for each step
4. Common issues:
   - Missing secrets or permissions
   - Build errors in backend/frontend
   - Docker build context issues

### Container Registry Issues

If you can't push/pull images:

1. Check package visibility (public vs private)
2. Verify GitHub token has correct permissions
3. Check repository package settings
4. Ensure Docker is logged in: `docker login ghcr.io`

## Security Recommendations

1. **Change Default Passwords:** Always change the default admin and barkeeper passwords
2. **Use HTTPS:** Set up a reverse proxy with SSL (nginx, Caddy, or Traefik)
3. **Firewall:** Only open necessary ports
4. **Updates:** Regularly update Docker and the host system
5. **Backups:** Keep regular database backups off-server
6. **Monitoring:** Set up monitoring and alerting (optional: Prometheus + Grafana)

## Additional Resources

- [Database Management Guide](DATABASE_MANAGEMENT.md)
- [Security Concept](../SECURITY_CONCEPT.md)
- [Local Network Testing](local-network-testing.md)
- [Architecture Documentation](ARCHITECTURE.md)
- [Troubleshooting Guide](TROUBLESHOOTING.md) - Detailed troubleshooting for all deployment issues

## Support

If you encounter issues:

1. Check the **[Troubleshooting Guide](TROUBLESHOOTING.md)** for detailed solutions
2. Review Docker and application logs
3. Check GitHub Actions workflow logs
4. Open an issue on GitHub with detailed error information
