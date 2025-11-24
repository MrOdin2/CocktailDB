# Deployment Troubleshooting Guide

This guide helps you diagnose and fix common deployment issues with CocktailDB.

## Quick Diagnostics

### Check System Status

```bash
# Check all services
docker compose ps

# Check container logs
docker compose logs --tail=50

# Check specific service
docker compose logs backend
docker compose logs frontend
docker compose logs postgres

# Check Docker system
docker system df
docker system info
```

### Health Checks

```bash
# Backend health
curl http://localhost:8080/api/auth/status

# Frontend health
curl http://localhost/

# Database health
docker exec cocktaildb-postgres pg_isready -U cocktaildb
```

## Common Issues

### 1. Services Won't Start

**Symptom:** `docker compose up` fails or containers immediately exit

**Diagnosis:**
```bash
# Check container status
docker compose ps

# Check logs for errors
docker compose logs

# Check Docker daemon
sudo systemctl status docker
```

**Solutions:**

**Port Already in Use:**
```bash
# Find process using port 80
sudo netstat -tulpn | grep :80

# Kill the process
sudo kill -9 <PID>

# Or change port in docker-compose.yml
ports:
  - "8080:80"  # Use port 8080 instead
```

**Insufficient Permissions:**
```bash
# Ensure you're in docker group
sudo usermod -aG docker $USER

# Log out and back in
exit

# Or run with sudo (not recommended for production)
sudo docker compose up -d
```

**Docker Daemon Not Running:**
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

### 2. Cannot Pull Docker Images

**Symptom:** `Error response from daemon: pull access denied`

**Diagnosis:**
```bash
# Check if logged in
docker login ghcr.io

# Try pulling manually
docker pull ghcr.io/mrodin2/cocktaildb/backend:latest
```

**Solutions:**

**Authentication Required:**
```bash
# Option 1: Login with Personal Access Token
echo "YOUR_GITHUB_TOKEN" | docker login ghcr.io -u YOUR_USERNAME --password-stdin

# Option 2: Make packages public on GitHub
# Go to GitHub → Your Profile → Packages → Change visibility to Public
```

**Network Issues:**
```bash
# Check connectivity
ping ghcr.io
curl https://ghcr.io

# Check DNS
nslookup ghcr.io

# Try with different DNS
echo "nameserver 8.8.8.8" | sudo tee /etc/resolv.conf
```

### 3. Database Connection Failed

**Symptom:** Backend logs show `Connection refused` or `Unknown host`

**Diagnosis:**
```bash
# Check if postgres is running
docker ps | grep postgres

# Check postgres logs
docker compose logs postgres

# Check connection from backend container
docker exec cocktaildb-backend ping postgres

# Test database connection
docker exec -it cocktaildb-postgres psql -U cocktaildb
```

**Solutions:**

**PostgreSQL Not Ready:**
```bash
# Check health status
docker inspect cocktaildb-postgres --format='{{.State.Health.Status}}'

# Wait for healthy status
while [ "$(docker inspect cocktaildb-postgres --format='{{.State.Health.Status}}')" != "healthy" ]; do
  echo "Waiting for postgres..."
  sleep 2
done
```

**Wrong Credentials:**
```bash
# Check .env file
cat .env | grep -E 'DATABASE|POSTGRES'

# Ensure they match in docker-compose
docker compose config | grep -A 10 environment
```

**Network Issues:**
```bash
# Check Docker network
docker network ls
docker network inspect cocktaildb_default

# Recreate network
docker compose down
docker compose up -d
```

### 4. Frontend Not Accessible

**Symptom:** `Connection refused` or `This site can't be reached`

**Diagnosis:**
```bash
# Check if frontend is running
docker ps | grep frontend

# Check frontend logs
docker compose logs frontend

# Test locally on server
curl http://localhost

# Check if port is open
sudo netstat -tulpn | grep :80
```

**Solutions:**

**Firewall Blocking:**
```bash
# Check firewall status
sudo ufw status

# Allow HTTP traffic
sudo ufw allow 80/tcp

# For remote access, allow from specific IP
sudo ufw allow from 192.168.1.0/24 to any port 80
```

