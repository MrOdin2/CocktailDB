# Authentication Implementation Guide

This guide provides practical instructions for implementing the security concept defined in `SECURITY_CONCEPT.md`.

## Quick Start

### 1. Set Up Environment Variables

Copy the example environment file and customize it:

```bash
cp .env.example .env
```

Edit `.env` and update the following values:

```bash
# Generate password hashes (see step 2)
ADMIN_PASSWORD_HASH=<your-generated-hash>
BARKEEPER_PASSWORD_HASH=<your-generated-hash>

# Generate session secret (see step 3)
SESSION_SECRET=<your-generated-secret>
```

### 2. Generate Password Hashes

To generate bcrypt password hashes, you can use the backend utility (to be implemented):

```bash
cd backend
./gradlew generatePasswordHash --password="your-secure-password"
```

Or use an online bcrypt generator (use work factor 12):
- https://bcrypt-generator.com/ (set rounds to 12)

**Default hashes in .env.example:**
- Admin password: "admin" → hash in file
- Barkeeper password: "barkeeper" → hash in file

**⚠️ IMPORTANT: Change these default passwords before deployment!**

### 3. Generate Session Secret

Generate a cryptographically secure random string for session signing:

```bash
# On Linux/Mac:
openssl rand -base64 64

# On Windows (PowerShell):
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

Copy the output to `SESSION_SECRET` in your `.env` file.

## API Endpoints

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "role": "admin",     // or "barkeeper"
  "password": "your-password"
}

Response (Success):
{
  "success": true,
  "role": "admin",
  "message": "Login successful"
}
Set-Cookie: sessionId=<uuid>; HttpOnly; Secure; SameSite=Strict; Path=/

Response (Failure):
{
  "success": false,
  "message": "Invalid credentials"
}
Status: 401 Unauthorized
```

#### Logout
```http
POST /api/auth/logout

Response:
{
  "success": true,
  "message": "Logged out successfully"
}
```

#### Check Authentication Status
```http
GET /api/auth/status

Response (Authenticated):
{
  "authenticated": true,
  "role": "admin"
}

Response (Not Authenticated):
{
  "authenticated": false,
  "role": null
}
```

### Protected Endpoints

All protected endpoints require a valid session cookie. Include the cookie automatically sent by the browser or manually in curl:

```bash
# Login and save cookies
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "admin"}' \
  -c cookies.txt

# Use saved cookies for authenticated requests
curl http://localhost:8080/api/ingredients \
  -b cookies.txt
```

## Frontend Integration

### AuthService (Angular)

```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface AuthStatus {
  authenticated: boolean;
  role: 'admin' | 'barkeeper' | null;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authStatus$ = new BehaviorSubject<AuthStatus>({
    authenticated: false,
    role: null
  });

  constructor(private http: HttpClient) {
    this.checkAuthStatus();
  }

  login(role: 'admin' | 'barkeeper', password: string): Observable<any> {
    return this.http.post('/api/auth/login', { role, password }).pipe(
      tap(() => this.checkAuthStatus())
    );
  }

  logout(): Observable<any> {
    return this.http.post('/api/auth/logout', {}).pipe(
      tap(() => {
        this.authStatus$.next({ authenticated: false, role: null });
      })
    );
  }

  checkAuthStatus(): void {
    this.http.get<AuthStatus>('/api/auth/status').subscribe({
      next: (status) => this.authStatus$.next(status),
      error: () => this.authStatus$.next({ authenticated: false, role: null })
    });
  }

  getAuthStatus(): Observable<AuthStatus> {
    return this.authStatus$.asObservable();
  }

  isAuthenticated(): boolean {
    return this.authStatus$.value.authenticated;
  }

  isAdmin(): boolean {
    return this.authStatus$.value.role === 'admin';
  }

  isBarkeeper(): boolean {
    return this.authStatus$.value.role === 'barkeeper';
  }
}
```

### Login Component

```typescript
// src/app/components/login/login.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  template: `
    <div class="login-container">
      <h2>CocktailDB Login</h2>
      <form (ngSubmit)="onLogin()">
        <div>
          <label>Role:</label>
          <select [(ngModel)]="role" name="role">
            <option value="admin">Admin</option>
            <option value="barkeeper">Barkeeper</option>
          </select>
        </div>
        <div>
          <label>Password:</label>
          <input type="password" [(ngModel)]="password" name="password" />
        </div>
        <button type="submit">Login</button>
        <div *ngIf="error" class="error">{{ error }}</div>
      </form>
    </div>
  `
})
export class LoginComponent {
  role: 'admin' | 'barkeeper' = 'admin';
  password = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin(): void {
    this.authService.login(this.role, this.password).subscribe({
      next: () => {
        this.router.navigate([this.role === 'admin' ? '/admin' : '/barkeeper']);
      },
      error: () => {
        this.error = 'Invalid credentials';
      }
    });
  }
}
```

### Route Guards

```typescript
// src/app/guards/admin.guard.ts
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

// src/app/guards/barkeeper.guard.ts
export const barkeeperGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin() || authService.isBarkeeper()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
```

### Route Configuration

