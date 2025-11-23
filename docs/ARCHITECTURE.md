# CocktailDB Architecture Guide

This document provides an architectural overview of CocktailDB for developers and contributors.

## Quick Navigation

**For End Users**:
- [README.md](../README.md) - Getting started and feature overview
- [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Security design and user flows
- [authentication-guide.md](authentication-guide.md) - Setup and usage instructions
- [security-quick-reference.md](security-quick-reference.md) - Quick reference guide

**For Developers**:
- This document - Architecture and development patterns
- [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Contributor guidance section
- [.github/copilot-instructions.md](../.github/copilot-instructions.md) - Coding conventions

## System Overview

CocktailDB is a full-stack web application designed for managing cocktail recipes and ingredients with a three-tier access control system optimized for different user needs.

### Technology Stack

**Backend**:
- Spring Boot 3.1.5 (Kotlin)
- Spring Data JPA
- PostgreSQL 15 (production) / H2 (development)
- Session-based authentication with bcrypt

**Frontend**:
- Angular 20 (Standalone components)
- TypeScript with strict mode
- RxJS for reactive programming
- Mobile-first responsive design

**Deployment**:
- Docker & Docker Compose
- nginx for frontend serving
- Environment-based configuration

## Application Architecture

### Three-Tier Access Model

The application implements three distinct user interfaces, each optimized for specific use cases:

```
┌─────────────────────────────────────────────────────────┐
│                    CocktailDB System                     │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────┐  ┌────────────────┐  ┌───────────┐ │
│  │ Visitor Mode   │  │ Barkeeper Mode │  │ Admin Mode│ │
│  │ (Public)       │  │ (Authenticated)│  │(Full Auth)│ │
│  └────────────────┘  └────────────────┘  └───────────┘ │
│         │                    │                  │       │
│         └────────────────────┴──────────────────┘       │
│                           │                             │
│                  ┌────────▼────────┐                    │
│                  │   API Gateway    │                   │
│                  │ (SessionFilter)  │                   │
│                  └────────┬────────┘                    │
│                           │                             │
│         ┌─────────────────┼─────────────────┐          │
│         │                 │                 │          │
│    ┌────▼─────┐   ┌──────▼──────┐   ┌─────▼──────┐   │
│    │Cocktail  │   │ Ingredient  │   │   Auth     │   │
│    │Service   │   │  Service    │   │  Service   │   │
│    └────┬─────┘   └──────┬──────┘   └─────┬──────┘   │
│         │                │                 │          │
│    ┌────▼─────┐   ┌──────▼──────┐   ┌─────▼──────┐   │
│    │Cocktail  │   │ Ingredient  │   │  Session   │   │
│    │Repository│   │ Repository  │   │   Store    │   │
│    └────┬─────┘   └──────┬──────┘   └────────────┘   │
│         │                │                            │
│         └────────────────┴──────────────┐             │
│                                         │             │
│                                  ┌──────▼──────┐      │
│                                  │  PostgreSQL │      │
│                                  │  Database   │      │
│                                  └─────────────┘      │
│                                                       │
└───────────────────────────────────────────────────────┘
```

### Component Organization

#### Frontend Structure

```
frontend/src/app/
├── components/
│   ├── visitor/              # Visitor mode components
│   │   ├── menu/            # Landing menu
│   │   ├── cocktail-list/   # Available cocktails
│   │   ├── recipe/          # Recipe detail view
│   │   ├── random-picker/   # Random cocktail selector
│   │   └── categories/      # Category browser
│   ├── barkeeper/           # Barkeeper mode components
│   │   ├── menu/            # Barkeeper main menu
│   │   ├── alphabet/        # Alphabetical index
│   │   ├── cocktail-list/   # Full cocktail list
│   │   ├── recipe/          # Recipe detail
│   │   ├── random-picker/   # Random selector
│   │   └── stock-management/ # Ingredient stock control
│   ├── cocktails/           # Admin cocktail management
│   ├── ingredients/         # Admin ingredient management
│   ├── visualization/       # Admin data visualizations
│   ├── settings/            # Admin settings
│   └── login/               # Authentication component
├── guards/
│   ├── admin.guard.ts       # Admin route protection
│   └── barkeeper.guard.ts   # Barkeeper route protection
├── services/
│   ├── api.service.ts       # Backend API client
│   └── auth.service.ts      # Authentication service
└── models/
    └── models.ts            # TypeScript interfaces
```

#### Backend Structure

```
backend/src/main/kotlin/com/cocktaildb/
├── controller/
│   ├── CocktailController.kt   # Cocktail endpoints
│   ├── IngredientController.kt # Ingredient endpoints
│   └── AuthController.kt       # Authentication endpoints
├── service/
│   ├── CocktailService.kt      # Cocktail business logic
│   ├── IngredientService.kt    # Ingredient business logic
│   ├── SessionService.kt       # Session management
│   └── PasswordService.kt      # Password hashing
├── repository/
│   ├── CocktailRepository.kt   # Cocktail data access
│   └── IngredientRepository.kt # Ingredient data access
├── model/
│   ├── Cocktail.kt            # Cocktail entity
│   ├── Ingredient.kt          # Ingredient entity
│   └── CocktailIngredient.kt  # Junction entity
├── security/
│   ├── SessionAuthenticationFilter.kt  # Request filter
│   ├── Session.kt                      # Session model
│   └── UserRole.kt                     # Role enum
└── config/
    ├── CorsConfig.kt          # CORS configuration
    └── WebConfig.kt           # Web configuration
```

## Authentication & Authorization Flow

### Session-Based Authentication

```
┌──────────────┐
│   Browser    │
└──────┬───────┘
       │
       │ 1. POST /api/auth/login
       │    {role: "admin", password: "***"}
       │
       ▼
┌──────────────────────┐
│  AuthController      │
│  - Validate password │ ─────► 2. PasswordService.verify()
│  - Create session    │ ◄───── 3. SessionService.createSession()
│  - Set cookie        │
└──────┬───────────────┘
       │
       │ 4. Set-Cookie: sessionId=<uuid>
       │    HttpOnly; Secure; SameSite=Strict
       │
       ▼
┌──────────────┐
│   Browser    │ (stores cookie)
└──────┬───────┘
       │
       │ 5. GET /api/cocktails
       │    Cookie: sessionId=<uuid>
       │
       ▼
┌──────────────────────────┐
│ SessionAuthFilter        │
│ - Extract sessionId      │ ────► 6. SessionService.validateSession()
│ - Validate session       │ ◄──── 7. Session{role, createdAt, ...}
│ - Check permissions      │
└──────┬───────────────────┘
       │
       │ 8. Allow request if authorized
       │
       ▼
┌──────────────────────┐
│  CocktailController  │
│  - Process request   │
│  - Return data       │
└──────────────────────┘
```

### Role-Based Access Control

Access control is enforced at two levels:

**1. Frontend (UX)**:
- Route guards prevent navigation to unauthorized routes
- UI elements conditionally rendered based on role
- Improves user experience, NOT security

**2. Backend (Security)**:
- SessionAuthenticationFilter validates all requests
- Role verification on every protected endpoint
- Independent of frontend checks

### Permission Model

```
Visitor (No Auth)
    │
    ├─ GET /api/cocktails/available ✅
    ├─ GET /api/cocktails/{id} ✅ (limited info)
    ├─ GET /api/cocktails/search ✅
    └─ All other endpoints ❌

Barkeeper (Authenticated)
    │
    ├─ All Visitor permissions ✅
    ├─ GET /api/cocktails ✅ (all cocktails)
    ├─ GET /api/ingredients ✅
    ├─ PATCH /api/ingredients/{id}/stock ✅
    └─ POST/PUT/DELETE endpoints ❌

Admin (Full Access)
    │
    ├─ All Barkeeper permissions ✅
    ├─ POST /api/cocktails ✅
    ├─ PUT /api/cocktails/{id} ✅
    ├─ DELETE /api/cocktails/{id} ✅
    ├─ POST /api/ingredients ✅
    ├─ PUT /api/ingredients/{id} ✅
    └─ DELETE /api/ingredients/{id} ✅
```

## Data Model

### Core Entities

```
┌─────────────────┐
│   Ingredient    │
├─────────────────┤
│ id: Long        │
│ name: String    │
│ type: Enum      │ ◄──┐
│ abv: Int        │    │
│ inStock: Boolean│    │
└─────────────────┘    │
                       │
                       │ Many-to-Many
                       │
┌─────────────────┐    │    ┌──────────────────────┐
│   Cocktail      │────┼────│ CocktailIngredient   │
├─────────────────┤    │    ├──────────────────────┤
│ id: Long        │    │    │ cocktailId: Long     │
│ name: String    │    └────│ ingredientId: Long   │
│ steps: List     │         │ measure: String      │
│ notes: String?  │         │ order: Int           │
│ abv: Int        │         └──────────────────────┘
│ baseSpirit: Str │
│ tags: List      │
└─────────────────┘
```

### Ingredient Types

```kotlin
enum class IngredientType {
    SPIRIT,      // Vodka, Gin, Rum, Whiskey, etc.
    LIQUEUR,     // Triple Sec, Amaretto, etc.
    WINE,        // Vermouth, Wine
    BEER,        // Beer types
    JUICE,       // Orange, Lime, Cranberry
    SODA,        // Club Soda, Tonic Water
    SYRUP,       // Simple Syrup, Grenadine
    BITTERS,     // Angostura, Peychaud's
    GARNISH,     // Lemon, Lime, Mint
    OTHER        // Anything else
}
```

## API Design

### RESTful Endpoints

**Authentication**:
```
POST   /api/auth/login       - Authenticate user
POST   /api/auth/logout      - End session
GET    /api/auth/status      - Check auth status
```

**Cocktails**:
```
GET    /api/cocktails         - List all (auth required)
GET    /api/cocktails/{id}    - Get single cocktail
POST   /api/cocktails         - Create (admin only)
PUT    /api/cocktails/{id}    - Update (admin only)
DELETE /api/cocktails/{id}    - Delete (admin only)
GET    /api/cocktails/available - Available cocktails (public)
GET    /api/cocktails/search  - Search cocktails (public)
```

**Ingredients**:
```
GET    /api/ingredients       - List all (auth required)
GET    /api/ingredients/{id}  - Get single ingredient (auth required)
POST   /api/ingredients       - Create (admin only)
PUT    /api/ingredients/{id}  - Update (admin only)
DELETE /api/ingredients/{id}  - Delete (admin only)
PATCH  /api/ingredients/{id}/stock - Toggle stock (barkeeper+)
```

### API Response Patterns

**Success Responses**:
```json
// Single entity
{
  "id": 1,
  "name": "Mojito",
  "ingredients": [...],
  "steps": [...],
  "notes": "..."
}

// Collection
[
  { "id": 1, "name": "Mojito", ... },
  { "id": 2, "name": "Margarita", ... }
]
```

**Error Responses**:
```json
// Validation error
{
  "error": "Validation failed",
  "details": ["Name is required", "ABV must be 0-100"]
}

// Authentication error
{
  "error": "Unauthorized",
  "message": "Invalid credentials"
}

// Authorization error
{
  "error": "Forbidden",
  "message": "Insufficient permissions"
}
```

## Frontend Patterns

### Reactive State Management

```typescript
// AuthService - Observable pattern
export class AuthService {
  private authStatus$ = new BehaviorSubject<AuthStatus>({
    authenticated: false,
    role: null
  });

  getAuthStatus(): Observable<AuthStatus> {
    return this.authStatus$.asObservable();
  }
}

// Component - Subscribe to changes
export class SomeComponent implements OnInit {
  isAdmin$ = this.authService.getAuthStatus().pipe(
    map(status => status.role === 'ADMIN')
  );
}
```

### Route Guards

```typescript
// Functional guard (Angular 15+)
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus().pipe(
    take(1),
    map(status => {
      if (status.authenticated && status.role === 'ADMIN') {
        return true;
      }
      router.navigate(['/login']);
      return false;
    })
  );
};
```

### API Service Pattern

```typescript
// Centralized API client
@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Public endpoint (no credentials)
  getAvailableCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(
      `${this.baseUrl}/cocktails/available`
    );
  }

  // Protected endpoint (with credentials)
  getAllCocktails(): Observable<Cocktail[]> {
    return this.http.get<Cocktail[]>(
      `${this.baseUrl}/cocktails`,
      { withCredentials: true }
    );
  }
}
```

## Deployment Architecture

### Docker Compose Setup

```
┌─────────────────────────────────────────────┐
│              Host System                     │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │         Docker Network                  │ │
│  │                                         │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────┐ │ │
│  │  │ Frontend │  │ Backend  │  │ Post-│ │ │
│  │  │  nginx   │  │  Spring  │  │greSQL│ │ │
│  │  │  :80     │  │  :8080   │  │ :5432│ │ │
│  │  └────┬─────┘  └─────┬────┘  └───┬──┘ │ │
│  │       │              │            │    │ │
│  │       │              └────────────┘    │ │
│  │       │              (API calls)       │ │
│  │       │                                │ │
│  └───────┼────────────────────────────────┘ │
│          │                                   │
│          │ Port 80 exposed                   │
└──────────┼───────────────────────────────────┘
           │
    ┌──────▼──────┐
    │   Browser   │
    │  (Client)   │
    └─────────────┘
```

### Environment Configuration

**Production (.env)**:
```bash
# Database
POSTGRES_DB=cocktaildb
POSTGRES_USER=cocktaildb
POSTGRES_PASSWORD=<secure-password>

# Authentication
ADMIN_PASSWORD_HASH=<bcrypt-hash>
BARKEEPER_PASSWORD_HASH=<bcrypt-hash>
SESSION_SECRET=<random-64-char-string>
SESSION_TIMEOUT_MINUTES=60

# CORS
CORS_ALLOWED_ORIGINS=http://localhost,http://192.168.1.100
```

**Development**:
- Backend: `./gradlew bootRun` (H2 in-memory)
- Frontend: `npm start` (webpack dev server)
- No Docker required for local development

## Security Considerations

### Session Security

**Session Properties**:
- Stored in-memory (ConcurrentHashMap)
- Automatically cleaned up after timeout
- 60-minute idle timeout (configurable)
- HTTP-only cookies prevent XSS attacks
- SameSite=Strict prevents CSRF attacks

**Password Security**:
- bcrypt hashing with work factor 12
- Passwords never stored in plain text
- Hashes stored in environment variables
- No password recovery UI (manual reset required)

### Known Limitations

1. **In-Memory Sessions**: Lost on server restart
2. **Single Admin**: Only one admin account supported
3. **No Rate Limiting**: Vulnerable to brute force (mitigated by local network)
4. **No Audit Logging**: No tracking of who did what
5. **No 2FA**: Single-factor authentication only

### Deployment Recommendations

**For Local Network (Raspberry Pi)**:
- ✅ Current implementation is sufficient
- ✅ HTTPS optional (trusted network)
- ⚠️ Change default passwords!
- ⚠️ Ensure firewall blocks external access

**For Internet-Facing Deployment**:
- ❌ Not recommended with current security
- 🔧 Would need: HTTPS, rate limiting, audit logs, 2FA, database-backed sessions

## Development Workflow

### Adding a New Feature

**1. Backend Development**:
```bash
# Add entity/model
# Add repository interface
# Add service logic
# Add controller endpoint
# Update SessionAuthenticationFilter if needed
```

**2. Frontend Development**:
```bash
# Create component (in appropriate mode folder)
# Add route with proper guard
# Update ApiService with new endpoint
# Add TypeScript interfaces if needed
```

**3. Testing**:
```bash
# Backend
./gradlew test

# Frontend
npm test

# Integration
# Manual testing with browser
```

### Code Style

**Backend (Kotlin)**:
- Use data classes for DTOs
- Constructor injection for dependencies
- Explicit return types
- Null safety with `?` and `!!`

**Frontend (TypeScript)**:
- Standalone components
- Reactive Forms
- Observables over Promises
- Type everything (strict mode)

## Monitoring & Troubleshooting

### Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database connection
docker exec -it cocktaildb-postgres psql -U cocktaildb -d cocktaildb

# Check sessions (dev mode)
# Sessions are in-memory, no direct inspection without debug endpoint
```

### Common Issues

**Session Issues**:
- Check cookie domain/path matches
- Verify `withCredentials: true` in API calls
- Check CORS configuration

**Authorization Failures**:
- Verify session is valid (not expired)
- Check role matches endpoint requirements
- Review SessionAuthenticationFilter logs

**CORS Errors**:
- Check `CORS_ALLOWED_ORIGINS` includes frontend URL
- Verify `Access-Control-Allow-Credentials: true`
- Check browser console for specific CORS error

## Performance Considerations

### Frontend Optimization

- **Lazy Loading**: Could implement route-based code splitting
- **OnPush Change Detection**: Use for list components
- **RxJS Optimization**: Unsubscribe in ngOnDestroy
- **Image Optimization**: Compress cocktail images

### Backend Optimization

- **Query Optimization**: Add database indexes on frequently queried fields
- **Caching**: Consider Redis for session storage
- **Connection Pooling**: Already configured in Spring Boot
- **N+1 Prevention**: Use JOIN FETCH for cocktail ingredients

### Database Design

**Current Indexes**:
- Primary keys auto-indexed
- Foreign keys should be indexed
- Consider adding:
  - `ingredient.in_stock` for availability queries
  - `cocktail.base_spirit` for filtering
  - `cocktail.name` for search

## Future Architecture Improvements

1. **Database-Backed Sessions**: Move from in-memory to PostgreSQL or Redis
2. **Event Sourcing**: Track all changes for audit and undo
3. **GraphQL API**: Alternative to REST for complex queries
4. **Real-Time Updates**: WebSocket for multi-user stock updates
5. **Image Storage**: S3 or similar for cocktail images
6. **Search Engine**: Elasticsearch for advanced cocktail search
7. **Mobile Apps**: React Native or Flutter apps using same API
8. **Import/Export**: JSON/CSV import for bulk recipe additions

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Detailed security design
- [README.md](../README.md) - User-facing documentation