**nginx Configuration Error:**
```bash
# Check nginx config inside container
docker exec cocktaildb-frontend cat /etc/nginx/conf.d/default.conf

# Test nginx config
docker exec cocktaildb-frontend nginx -t

# Reload nginx
docker exec cocktaildb-frontend nginx -s reload
```

**Wrong Binding Address:**
```bash
# Check docker-compose.yml
# Should be "0.0.0.0:80:80" for remote access
# Not "127.0.0.1:80:80"
```

### 5. Backend API Not Responding

**Symptom:** API endpoints return 500 errors or timeout

**Diagnosis:**
```bash
# Check backend logs
docker compose logs backend --tail=100

# Test API endpoint
curl -v http://localhost:8080/api/auth/status

# Check Java process
docker exec cocktaildb-backend ps aux | grep java

# Check resource usage
docker stats cocktaildb-backend
```

**Solutions:**

**Out of Memory:**
```bash
# Check memory usage
free -h
docker stats

# Increase container memory limit in docker-compose.yml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 2G
```

**Database Migration Failed:**
```bash
# Check Flyway migration logs
docker compose logs backend | grep -i flyway

# Reset and migrate manually if needed
docker exec -it cocktaildb-postgres psql -U cocktaildb
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\q

# Restart backend to run migrations
docker compose restart backend
```

**CORS Issues:**
```bash
# Check CORS configuration
docker compose logs backend | grep -i cors

# Update .env
CORS_ALLOWED_ORIGINS=http://192.168.1.100,http://localhost

# Restart backend
docker compose restart backend
```

### 6. GitHub Actions Workflow Failures

**Symptom:** Workflows fail in GitHub Actions

**Diagnosis:**
```bash
# Check workflow run on GitHub
# Go to: Repository → Actions → Click on failed run
```

**Solutions:**

**Build Failures:**
```
# Backend build fails:
- Check Java version (should be 17)
- Check Gradle version compatibility
- Clear Gradle cache in Actions

# Frontend build fails:
- Check Node.js version (should be 22)
- Delete package-lock.json and regenerate
- Check for TypeScript errors
```

**Docker Push Permission Denied:**
```bash
# Check workflow permissions
# Repository Settings → Actions → General → Workflow permissions
# Select "Read and write permissions"

# Or add permissions in workflow file (already done)
permissions:
  contents: read
  packages: write
```

**Secrets Not Available:**
```bash
# Check repository secrets
# Repository Settings → Secrets and variables → Actions
# Ensure GITHUB_TOKEN is available (automatic)
```

### 7. Deployment Script Fails

**Symptom:** `deploy.sh` exits with error

**Diagnosis:**
```bash
# Run with debug mode
bash -x scripts/deploy.sh

# Check prerequisites
docker --version
docker compose version
```

**Solutions:**

**Backup Fails:**
```bash
# Check if postgres is running
docker ps | grep postgres

# If postgres is down, skip backup
# Comment out backup_database call in deploy.sh
```

**Image Pull Fails:**
```bash
# Ensure you're logged in
docker login ghcr.io

# Pull images manually to test
docker pull ghcr.io/mrodin2/cocktaildb/backend:latest
```

**Rollback Needed:**
```bash
# Manual rollback
docker compose down

# Restore from latest backup
ls -lt backup_*.sql | head -1
docker compose up -d postgres
sleep 10
docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < backup_YYYYMMDD_HHMMSS.sql

# Start all services
docker compose up -d
```

### 8. SSL/HTTPS Issues

**Symptom:** Need to add HTTPS support

**Solution - Using Caddy (Recommended):**

```bash
# Install Caddy
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy

# Create Caddyfile
sudo tee /etc/caddy/Caddyfile > /dev/null <<EOF
yourdomain.com {
    reverse_proxy localhost:80
}

api.yourdomain.com {
    reverse_proxy localhost:8080
}
EOF

# Restart Caddy
sudo systemctl restart caddy

# Update docker-compose.yml to bind to localhost
ports:
  - "127.0.0.1:80:80"
  - "127.0.0.1:8080:8080"
```

