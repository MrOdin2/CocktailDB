# Server Setup Quick Guide

This is a condensed guide for setting up your Ubuntu server to run CocktailDB.

## Prerequisites

- Ubuntu Server 20.04 LTS or later
- SSH access to the server
- Sudo privileges

## Quick Setup Steps

### 1. Install Docker

```bash
# Run this one-liner to install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add your user to docker group
sudo usermod -aG docker $USER

# Install docker-compose plugin
sudo apt update
sudo apt install -y docker-compose-plugin

# Log out and back in, then verify
docker --version
docker compose version
```

### 2. Configure Firewall

```bash
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # Backend API (optional)
sudo ufw enable
```

### 3. Create Deployment Directory

```bash
mkdir -p ~/cocktaildb
cd ~/cocktaildb
```

### 4. Download Deployment Files

```bash
# Download docker-compose.prod.yml
curl -O https://raw.githubusercontent.com/MrOdin2/CocktailDB/main/docker-compose.prod.yml

# Download deployment script
mkdir -p scripts
curl -o scripts/deploy.sh https://raw.githubusercontent.com/MrOdin2/CocktailDB/main/scripts/deploy.sh
chmod +x scripts/deploy.sh

# Download .env.example
curl -O https://raw.githubusercontent.com/MrOdin2/CocktailDB/main/.env.example
cp .env.example .env
```

### 5. Configure Environment

Edit `.env` and change these critical values:

```bash
nano .env
```

**Must change:**
- `ADMIN_PASSWORD_HASH` - Generate with: `python3 -c "import bcrypt; print(bcrypt.hashpw(b'your-password', bcrypt.gensalt(12)).decode().replace('\$2b\$', '\$2a\$'))"`
- `BARKEEPER_PASSWORD_HASH` - Same as above
- `SESSION_SECRET` - Generate with: `openssl rand -base64 64`
- `CORS_ALLOWED_ORIGINS` - Set to your server IP or domain

### 6. Authenticate with GitHub Container Registry

**Option A: Make packages public (easiest)**
1. Go to GitHub.com → Your Profile → Packages
2. Click each package (backend, frontend, postgres)
3. Settings → Change visibility → Public

**Option B: Use authentication token**
```bash
# Create Personal Access Token on GitHub with 'read:packages' permission
# Then login:
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```

### 7. Deploy

```bash
./scripts/deploy.sh
```

## What Gets Installed

The deployment will create:
- PostgreSQL database (port 5432)
- Backend API (port 8080)
- Frontend web app (port 80)
- Persistent data volumes
- Automated database backups

## Access Your Application

After deployment completes:
- **Frontend:** http://YOUR_SERVER_IP
- **Backend API:** http://YOUR_SERVER_IP:8080/api
- **Default Admin Password:** `admin` (CHANGE THIS!)
- **Default Barkeeper Password:** `barkeeper` (CHANGE THIS!)

## Quick Commands

```bash
# View logs
docker compose logs -f

# Check status
docker compose ps

# Restart services
docker compose restart

# Stop everything
docker compose down

# Update to latest version
./scripts/deploy.sh

# Backup database
docker exec cocktaildb-postgres pg_dump -U cocktaildb cocktaildb > backup.sql
```

## Next Steps

1. **Change default passwords** in `.env` and redeploy
2. **Set up HTTPS** with a reverse proxy (nginx, Caddy, or Traefik)
3. **Configure domain name** if you have one
4. **Set up automated backups** to external storage
5. **Monitor logs** regularly

## Troubleshooting

**Services won't start:**
```bash
docker compose logs
```

**Can't access frontend:**
```bash
# Check firewall
sudo ufw status

# Check if port is listening
sudo netstat -tulpn | grep 80
```

**Database connection failed:**
```bash
docker compose logs postgres
docker exec -it cocktaildb-postgres psql -U cocktaildb
```

For detailed troubleshooting, see the full [Deployment Guide](DEPLOYMENT.md).

## Security Checklist

- [ ] Changed default admin password
- [ ] Changed default barkeeper password
- [ ] Generated new session secret
- [ ] Configured CORS properly
- [ ] Firewall is enabled
- [ ] Regular backups configured
- [ ] HTTPS/SSL configured (recommended)

## Getting Help

- Full documentation: See [DEPLOYMENT.md](DEPLOYMENT.md)
- Issues: https://github.com/MrOdin2/CocktailDB/issues
