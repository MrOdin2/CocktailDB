# Security Concept for CocktailDB Access Modes

## Overview

CocktailDB implements a three-tier access control system designed to support different use cases while maintaining simplicity and security. Each mode serves a distinct purpose and provides appropriate access levels for its intended users.

### The Three Access Modes

#### 1. Visitor Mode (Public Interface)
**Purpose**: Public cocktail discovery without barriers  
**Authentication**: None required  
**Target Users**: Guests, friends, party attendees, anyone wanting to browse available cocktails  
**Philosophy**: Zero friction access to discover what cocktails can be made

**Key Features**:
- Browse cocktails that can be made with current ingredient stock
- View detailed recipes with step-by-step instructions
- Random cocktail picker with filtering (alcoholic/non-alcoholic, by spirit type)
- Category-based browsing
- Mobile-optimized interface for viewing at parties or gatherings

**UI Characteristics**:
- Simple, clean menu-driven interface
- Large, touch-friendly buttons
- No complex forms or editing tools
- Immediate access without login prompts

#### 2. Barkeeper Mode (Professional Interface)
**Purpose**: Fast, efficient cocktail service tools for working bartenders  
**Authentication**: Required - barkeeper credentials  
**Target Users**: Professional or home bartenders actively making drinks  
**Philosophy**: Speed and efficiency - quick access to all recipes and stock updates

**Key Features**:
- Access to complete cocktail library (not limited to available cocktails)
- Alphabetical index for rapid recipe lookup
- Quick stock management - mark ingredients as in/out of stock on the fly
- Advanced search and filtering capabilities
- Random cocktail suggestions with professional filters
- Streamlined navigation optimized for mobile/tablet use at the bar

**UI Characteristics**:
- Fast-access menu with minimal navigation depth
- Stock toggle switches that don't require form submission
- Optimized for one-handed operation on mobile devices
- Professional color scheme and layout
- Logout button visible in menu for easy session end

#### 3. Admin Mode (Complete Management)
**Purpose**: Full system administration and recipe database management  
**Authentication**: Required - admin credentials  
**Target Users**: Home bartenders building and maintaining their cocktail collection  
**Philosophy**: Complete control - manage entire cocktail and ingredient database

**Key Features**:
- Create, edit, and delete cocktails with full recipe editing
- Create, edit, and delete ingredients
- Data visualizations and analytics (usage trends, ingredient statistics)
- System configuration and settings
- Import/export functionality for backup and sharing
- All features available in barkeeper mode plus full editing capabilities

**UI Characteristics**:
- Table-based views with CRUD operations
- Comprehensive forms for detailed data entry
- Data visualization dashboards
- Desktop-optimized layout (responsive to mobile)
- Full navigation menu with all administrative tools

### Access Mode Separation Rationale

**Why three separate modes instead of one flexible interface?**

1. **Security**: Clear separation of capabilities - visitors can't accidentally or maliciously modify data
2. **User Experience**: Each mode optimized for its specific use case (discovery vs. service vs. management)
3. **Performance**: Visitor mode loads minimal data (only available cocktails) for faster access
4. **Simplicity**: Each interface only shows relevant features, reducing cognitive load
5. **Access Control**: Easy to implement role-based restrictions at both API and UI levels

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

## Navigation & User Flows

### Entry Points and Routes

**Default Landing Page**: `http://[host]/` → Redirects to `/visitor`

All three modes have distinct URL prefixes for clear separation:
- **Visitor Mode**: `/visitor/*`
- **Barkeeper Mode**: `/barkeeper/*`
- **Admin Mode**: `/cocktails`, `/ingredients`, `/visualizations`, `/settings`

### Visitor Mode User Flow

**Entry**: No authentication required - direct access to all visitor routes

**Available Routes**:
1. `/visitor` - Main menu with feature cards
2. `/visitor/cocktails` - List of available cocktails
3. `/visitor/recipe/:id` - Detailed recipe view
4. `/visitor/random` - Random cocktail picker with filters
5. `/visitor/categories` - Browse cocktails by category

