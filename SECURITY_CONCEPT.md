# Security Concept for CocktailDB Access Modes

## Overview

CocktailDB supports three access modes with different permission levels:
1. **Admin Mode**: Full access to all features (CRUD operations on cocktails and ingredients)
2. **Barkeeper Mode**: Limited access (view all, modify ingredient stock only)
3. **Visitor Mode**: Read-only access (view available cocktails only)

This document outlines the security approach for implementing these access modes in a simple, lightweight manner suitable for local network deployment on devices like Raspberry Pi.

## Design Principles

- **Simplicity First**: Minimal authentication infrastructure, avoiding complex user management systems
- **Local Network Focus**: Designed for trusted local network environments, not internet-facing deployments
- **No User Registration**: Fixed credentials for admin and barkeeper roles
- **Visitor Mode Always Open**: No authentication required for visitor access
- **Environment-Based Configuration**: Credentials stored in environment variables, not in code

## Authentication Strategy

### Credential Storage

**Passwords are stored as bcrypt hashes in environment variables:**

- Use **bcrypt** for password hashing (industry-standard, resistant to brute force)
- Passwords are hashed with a work factor of 12 (configurable)
- Environment variables contain only the hashes, never plain text passwords
- Default credentials should be changed on first deployment

**Environment Variables:**
```bash
# Admin credentials
ADMIN_PASSWORD_HASH=<bcrypt hash of admin password>

# Barkeeper credentials  
BARKEEPER_PASSWORD_HASH=<bcrypt hash of barkeeper password>

# Session configuration
SESSION_SECRET=<random string for session signing>
SESSION_TIMEOUT_MINUTES=60
```

**Initial Setup:**
A command-line utility or startup script will be provided to generate password hashes:
```bash
# Example: Generate password hash
./gradlew generatePasswordHash --password=your-password-here
```

### Session Management

**Simple session-based authentication using HTTP-only cookies:**

1. **Session Creation**:
   - After successful login (username + password verification), create a session
   - Store session in memory (or Redis for production if needed)
   - Issue HTTP-only, Secure cookie with session ID
   - Session contains: user role (admin/barkeeper), creation time, last access time

2. **Session Storage**:
   - **Development**: In-memory HashMap (ConcurrentHashMap for thread safety)
   - **Production**: Optional Redis or Spring Session for persistence across restarts
   - Simple key-value store: `sessionId -> {role, createdAt, lastAccessAt}`

3. **Session Validation**:
   - Every protected endpoint checks for valid session cookie
   - Verify session exists and is not expired
   - Update last access time on each request
   - Return 401 Unauthorized if session invalid/expired

4. **Session Expiration**:
   - **Idle Timeout**: 60 minutes of inactivity (configurable via `SESSION_TIMEOUT_MINUTES`)
   - **Absolute Timeout**: Optional 8-hour maximum session lifetime
   - Cleanup task runs every 5 minutes to remove expired sessions

5. **Session Termination**:
   - Explicit logout endpoint: `/api/auth/logout`
   - Removes session from storage and clears cookie

### Authentication Flow

```
1. Login Request:
   POST /api/auth/login
   Body: { "role": "admin", "password": "user-password" }
   
2. Password Verification:
   - Retrieve hash from environment variable based on role
   - Use bcrypt to verify password against stored hash
   - If valid, create session and return success
   
3. Session Cookie:
   - Set-Cookie: sessionId=<random-uuid>; HttpOnly; Secure; SameSite=Strict; Path=/
   - Cookie automatically sent with subsequent requests
   
4. Protected Endpoints:
   - Check session cookie on every request
   - Verify role matches required permission level
   - Return 403 Forbidden if role insufficient
```

### Access Control

**Role-Based Access Control (RBAC):**

| Endpoint | Visitor | Barkeeper | Admin |
|----------|---------|-----------|-------|
| `GET /api/cocktails/available` | ✅ | ✅ | ✅ |
| `GET /api/cocktails` | ❌ | ✅ | ✅ |
| `POST /api/cocktails` | ❌ | ❌ | ✅ |
| `PUT /api/cocktails/{id}` | ❌ | ❌ | ✅ |
| `DELETE /api/cocktails/{id}` | ❌ | ❌ | ✅ |
| `GET /api/ingredients` | ❌ | ✅ | ✅ |
| `PATCH /api/ingredients/{id}/stock` | ❌ | ✅ | ✅ |
| `POST /api/ingredients` | ❌ | ❌ | ✅ |
| `PUT /api/ingredients/{id}` | ❌ | ❌ | ✅ |
| `DELETE /api/ingredients/{id}` | ❌ | ❌ | ✅ |

