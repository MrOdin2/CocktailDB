# Local Network Testing Guide

This guide explains how to run CocktailDB and access it from multiple devices on your local network.

## Overview

CocktailDB is now configured to support access from multiple devices (phones, tablets, laptops) on your local network. This is useful for:
- Testing the application on different devices
- Allowing multiple users to access the same instance
- Demonstrating the application to others on your network

## Quick Start

### 1. Find Your Server's IP Address

First, find the IP address of the machine running CocktailDB:

**On Linux/macOS:**
```bash
# Find your local IP address
ip addr show | grep "inet " | grep -v 127.0.0.1

# Or on macOS
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**On Windows:**
```cmd
ipconfig
```

Look for your local network IP address, typically in the format:
- `192.168.x.x` (most home networks)
- `10.x.x.x` (some corporate networks)
- `172.16.x.x` to `172.31.x.x` (some networks)

Example: `192.168.1.100`

### 2. Start CocktailDB with Docker Compose

```bash
# Clone the repository
git clone https://github.com/MrOdin2/CocktailDB.git
cd CocktailDB

# (Optional) Create .env file from example
cp .env.example .env

# Build the application
./build.sh

# Start all services
docker compose up -d
```

### 3. Access from Other Devices

Once running, you can access CocktailDB from any device on the same network:

**On the server machine:**
- Frontend: `http://localhost`
- API: `http://localhost:8080/api`

**On other devices (replace `192.168.1.100` with your server's IP):**
- Frontend: `http://192.168.1.100`
- API: `http://192.168.1.100:8080/api`

## Configuration Details

### CORS (Cross-Origin Resource Sharing)

By default, the production configuration allows requests from all origins (`*`). This is ideal for local network testing but should be restricted in production environments.

To customize allowed origins, edit your `.env` file:

```bash
# Allow all origins (default for local testing)
CORS_ALLOWED_ORIGINS=*

# Or restrict to specific IP addresses/domains
CORS_ALLOWED_ORIGINS=http://192.168.1.100,http://192.168.1.101,http://192.168.1.102
```

After changing `.env`, restart the services:
```bash
docker compose down
docker compose up -d
```

### Port Bindings

The docker-compose configuration binds services to all network interfaces (`0.0.0.0`):
- Frontend (nginx): Port 80 on all interfaces
- Backend (Spring Boot): Port 8080 on all interfaces
- PostgreSQL: Port 5432 (only if you need direct database access)

### Network Requirements

Ensure that:
1. All devices are on the same local network
2. Your firewall allows incoming connections on ports 80 and 8080
3. No other services are using ports 80 or 8080

## Firewall Configuration

### Linux (UFW)

```bash
sudo ufw allow 80/tcp
sudo ufw allow 8080/tcp
sudo ufw reload
```

### Linux (firewalld)

```bash
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### macOS

macOS typically allows local network traffic by default. If you have issues:
1. Go to System Preferences → Security & Privacy → Firewall
2. Click "Firewall Options"
3. Ensure "Block all incoming connections" is not enabled

### Windows

1. Open Windows Defender Firewall
2. Click "Advanced settings"
3. Create inbound rules for ports 80 and 8080

Or use PowerShell (as Administrator):
```powershell
New-NetFirewallRule -DisplayName "CocktailDB HTTP" -Direction Inbound -LocalPort 80 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "CocktailDB API" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

## Testing Multiple Devices

### Recommended Test Scenarios

1. **Phone/Tablet Browser**
   - Open Safari/Chrome on your mobile device
   - Navigate to `http://[SERVER_IP]`
   - Test responsive design and touch interactions

2. **Different Laptops**
   - Access from multiple computers simultaneously
   - Test concurrent usage and data synchronization

3. **Mixed Device Types**
   - Combine phones, tablets, and laptops
   - Verify consistent behavior across device types

### Expected Device Range

The application can handle 5-20 simultaneous devices depending on:
- Server hardware resources
- Network bandwidth
- Concurrent active users

## Troubleshooting

### Cannot Access from Other Devices

1. **Verify server is running:**
   ```bash
   docker compose ps
   ```
   All services should show "Up" status.

2. **Check firewall:**
   Temporarily disable firewall to test if it's blocking access.

3. **Verify IP address:**
   Ensure you're using the correct local IP address (not 127.0.0.1).

4. **Check network connectivity:**
   ```bash
   # On client device, ping the server
   ping 192.168.1.100
   ```

5. **Verify port binding:**
   ```bash
   # On server, check if ports are listening on all interfaces
   netstat -tuln | grep -E ':(80|8080)'
   ```
   Should show `0.0.0.0:80` and `0.0.0.0:8080`, not `127.0.0.1:80`.

### CORS Errors in Browser Console

If you see CORS errors:
1. Check that `CORS_ALLOWED_ORIGINS` in `.env` is set to `*` or includes your client's origin
2. Restart services after changing `.env`
3. Clear browser cache

### Backend API Not Responding

1. Check backend logs:
   ```bash
   docker compose logs backend
   ```

2. Verify backend health:
   ```bash
   curl http://localhost:8080/api/ingredients
   ```

3. Restart backend if needed:
   ```bash
   docker compose restart backend
   ```

## Security Considerations

### For Local Testing

The default configuration (`CORS_ALLOWED_ORIGINS=*`) is appropriate for:
- Home networks
- Private development networks
- Local testing environments

### For Production Deployment

If deploying to a public network or production environment:

1. **Restrict CORS origins:**
   ```bash
   CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
   ```

2. **Use HTTPS:**
   - Configure SSL/TLS certificates
   - Update nginx configuration for HTTPS

3. **Configure authentication:**
   - Review the [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) file
   - Set strong passwords in the `.env` file
   - Change default session secrets

4. **Network security:**
   - Use a reverse proxy (nginx, Apache)
   - Implement rate limiting
   - Consider using a VPN for remote access

## Advanced Configuration

### Custom Ports

To use different ports, edit `docker-compose.yml`:

```yaml
frontend:
  ports:
    - "0.0.0.0:8081:80"  # Use port 8081 instead of 80

backend:
  ports:
    - "0.0.0.0:8082:8080"  # Use port 8082 instead of 8080
```

### Development Mode on Local Network

For development with hot-reload:

1. **Backend:**
   ```bash
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=dev-postgres'
   ```
   Access at `http://[SERVER_IP]:8080`

2. **Frontend:**
   Update `frontend/src/environments/environment.development.ts`:
   ```typescript
   export const environment = {
     production: false,
     apiUrl: 'http://[SERVER_IP]:8080/api'
   };
   ```
   
   Then run:
   ```bash
   cd frontend
   npm start -- --host 0.0.0.0
   ```
   Access at `http://[SERVER_IP]:4200`

## Additional Resources

- [Main README](../README.md) - General project documentation
- [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Security architecture
- [Docker Documentation](https://docs.docker.com/) - Docker and Docker Compose reference

## Support

If you encounter issues not covered in this guide:
1. Check the GitHub issues page
2. Create a new issue with:
   - Your setup details (OS, Docker version)
   - Error messages or logs
   - Steps to reproduce the problem