**Navigation Pattern**:
```
Landing Page (/)
    ↓ (auto-redirect)
Visitor Menu (/visitor)
    ├─→ Cocktail List (/visitor/cocktails)
    │      ├─→ Recipe Detail (/visitor/recipe/:id)
    │      └─→ Back to Menu
    ├─→ Random Picker (/visitor/random)
    │      ├─→ View Recipe (links to /visitor/recipe/:id)
    │      └─→ Back to Menu
    └─→ Categories (/visitor/categories)
           ├─→ Cocktail List (filtered)
           └─→ Back to Menu
```

**UI Cues**:
- No login button visible
- Simple "Menu" or "Back" buttons for navigation
- Large, touch-friendly cards and buttons
- No editing tools or forms
- Public indicator in header (optional)

**Browser Navigation**: 
- Back/Forward buttons work correctly
- Each view has its own URL for bookmarking
- Refresh maintains current view

### Barkeeper Mode User Flow

**Entry**: Requires authentication - redirect to `/login` if not authenticated

**Login Process**:
1. User navigates to `/login` or any barkeeper route
2. If not authenticated, redirected to `/login`
3. User selects "Barkeeper" role from dropdown
4. User enters barkeeper password
5. On success, redirected to `/barkeeper/menu`
6. Session cookie stored in browser

**Available Routes**:
1. `/barkeeper/menu` - Main menu (default after login)
2. `/barkeeper/alphabet` - Alphabetical cocktail index
3. `/barkeeper/cocktails` - Full cocktail list
4. `/barkeeper/recipe/:id` - Recipe detail view
5. `/barkeeper/random` - Random cocktail picker
6. `/barkeeper/stock` - Stock management interface

**Navigation Pattern**:
```
Login (/login)
    ↓ (select barkeeper, authenticate)
Barkeeper Menu (/barkeeper/menu)
    ├─→ Alphabet Index (/barkeeper/alphabet)
    │      ├─→ Recipe Detail (/barkeeper/recipe/:id)
    │      └─→ Back to Menu
    ├─→ Cocktail List (/barkeeper/cocktails)
    │      ├─→ Recipe Detail (/barkeeper/recipe/:id)
    │      └─→ Back to Menu
    ├─→ Random Picker (/barkeeper/random)
    │      ├─→ View Recipe (links to /barkeeper/recipe/:id)
    │      └─→ Back to Menu
    ├─→ Stock Management (/barkeeper/stock)
    │      ├─→ Toggle ingredient stock
    │      └─→ Back to Menu
    └─→ Logout
           ↓
       Login Page (/login)
```

**UI Cues**:
- "Barkeeper Mode" or "🍸" indicator in header
- "Logout" button visible in menu
- Professional color scheme (different from visitor mode)
- Stock toggle switches visible
- No delete/edit buttons for cocktails or ingredients

**Session Behavior**:
- Session expires after 60 minutes of inactivity (configurable)
- Expired session redirects to login with message
- Manual logout clears session immediately
- Session persists across page refreshes

### Admin Mode User Flow

**Entry**: Requires admin authentication - redirect to `/login` if not authenticated

**Login Process**:
1. User navigates to `/login` or any admin route
2. If not authenticated, redirected to `/login`
3. User selects "Admin" role from dropdown
4. User enters admin password
5. On success, redirected to `/cocktails` (default admin page)
6. Session cookie stored in browser

**Available Routes**:
1. `/cocktails` - Cocktail management (default after login)
2. `/ingredients` - Ingredient management
3. `/visualizations` - Data analytics and charts
4. `/settings` - System configuration

**Navigation Pattern**:
```
Login (/login)
    ↓ (select admin, authenticate)
Admin Dashboard (/cocktails)
    ├─→ Ingredients (/ingredients)
    │      ├─→ Add New Ingredient
    │      ├─→ Edit Ingredient
    │      ├─→ Delete Ingredient
    │      └─→ View Stock Status
    ├─→ Cocktails (/cocktails)
    │      ├─→ Add New Cocktail
    │      ├─→ Edit Cocktail
    │      ├─→ Delete Cocktail
    │      └─→ Filter Available/All
    ├─→ Visualizations (/visualizations)
    │      ├─→ Ingredient Charts
    │      ├─→ Cocktail Trends
    │      └─→ Usage Statistics
    ├─→ Settings (/settings)
    │      └─→ System Configuration
    └─→ Logout
           ↓
       Login Page (/login)
```