**Implementation:**
- Spring Security with custom filter for session validation
- `@PreAuthorize` annotations on controller methods
- Custom `SessionUser` object injected into controllers

## Implementation Details

### Backend Components

1. **AuthController** (`/api/auth`):
   - `POST /login`: Authenticate and create session
   - `POST /logout`: Terminate session
   - `GET /status`: Check current authentication status

2. **SessionService**:
   - `createSession(role)`: Generate session ID, store session data
   - `validateSession(sessionId)`: Check if session exists and is valid
   - `refreshSession(sessionId)`: Update last access time
   - `terminateSession(sessionId)`: Remove session from storage
   - `cleanupExpiredSessions()`: Scheduled task to remove old sessions

3. **PasswordService**:
   - `hashPassword(plaintext)`: Generate bcrypt hash (for setup utility)
   - `verifyPassword(plaintext, hash)`: Verify password against hash
   - `generatePasswordHash()`: CLI utility for initial setup

4. **Security Configuration**:
   - Custom `SessionAuthenticationFilter`: Intercepts requests, validates session
   - CORS configuration: Allow frontend origin
   - CSRF protection: Disabled for REST API (using SameSite cookies instead)
   - HTTP Security headers: X-Content-Type-Options, X-Frame-Options

### Frontend Components

1. **AuthService**:
   - `login(role, password)`: Call login endpoint
   - `logout()`: Call logout endpoint  
   - `getAuthStatus()`: Check current authentication state
   - `currentRole$`: Observable of current user role

2. **Route Guards**:
   - `AdminGuard`: Redirects to login if not admin
   - `BarkeeperGuard`: Redirects to login if not admin or barkeeper
   - No guard needed for visitor routes

3. **Login Component**:
   - Simple form with role selection and password input
   - Redirect to appropriate page after successful login

## Security Considerations

### Strengths

✅ **Password Security**: Bcrypt hashing with adequate work factor  
✅ **No Plain Text**: Passwords never stored or transmitted in plain text  
✅ **HTTP-only Cookies**: Prevents XSS attacks from stealing session tokens  
✅ **Secure Cookies**: HTTPS-only (in production) prevents MITM attacks  
✅ **SameSite Cookies**: Protects against CSRF attacks  
✅ **Session Expiration**: Limits exposure window if device left unattended  
✅ **Local Network Only**: Reduces attack surface compared to internet-facing deployment

### Limitations & Caveats

⚠️ **No Password Recovery**: Lost passwords require manual hash regeneration and environment variable update  
⚠️ **No Password Change UI**: Password changes require server restart with new hash  
⚠️ **Fixed Usernames**: Only two accounts (admin, barkeeper) - no custom usernames  
⚠️ **No Multi-Factor Authentication**: Simple password-only authentication  
⚠️ **No Account Lockout**: Unlimited login attempts (mitigated by local network deployment)  
⚠️ **No Audit Logging**: No tracking of who performed which actions  
⚠️ **Session Storage**: In-memory sessions lost on server restart (use Redis for persistence)  
⚠️ **Single Device**: Sessions tied to device - no cross-device session sharing

### Attack Vectors & Mitigations

| Attack Vector | Risk Level | Mitigation |
|---------------|------------|------------|
| **Brute Force Login** | Low | Bcrypt work factor slows attempts; local network limits exposure |
| **Session Hijacking** | Low | HTTP-only, Secure, SameSite cookies; HTTPS in production |
| **XSS Attacks** | Medium | HTTP-only cookies; sanitize user inputs; CSP headers |
| **CSRF Attacks** | Low | SameSite=Strict cookies; no state-changing GET requests |
| **Password Sniffing** | Medium | Use HTTPS in production; local network reduces risk |
| **Physical Access** | High | No mitigation - Raspberry Pi accessible to anyone with physical access |
| **Shoulder Surfing** | Medium | No mitigation - ensure screen privacy when entering passwords |