### 9. Performance Issues

**Symptom:** Application is slow

**Diagnosis:**
```bash
# Check resource usage
docker stats

# Check disk space
df -h

# Check I/O
iostat -x 1 5

# Check network
iftop
```

**Solutions:**

**High Memory Usage:**
```bash
# Increase swap
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make permanent
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

**Disk Space Full:**
```bash
# Clean up Docker
docker system prune -a
docker volume prune

# Clean old backups
find . -name "backup_*.sql" -mtime +7 -delete

# Clean logs
sudo journalctl --vacuum-time=7d
```

**Database Performance:**
```bash
# Vacuum database
docker exec -it cocktaildb-postgres psql -U cocktaildb -c "VACUUM ANALYZE;"

# Check table sizes
docker exec -it cocktaildb-postgres psql -U cocktaildb -c "\dt+"
```

## Prevention and Monitoring

### Regular Maintenance

```bash
# Weekly cleanup
docker system prune -a

# Monthly database vacuum
docker exec -it cocktaildb-postgres psql -U cocktaildb -c "VACUUM ANALYZE;"

# Check disk space
df -h

# Review logs
docker compose logs --tail=100 --since 24h
```

### Monitoring Setup

```bash
# Basic monitoring script
cat > /usr/local/bin/cocktaildb-monitor.sh <<'EOF'
#!/bin/bash
# Check if services are running
if ! docker ps | grep -q cocktaildb-backend; then
    echo "Backend is down!" | mail -s "CocktailDB Alert" admin@example.com
fi
EOF

chmod +x /usr/local/bin/cocktaildb-monitor.sh

# Add to crontab
crontab -e
# Add: */5 * * * * /usr/local/bin/cocktaildb-monitor.sh
```

### Backup Strategy

```bash
# Automated backup script
cat > /usr/local/bin/cocktaildb-backup.sh <<'EOF'
#!/bin/bash
BACKUP_DIR=/var/backups/cocktaildb
mkdir -p $BACKUP_DIR
cd $BACKUP_DIR

# Backup database
docker exec cocktaildb-postgres pg_dump -U cocktaildb cocktaildb > cocktaildb_$(date +%Y%m%d_%H%M%S).sql

# Keep only last 14 days
find $BACKUP_DIR -name "cocktaildb_*.sql" -mtime +14 -delete

# Optional: Upload to cloud storage
# aws s3 sync $BACKUP_DIR s3://your-bucket/cocktaildb-backups/
EOF

chmod +x /usr/local/bin/cocktaildb-backup.sh

# Add to crontab for daily backups
crontab -e
# Add: 0 2 * * * /usr/local/bin/cocktaildb-backup.sh
```

## Getting Help

If you still have issues:

1. **Check logs thoroughly:**
   ```bash
   docker compose logs > full-logs.txt
   ```

2. **Gather system information:**
   ```bash
   docker version
   docker compose version
   uname -a
   free -h
   df -h
   ```

3. **Create a GitHub issue with:**
   - Description of the problem
   - Error messages
   - System information
   - Steps to reproduce

4. **Community resources:**
   - GitHub Issues: https://github.com/MrOdin2/CocktailDB/issues
   - Documentation: See docs/ folder
   - Stack Overflow: Tag with `docker`, `spring-boot`, `angular`

## Emergency Recovery

### Complete Reset

```bash
# WARNING: This will delete ALL data!

# Stop and remove everything
docker compose down -v

# Remove images
docker rmi $(docker images | grep cocktaildb | awk '{print $3}')

# Clean system
docker system prune -a

# Restore from backup
# ... copy backup file ...

# Fresh start
./scripts/deploy.sh

# Restore data
docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < your-backup.sql
```

### Disaster Recovery

```bash
# If server is completely broken:

# 1. Backup what you can
scp user@server:~/cocktaildb/backup_*.sql ./

# 2. Set up new server following SERVER_SETUP.md

# 3. Deploy application
./scripts/deploy.sh

# 4. Restore data
docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < backup.sql

# 5. Verify
curl http://your-server/api/auth/status
```