**UI Cues**:
- "Admin Mode" or "⚙️" indicator in header
- Full navigation sidebar or top menu
- CRUD buttons visible (Add, Edit, Delete)
- Data tables with sorting and filtering
- Form-based interfaces for creating/editing
- Professional/technical interface design

**Session Behavior**:
- Same session timeout as barkeeper (60 min configurable)
- Unsaved changes warning before navigation (future enhancement)
- Session persists across page refreshes

### Access Boundary Enforcement

**Route Guards**:
- **adminGuard**: Checks for `role === 'ADMIN'`
  - If not authenticated: redirect to `/login`
  - If authenticated as barkeeper: redirect to `/barkeeper`
- **barkeeperGuard**: Checks for `role === 'BARKEEPER' || role === 'ADMIN'`
  - If not authenticated: redirect to `/login`
  - Admin users can access barkeeper routes
- **No guard for visitor routes**: Always accessible

**API-Level Enforcement**:
- Backend validates all requests independently of frontend guards
- Session cookie required for protected endpoints
- Role verification on every API call
- Returns 401 Unauthorized if session missing/invalid
- Returns 403 Forbidden if role insufficient

**Cross-Mode Prevention**:
- Visitors cannot access barkeeper/admin APIs even with direct URL manipulation
- Barkeepers cannot access admin APIs (server validates role)
- Client-side guards prevent unnecessary API calls
- All state-changing operations require authentication

### UI Access Indicators

To make access boundaries clear to users, each mode should display:

**Visitor Mode**:
- Header: "CocktailDB - Discover Drinks" or simple logo
- No authentication indicator needed
- No user menu or logout button
- Simple navigation (back buttons, menu button)

**Barkeeper Mode**:
- Header: "Barkeeper Mode 🍸" or similar indicator
- User menu with "Logout" option
- Visual distinction from visitor mode (different color theme)
- Professional icons and layout

**Admin Mode**:
- Header: "Admin Mode ⚙️" or similar indicator  
- User menu with "Logout" option
- Full feature navigation menu visible
- Visual distinction from other modes (admin color theme)

### Random Cocktail Feature

The random cocktail picker is available in both visitor and barkeeper modes with the same filtering logic:

**Filter Options**:
1. **Alcoholic Type Filter**:
   - All (default)
   - Alcoholic only (ABV > 0)
   - Non-alcoholic only (ABV = 0)

2. **Base Spirit Filter**:
   - Dynamically populated from available cocktails
   - Options include: Vodka, Gin, Rum, Whiskey, Tequila, Brandy, etc.
   - Empty option for "Any spirit"

**Selection Logic**:
```typescript
1. Load available cocktails (visitor) or all cocktails (barkeeper)
2. Extract unique base spirits from loaded cocktails
3. Apply filters:
   - If alcoholic filter = 'alcoholic': exclude cocktails with ABV = 0
   - If alcoholic filter = 'non-alcoholic': exclude cocktails with ABV > 0
   - If spirit filter set: only include cocktails with matching baseSpirit
4. From filtered set, select random index
5. Display selected cocktail with:
   - Name, image (if available), ABV, base spirit
   - "View Recipe" button linking to recipe detail
   - "Pick Again" button to select new random cocktail
6. Reset Filters button to clear all filters and re-pick
```

**Visitor vs Barkeeper Differences**:
- **Visitor**: Only sees cocktails that can be made (based on ingredient stock)
- **Barkeeper**: Sees all cocktails regardless of ingredient availability
- Filter UI is identical in both modes
- Results respect the mode's data scope



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

## Guidance for Future Contributors

This section provides architectural context and best practices for developers contributing to CocktailDB's access control system.

### Architecture Principles

**1. Clear Mode Separation**
- Each mode (visitor/barkeeper/admin) has its own component directory structure
- Routes use distinct prefixes (`/visitor/*`, `/barkeeper/*`, `/admin/*`)
- Shared components should be generic and mode-agnostic
- Mode-specific styling in separate CSS files (`visitor-shared.css`, `barkeeper-shared.css`, `admin-shared.css`)

**2. Defense in Depth**
- Frontend guards prevent unnecessary navigation
- Backend always validates permissions independently
- Never trust client-side role information
- Session validation on every protected API call

