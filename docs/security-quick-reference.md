# Security Quick Reference

Quick reference for CocktailDB's three-tier access control system.

## Three Access Modes

### 🎯 Visitor Mode
- **Access**: Public - No login required
- **Entry Point**: `http://localhost/` or `http://localhost/visitor`
- **Purpose**: Browse available cocktails
- **Features**: View available cocktails, recipes, random picker, categories
- **Restrictions**: Cannot see all cocktails, cannot modify anything

### 🍸 Barkeeper Mode  
- **Access**: Authenticated - Barkeeper password required
- **Entry Point**: `http://localhost/login` → Select "Barkeeper"
- **Purpose**: Professional bar service interface
- **Features**: All cocktails, stock management, search, random picker
- **Restrictions**: Cannot create/edit/delete cocktails or ingredients (only toggle stock)

### ⚙️ Admin Mode
- **Access**: Authenticated - Admin password required
- **Entry Point**: `http://localhost/login` → Select "Admin"
- **Purpose**: Complete database management
- **Features**: Full CRUD on cocktails and ingredients, visualizations, settings
- **Restrictions**: None - full system access

## Default Credentials (CHANGE THESE!)

- **Admin**: username: `admin`, password: `admin`
- **Barkeeper**: username: `barkeeper`, password: `barkeeper`

⚠️ **These are defaults for development only. Change them before deployment!**

## Quick Setup

### 1. Generate Password Hash

**Using online tool (easiest):**
1. Go to https://bcrypt-generator.com/
2. Enter your password
3. Set rounds to **12**
4. Click "Generate Hash"
5. Copy the hash to your `.env` file

**Using command line (Linux/Mac):**
```bash
# Install htpasswd (usually part of apache2-utils)
sudo apt-get install apache2-utils

# Generate hash (will prompt for password)
htpasswd -nbB -C 12 admin password | cut -d: -f2
```

**Using Python:**
```python
import bcrypt
password = b"your-password-here"
hash = bcrypt.hashpw(password, bcrypt.gensalt(rounds=12))
print(hash.decode('utf-8'))
```

**Using Node.js:**
```bash
npm install -g bcrypt-cli
bcrypt-cli "your-password-here" 12
```

### 2. Generate Session Secret

```bash
# Linux/Mac
openssl rand -base64 64

# Or use this Python one-liner
python3 -c "import secrets; print(secrets.token_urlsafe(48))"

# Or use Node.js
node -e "console.log(require('crypto').randomBytes(48).toString('base64'))"
```

### 3. Update .env File

```bash
# Copy example file
cp .env.example .env

# Edit with your favorite editor
nano .env  # or vim, code, etc.
```

Update these lines:
```bash
ADMIN_PASSWORD_HASH=<paste-your-admin-hash-here>
BARKEEPER_PASSWORD_HASH=<paste-your-barkeeper-hash-here>
SESSION_SECRET=<paste-your-secret-here>
```

## Access Permissions Table

| Feature | Visitor | Barkeeper | Admin |
|---------|---------|-----------|-------|
| **Viewing** |
| View available cocktails | ✅ | ✅ | ✅ |
| View all cocktails | ❌ | ✅ | ✅ |
| View all ingredients | ❌ | ✅ | ✅ |
| View cocktail recipe details | ✅ | ✅ | ✅ |
| Search cocktails | ✅ (available only) | ✅ (all) | ✅ (all) |
| Random cocktail picker | ✅ | ✅ | ✅ |
| Browse by categories | ✅ | ✅ | ✅ |
| **Stock Management** |
| Update ingredient stock | ❌ | ✅ | ✅ |
| **Cocktail Management** |
| Create new cocktails | ❌ | ❌ | ✅ |
| Edit existing cocktails | ❌ | ❌ | ✅ |
| Delete cocktails | ❌ | ❌ | ✅ |
| **Ingredient Management** |
| Create new ingredients | ❌ | ❌ | ✅ |
| Edit existing ingredients | ❌ | ❌ | ✅ |
| Delete ingredients | ❌ | ❌ | ✅ |
| **Admin Features** |
| View visualizations | ❌ | ❌ | ✅ |
| Access settings | ❌ | ❌ | ✅ |

## User Flows

### Visitor Flow (No Login)
```
1. Navigate to http://localhost/
2. Automatically redirected to /visitor
3. Browse available cocktails
4. View recipes
5. Use random picker
6. Browse categories
```

