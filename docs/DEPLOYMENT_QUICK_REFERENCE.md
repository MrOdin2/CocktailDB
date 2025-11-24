# Deployment Automation - Quick Reference

This document provides a quick overview of the deployment automation setup for CocktailDB.

## What Was Implemented

### 1. CI/CD Pipeline (GitHub Actions)

Three automated workflows:

- **Backend CI** (`.github/workflows/ci-backend.yml`)
  - Runs on push/PR to `main` or `develop`
  - Executes backend tests
  - Builds JAR file
  - Uploads build artifacts

- **Frontend CI** (`.github/workflows/ci-frontend.yml`)
  - Runs on push/PR to `main` or `develop`
  - Executes frontend tests
  - Runs linting
  - Builds production bundle
  - Uploads build artifacts

- **Docker Build & Push** (`.github/workflows/docker-build-push.yml`)
  - Runs on push to `main` or version tags (`v*`)
  - Builds Docker images for all services
  - Pushes to GitHub Container Registry (ghcr.io)
  - Supports manual triggering

### 2. Deployment Scripts

- **Production Compose File** (`docker-compose.prod.yml`)
  - Uses pre-built images from GitHub Container Registry
  - Same configuration as development setup
  - Environment-based configuration via .env

- **Deployment Script** (`scripts/deploy.sh`)
  - One-command deployment: `./scripts/deploy.sh`
  - Automatic database backup before deployment
  - Health checks for all services
  - Automatic rollback on failure
  - Image cleanup after deployment

### 3. Documentation

- **[DEPLOYMENT.md](docs/DEPLOYMENT.md)** - Complete deployment guide
- **[SERVER_SETUP.md](docs/SERVER_SETUP.md)** - Quick server setup
- **[CI_CD.md](docs/CI_CD.md)** - CI/CD pipeline details
- **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Problem resolution

## How to Use

### For Development

1. Make code changes
2. Create PR to `develop` branch
3. CI workflows run automatically
4. Review and merge

### For Production Release

1. Merge `develop` to `main`
2. Docker images build automatically
3. SSH to server
4. Run: `./scripts/deploy.sh`

### For Versioned Releases

```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
# Images tagged with v1.0.0, v1.0, and latest
```

## Quick Commands

### Server Setup (First Time)
```bash
# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# Setup deployment
mkdir -p ~/cocktaildb && cd ~/cocktaildb
curl -O https://raw.githubusercontent.com/MrOdin2/CocktailDB/main/scripts/deploy.sh
chmod +x deploy.sh

# Configure (copy from .env.example and edit)
nano .env

# Deploy
./deploy.sh
```

### Regular Deployment
```bash
cd ~/cocktaildb
./deploy.sh
```

### Manual Commands
```bash
# Pull latest images
docker pull ghcr.io/mrodin2/cocktaildb/backend:latest
docker pull ghcr.io/mrodin2/cocktaildb/frontend:latest
docker pull ghcr.io/mrodin2/cocktaildb/postgres:latest

# Deploy
docker compose -f docker-compose.prod.yml up -d

# Check status
docker compose ps
docker compose logs -f
```

## Architecture Flow

```
Developer
   ↓ (git push)
GitHub Repository
   ↓ (triggers)
GitHub Actions
   ↓ (builds)
Docker Images
   ↓ (pushes to)
GitHub Container Registry
   ↓ (pulls from)
Deployment Script
   ↓ (deploys to)
Server Docker Containers
```

## Key Features

✅ **Automated Testing** - Every push is tested  
✅ **Automated Builds** - Docker images built on main branch  
✅ **One-Command Deploy** - Simple deployment script  
✅ **Auto Backup** - Database backed up before each deployment  
✅ **Health Checks** - Verifies deployment success  
✅ **Auto Rollback** - Rolls back on failure  
✅ **Version Tagging** - Supports semantic versioning  
✅ **Security** - Proper workflow permissions, secure defaults  

## Environment Variables

Required in `.env` file:

```bash
# Database (matches docker-compose)
POSTGRES_DB=cocktaildb
POSTGRES_USER=cocktaildb
POSTGRES_PASSWORD=cocktaildb

# Security (MUST CHANGE!)
ADMIN_PASSWORD_HASH=<generate-new>
BARKEEPER_PASSWORD_HASH=<generate-new>
SESSION_SECRET=<generate-random>

# Network
CORS_ALLOWED_ORIGINS=http://your-server-ip
```

Generate secure values:
```bash
# Password hashes
python3 -c "import bcrypt; print(bcrypt.hashpw(b'your-password', bcrypt.gensalt(14)).decode().replace('\$2b\$', '\$2a\$'))"

# Session secret
openssl rand -base64 64
```

## Monitoring

```bash
# View all logs
docker compose logs

# Follow logs
docker compose logs -f

# Service status
docker compose ps

# Resource usage
docker stats

# Health checks
curl http://localhost:8080/api/auth/status
curl http://localhost/
```

## Troubleshooting Quick Fixes

**Services won't start:**
```bash
docker compose logs
sudo systemctl status docker
```

**Can't pull images:**
```bash
docker login ghcr.io
# Or make packages public on GitHub
```

**Database issues:**
```bash
docker compose logs postgres
docker exec -it cocktaildb-postgres psql -U cocktaildb
```

**Port conflicts:**
```bash
sudo netstat -tulpn | grep -E '80|8080|5432'
sudo kill -9 <PID>
```

See **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** for detailed solutions.

## Security Checklist

Before deploying to production:

- [ ] Change default admin password
- [ ] Change default barkeeper password
- [ ] Generate new session secret
- [ ] Configure CORS for your domain
- [ ] Enable firewall (UFW)
- [ ] Set up HTTPS/SSL
- [ ] Regular database backups
- [ ] Keep Docker updated

## Support

- Documentation: `docs/` folder
- Issues: https://github.com/MrOdin2/CocktailDB/issues
- Main Guide: [DEPLOYMENT.md](docs/DEPLOYMENT.md)

## Next Steps

1. **After Initial Setup**: Test the application thoroughly
2. **Set Up HTTPS**: Use Caddy or nginx with Let's Encrypt
3. **Configure Monitoring**: Set up health check alerts
4. **Backup Strategy**: Configure off-server backups
5. **Domain Setup**: Point your domain to the server
6. **Performance Tuning**: Adjust resources as needed

## File Reference

```
.github/workflows/
  ├── ci-backend.yml          # Backend CI pipeline
  ├── ci-frontend.yml         # Frontend CI pipeline
  └── docker-build-push.yml   # Docker image builds

scripts/
  └── deploy.sh               # Deployment script

docs/
  ├── DEPLOYMENT.md           # Complete deployment guide
  ├── SERVER_SETUP.md         # Server setup instructions
  ├── CI_CD.md                # CI/CD documentation
  └── TROUBLESHOOTING.md      # Troubleshooting guide

docker-compose.prod.yml       # Production compose file
.env.example                  # Environment template
```