**3. API Design Patterns**
```
Public endpoints (no auth):
  GET /api/cocktails/available
  GET /api/cocktails/search
  GET /api/cocktails/{id} (read-only)
  POST /api/auth/login
  GET /api/auth/status

Barkeeper endpoints (barkeeper or admin):
  GET /api/cocktails (all cocktails)
  GET /api/ingredients
  PATCH /api/ingredients/{id}/stock

Admin endpoints (admin only):
  POST /api/cocktails
  PUT /api/cocktails/{id}
  DELETE /api/cocktails/{id}
  POST /api/ingredients
  PUT /api/ingredients/{id}
  DELETE /api/ingredients/{id}
```

### Adding New Features

**When adding a visitor feature:**
1. Create component in `/frontend/src/app/components/visitor/`
2. Add route to `app.routes.ts` with `/visitor/` prefix
3. Use `ApiService.getAvailableCocktails()` (respects stock filter)
4. No authentication required - omit route guard
5. Backend endpoint should be public (listed in `isPublicEndpoint()`)
6. Design for mobile-first, touch-friendly UI

**When adding a barkeeper feature:**
1. Create component in `/frontend/src/app/components/barkeeper/`
2. Add route with `canActivate: [barkeeperGuard]`
3. Use appropriate API methods with `{ withCredentials: true }`
4. Backend endpoint should check for BARKEEPER or ADMIN role
5. Design for quick access and minimal clicks
6. Include logout functionality in main menu

**When adding an admin feature:**
1. Create component in `/frontend/src/app/components/` (top level for admin)
2. Add route with `canActivate: [adminGuard]`
3. Use CRUD API methods with `{ withCredentials: true }`
4. Backend endpoint should check for ADMIN role only
5. Include comprehensive forms and validation
6. Add to admin navigation menu

### Security Best Practices for Contributors

**Frontend (Angular)**:
```typescript
// ✅ CORRECT: Use route guards for protected routes
{
  path: 'barkeeper/stock',
  component: StockManagementComponent,
  canActivate: [barkeeperGuard]
}

// ❌ WRONG: No guard on authenticated route
{
  path: 'barkeeper/stock',
  component: StockManagementComponent
}

// ✅ CORRECT: Always include withCredentials for protected APIs
this.http.get('/api/ingredients', { withCredentials: true })

// ❌ WRONG: Missing credentials on protected endpoint
this.http.get('/api/ingredients')

// ✅ CORRECT: Check auth status before showing features
this.authService.checkAuthStatus().subscribe(status => {
  this.isAdmin = status.role === 'ADMIN';
});

// ❌ WRONG: Trust localStorage or manual flags
this.isAdmin = localStorage.getItem('role') === 'admin';
```

**Backend (Kotlin/Spring)**:
```kotlin
// ✅ CORRECT: Verify auth in filter before processing
if (!isPublicEndpoint(path, method)) {
    val session = sessionService.validateSession(sessionId)
    if (session == null) {
        response.sendError(401, "Unauthorized")
        return
    }
}

// ❌ WRONG: Trust headers or client-provided role
val role = request.getHeader("X-User-Role") // Never do this!

// ✅ CORRECT: Public endpoint pattern
private fun isPublicEndpoint(path: String, method: String): Boolean {
    return path.startsWith("/api/auth/") ||
           path == "/api/cocktails/available" ||
           (path.startsWith("/api/cocktails/") && method == "GET")
}

// ❌ WRONG: Overly permissive pattern
private fun isPublicEndpoint(path: String): Boolean {
    return path.contains("cocktails") // Too broad!
}
```

### Testing Access Control

**Manual Testing Checklist**:
```bash
# 1. Test visitor access (no auth)
curl http://localhost:8080/api/cocktails/available
# Expected: 200 OK with available cocktails

# 2. Test protected endpoint without auth
curl http://localhost:8080/api/ingredients
# Expected: 401 Unauthorized

# 3. Login as barkeeper
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role":"barkeeper","password":"barkeeper"}' \
  -c cookies.txt
# Expected: 200 OK with session cookie

# 4. Access barkeeper endpoint
curl http://localhost:8080/api/ingredients -b cookies.txt
# Expected: 200 OK with ingredient list

# 5. Try admin endpoint as barkeeper
curl -X POST http://localhost:8080/api/ingredients \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","type":"SPIRIT","abv":40,"inStock":true}' \
  -b cookies.txt
# Expected: 403 Forbidden

# 6. Logout and verify session cleared
curl -X POST http://localhost:8080/api/auth/logout -b cookies.txt
curl http://localhost:8080/api/ingredients -b cookies.txt
# Expected: 401 Unauthorized
```