### Barkeeper Flow
```
1. Navigate to http://localhost/login
2. Select "Barkeeper" role
3. Enter barkeeper password
4. Redirected to /barkeeper/menu
5. Access all cocktails and ingredients
6. Toggle ingredient stock as needed
7. Click "Logout" when done
```

### Admin Flow
```
1. Navigate to http://localhost/login
2. Select "Admin" role
3. Enter admin password
4. Redirected to /cocktails
5. Manage cocktails and ingredients (full CRUD)
6. Access visualizations and settings
7. Click "Logout" when done
```

## API Quick Reference

### Login

```bash
# Admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "your-password"}' \
  -c cookies.txt

# Barkeeper login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "barkeeper", "password": "your-password"}' \
  -c cookies.txt
```

### Check Status

```bash
curl http://localhost:8080/api/auth/status -b cookies.txt
```

### Logout

```bash
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt
```

### Access Protected Endpoint

```bash
# Get all ingredients (requires barkeeper or admin)
curl http://localhost:8080/api/ingredients -b cookies.txt

# Create ingredient (requires admin)
curl -X POST http://localhost:8080/api/ingredients \
  -H "Content-Type: application/json" \
  -d '{"name":"Vodka","type":"SPIRIT","abv":40,"inStock":true}' \
  -b cookies.txt
```

## Password Recovery

If you forget your password:

1. Stop the application:
   ```bash
   docker compose down
   ```

2. Generate new password hash (see "Generate Password Hash" above)

3. Update `.env` file with new hash

4. Restart the application:
   ```bash
   docker compose up -d
   ```

## Troubleshooting

### Login Fails

```bash
# Check backend logs
docker compose logs backend

# Verify password hash is correct
echo "Check that ADMIN_PASSWORD_HASH in .env matches your password"

# Test with default credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "admin"}' \
  -v
```

### Session Issues

```bash
# Check session timeout setting
grep SESSION_TIMEOUT_MINUTES .env

# Clear browser cookies
# (In browser DevTools: Application > Cookies > Delete sessionId)

# Restart backend to clear sessions
docker compose restart backend
```

### CORS Issues

```bash
# Check if frontend can reach backend
curl http://localhost:8080/api/auth/status

# If CORS error in browser, check backend logs
docker compose logs backend | grep CORS
```

## Security Checklist

Before going live:

```bash
# 1. Check passwords are changed
grep "admin.*password" .env  # Should NOT show default hash
grep "barkeeper.*password" .env  # Should NOT show default hash

# 2. Verify session secret is set
grep SESSION_SECRET .env  # Should be a long random string

# 3. Check all environment variables
cat .env

# 4. Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "your-new-password"}' \
  -c test-cookies.txt

# 5. Test protected endpoint
curl http://localhost:8080/api/ingredients -b test-cookies.txt

# 6. Clean up test files
rm test-cookies.txt
```

## Environment Variables Reference

```bash
# Required Security Variables
ADMIN_PASSWORD_HASH=<bcrypt hash>           # Admin password hash
BARKEEPER_PASSWORD_HASH=<bcrypt hash>       # Barkeeper password hash
SESSION_SECRET=<random string>              # Secret for session signing

# Optional Security Variables
SESSION_TIMEOUT_MINUTES=60                  # Session idle timeout (default: 60)
BCRYPT_WORK_FACTOR=12                       # Bcrypt rounds (default: 12)

# Database Variables (existing)
POSTGRES_DB=cocktaildb
POSTGRES_USER=cocktaildb
POSTGRES_PASSWORD=cocktaildb
DATABASE_URL=jdbc:postgresql://postgres:5432/cocktaildb
DATABASE_USERNAME=cocktaildb
DATABASE_PASSWORD=cocktaildb
SPRING_PROFILES_ACTIVE=prod
```

## Common Tasks

### Change Admin Password

1. Generate new hash
2. Update `ADMIN_PASSWORD_HASH` in `.env`
3. Restart: `docker compose restart backend`

### Change Barkeeper Password

1. Generate new hash
2. Update `BARKEEPER_PASSWORD_HASH` in `.env`
3. Restart: `docker compose restart backend`

### Increase Session Timeout

1. Edit `.env`: `SESSION_TIMEOUT_MINUTES=120` (2 hours)
2. Restart: `docker compose restart backend`

### Reset All Sessions

```bash
docker compose restart backend
```

## Getting Help

- **Full Security Concept**: See [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md)
- **Implementation Guide**: See [authentication-guide.md](authentication-guide.md)
- **API Documentation**: See main [README.md](../README.md)
- **Issues**: https://github.com/MrOdin2/CocktailDB/issues