### Recommendations for Enhanced Security

**If deploying beyond trusted local network:**

1. **Enable HTTPS**: Use Let's Encrypt or self-signed certificates
2. **Add Rate Limiting**: Limit login attempts per IP (e.g., 5 attempts per minute)
3. **Enable Audit Logging**: Track all admin/barkeeper actions
4. **Add Account Lockout**: Temporary lockout after N failed attempts
5. **Use Redis for Sessions**: Persist sessions across server restarts
6. **Add TOTP 2FA**: Optional second factor for admin account
7. **Implement Password Change UI**: Allow password updates without server restart
8. **Add Security Headers**: CSP, HSTS, X-XSS-Protection

## Initial Configuration

### 1. Generate Password Hashes

Use the provided utility to generate bcrypt hashes:

```bash
cd backend
./gradlew generatePasswordHash --password="your-admin-password"
# Output: $2a$12$abcd... (copy this hash)

./gradlew generatePasswordHash --password="your-barkeeper-password"  
# Output: $2a$12$efgh... (copy this hash)
```

### 2. Configure Environment Variables

Create `.env` file based on `.env.example`:

```bash
# Admin password hash (change this!)
ADMIN_PASSWORD_HASH=$2a$12$...your-hash-here...

# Barkeeper password hash (change this!)
BARKEEPER_PASSWORD_HASH=$2a$12$...your-hash-here...

# Session secret for signing (generate random 64+ character string)
SESSION_SECRET=your-random-secret-key-here-change-this-value

# Session timeout in minutes (default: 60)
SESSION_TIMEOUT_MINUTES=60

# Other existing configuration...
POSTGRES_DB=cocktaildb
POSTGRES_USER=cocktaildb
POSTGRES_PASSWORD=cocktaildb
```

### 3. Generate Session Secret

Use a cryptographically secure random string:

```bash
# Linux/Mac
openssl rand -base64 64

# Or use the provided utility
./gradlew generateSessionSecret
```

### 4. Deploy and Test

```bash
# Build and start services
./build.sh
docker compose up -d

# Test visitor access (no login required)
curl http://localhost/api/cocktails/available

# Test admin login
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "your-admin-password"}' \
  -c cookies.txt

# Test authenticated request
curl http://localhost/api/ingredients \
  -b cookies.txt
```

## Password Recovery Process

**If admin/barkeeper password is lost:**

1. Stop the application:
   ```bash
   docker compose down
   ```

2. Generate new password hash:
   ```bash
   cd backend
   ./gradlew generatePasswordHash --password="new-password"
   ```

3. Update `.env` file with new hash:
   ```bash
   ADMIN_PASSWORD_HASH=$2a$12$...new-hash-here...
   ```

4. Restart the application:
   ```bash
   docker compose up -d
   ```

## Future Enhancements

**Potential improvements for future versions:**

1. **Database-Backed Sessions**: Replace in-memory sessions with database persistence
2. **Multiple Admin Accounts**: Support for multiple admin users with unique credentials
3. **Password Change API**: Allow users to change their own passwords without restart
4. **Remember Me**: Optional extended session duration (30 days)
5. **Activity Logging**: Track all admin/barkeeper actions for accountability
6. **Role Management UI**: Admin interface to manage barkeeper credentials
7. **API Key Authentication**: Alternative authentication for programmatic access
8. **OAuth Integration**: Optional login via Google/GitHub for admin accounts

## Conclusion

This security concept provides a **simple, lightweight authentication system** suitable for local network deployment on resource-constrained devices like Raspberry Pi. While not suitable for internet-facing production systems, it offers adequate security for a trusted home network environment while avoiding the complexity of full user management systems.

**Key Takeaways:**
- ✅ Secure password storage with bcrypt
- ✅ Simple session-based authentication
- ✅ Role-based access control
- ✅ No user registration complexity
- ✅ Visitor mode always accessible
- ⚠️ Best suited for local network deployments
- ⚠️ Password recovery requires manual intervention
- ⚠️ No advanced security features (2FA, rate limiting, audit logs)

For most home bartending use cases on a local network, this approach provides the right balance of security and simplicity.