```typescript
// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { adminGuard } from './guards/admin.guard';
import { barkeeperGuard } from './guards/barkeeper.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/visitor', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  
  // Visitor routes (no authentication required)
  { path: 'visitor', component: VisitorHomeComponent },
  { path: 'visitor/cocktails', component: VisitorCocktailsComponent },
  
  // Barkeeper routes (barkeeper or admin)
  { 
    path: 'barkeeper', 
    component: BarkeeperHomeComponent,
    canActivate: [barkeeperGuard]
  },
  
  // Admin routes (admin only)
  { 
    path: 'admin', 
    component: AdminHomeComponent,
    canActivate: [adminGuard]
  },
  { 
    path: 'admin/ingredients', 
    component: AdminIngredientsComponent,
    canActivate: [adminGuard]
  },
  { 
    path: 'admin/cocktails', 
    component: AdminCocktailsComponent,
    canActivate: [adminGuard]
  }
];
```

## Backend Implementation

### Security Configuration

```kotlin
// src/main/kotlin/com/cocktaildb/config/SecurityConfig.kt
@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Using SameSite cookies instead
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints (visitor mode)
                    .requestMatchers("/api/cocktails/available").permitAll()
                    .requestMatchers("/api/auth/login", "/api/auth/status").permitAll()
                    
                    // Barkeeper endpoints
                    .requestMatchers(HttpMethod.GET, "/api/cocktails/**").hasAnyRole("BARKEEPER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/ingredients/**").hasAnyRole("BARKEEPER", "ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/ingredients/*/stock").hasAnyRole("BARKEEPER", "ADMIN")
                    
                    // Admin-only endpoints
                    .requestMatchers(HttpMethod.POST, "/api/cocktails").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/cocktails/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/cocktails/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/ingredients").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/ingredients/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/ingredients/**").hasRole("ADMIN")
                    
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(sessionAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }
    
    @Bean
    fun sessionAuthenticationFilter() = SessionAuthenticationFilter()
}
```

### Session Model

```kotlin
// src/main/kotlin/com/cocktaildb/model/Session.kt
data class Session(
    val sessionId: String,
    val role: UserRole,
    val createdAt: Instant,
    var lastAccessAt: Instant
) {
    fun isExpired(timeoutMinutes: Long): Boolean {
        val timeout = Duration.ofMinutes(timeoutMinutes)
        return Instant.now().isAfter(lastAccessAt.plus(timeout))
    }
}

enum class UserRole {
    ADMIN,
    BARKEEPER
}
```

## Testing

### Manual Testing

1. **Test Visitor Access (No Auth Required)**
```bash
curl http://localhost:8080/api/cocktails/available
# Should return available cocktails without authentication
```

2. **Test Admin Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"role": "admin", "password": "admin"}' \
  -c cookies.txt -v
# Should set session cookie
```

3. **Test Protected Endpoint**
```bash
curl http://localhost:8080/api/ingredients \
  -b cookies.txt
# Should return ingredients (authenticated)

curl http://localhost:8080/api/ingredients
# Should return 401 Unauthorized (no cookie)
```

4. **Test Logout**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
# Should clear session
```

### Integration Tests

```kotlin
// src/test/kotlin/com/cocktaildb/AuthenticationTests.kt
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTests {
    
    @Autowired
    lateinit var mockMvc: MockMvc
    
    @Test
    fun `visitor can access available cocktails without authentication`() {
        mockMvc.perform(get("/api/cocktails/available"))
            .andExpect(status().isOk)
    }
    
    @Test
    fun `admin can login with valid credentials`() {
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"role":"admin","password":"admin"}""")
        )
            .andExpect(status().isOk)
            .andExpect(cookie().exists("sessionId"))
    }
    
    @Test
    fun `protected endpoint returns 401 without authentication`() {
        mockMvc.perform(get("/api/ingredients"))
            .andExpect(status().isUnauthorized)
    }
}
```

## Troubleshooting

### Common Issues

**Problem: Login fails with valid password**
- Check that password hash in `.env` matches the password
- Verify bcrypt work factor is 12
- Check backend logs for authentication errors

**Problem: Session expires immediately**
- Check `SESSION_TIMEOUT_MINUTES` is set correctly
- Verify system clock is accurate
- Check if sessions are being cleaned up too aggressively

**Problem: 401 on all requests**
- Verify session cookie is being sent (check browser dev tools)
- Check cookie domain/path settings
- Ensure CORS is configured correctly

**Problem: Cannot access any endpoints after login**
- Check role permissions in SecurityConfig
- Verify session is stored correctly
- Check for typos in role names (ADMIN vs admin)

## Security Checklist

Before deploying to production:

- [ ] Changed default admin password
- [ ] Changed default barkeeper password  
- [ ] Generated strong session secret
- [ ] Configured HTTPS (if not on trusted local network)
- [ ] Reviewed CORS settings
- [ ] Tested all authentication flows
- [ ] Verified session expiration works
- [ ] Checked that visitor endpoints are accessible without auth
- [ ] Confirmed admin endpoints require admin role
- [ ] Tested logout functionality
- [ ] Backed up `.env` file securely

## Additional Resources

- [SECURITY_CONCEPT.md](../SECURITY_CONCEPT.md) - Detailed security architecture
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [bcrypt.js Documentation](https://github.com/kelektiv/node.bcrypt.js)
- [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)