**Automated Test Patterns**:
```kotlin
// Example integration test
@Test
fun `barkeeper can view ingredients but not create them`() {
    val session = sessionService.createSession(UserRole.BARKEEPER)
    
    // Should allow GET
    mockMvc.perform(
        get("/api/ingredients")
            .cookie(Cookie("sessionId", session.sessionId))
    ).andExpect(status().isOk)
    
    // Should deny POST
    mockMvc.perform(
        post("/api/ingredients")
            .cookie(Cookie("sessionId", session.sessionId))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"name":"Vodka","type":"SPIRIT"}""")
    ).andExpect(status().isForbidden)
}
```

### Common Pitfalls to Avoid

1. **Don't bypass authentication in development**
   - Always test with real authentication flows
   - Don't comment out guards or filters for convenience
   - Use proper test credentials from `.env.example`

2. **Don't expose sensitive data in public endpoints**
   - Visitor endpoints should only return essential cocktail data
   - Don't include internal IDs or metadata not needed for display
   - Filter sensitive fields in API responses

3. **Don't implement client-side-only security**
   - Frontend guards are UX, not security
   - Backend must validate every request independently
   - Assume all client input is untrusted

4. **Don't hard-code credentials or roles**
   - Always use environment variables for configuration
   - Never commit passwords or secrets to version control
   - Use bcrypt hashes, never plain text

5. **Don't create overlapping route patterns**
   - Visitor routes: `/visitor/*`
   - Barkeeper routes: `/barkeeper/*`
   - Admin routes: Direct paths like `/cocktails`, `/ingredients`
   - Avoid ambiguous patterns that could match multiple guards

### Code Review Checklist for Access Control Changes

When reviewing PRs that touch authentication or access control:

- [ ] New routes have appropriate guards (`adminGuard`, `barkeeperGuard`, or none for public)
- [ ] Backend endpoints validate session and role
- [ ] Public endpoints are explicitly listed in `isPublicEndpoint()`
- [ ] API calls include `{ withCredentials: true }` when needed
- [ ] No hard-coded credentials or session tokens
- [ ] Error messages don't leak sensitive information
- [ ] Session timeout is respected
- [ ] CORS configuration allows only intended origins
- [ ] New features documented in appropriate mode section
- [ ] Manual testing performed for all three modes
- [ ] Browser navigation (back/forward) works correctly

### Debugging Access Issues

**Common issues and solutions**:

1. **"401 Unauthorized" on all requests after login**
   - Check: Session cookie being set correctly?
   - Check: `withCredentials: true` in API calls?
   - Check: CORS allowing credentials?
   - Check: Cookie domain/path matches request

2. **Route guard redirecting authenticated users**
   - Check: `authService.checkAuthStatus()` returning correct role?
   - Check: Guard logic comparing roles correctly?
   - Check: Session not expired?

3. **Frontend shows admin features for barkeeper**
   - Check: Using `authService.checkAuthStatus()` not localStorage
   - Check: Components subscribing to auth status changes
   - Check: Guards on all admin routes

4. **Backend allowing unauthorized access**
   - Check: Endpoint listed in `isPublicEndpoint()` incorrectly?
   - Check: Filter being applied to request?
   - Check: Session validation working?

## Conclusion

This security concept provides a **simple, lightweight authentication system** suitable for local network deployment on resource-constrained devices like Raspberry Pi. While not suitable for internet-facing production systems, it offers adequate security for a trusted home network environment while avoiding the complexity of full user management systems.

**Key Takeaways:**
- ✅ Three distinct modes optimized for different use cases
- ✅ Clear separation of concerns and access boundaries
- ✅ Secure password storage with bcrypt
- ✅ Simple session-based authentication
- ✅ Role-based access control at both frontend and backend
- ✅ No user registration complexity
- ✅ Visitor mode always accessible without friction
- ✅ Browser-friendly navigation with proper routing
- ⚠️ Best suited for local network deployments
- ⚠️ Password recovery requires manual intervention
- ⚠️ No advanced security features (2FA, rate limiting, audit logs)

For most home bartending use cases on a local network, this approach provides the right balance of security, usability, and simplicity.
