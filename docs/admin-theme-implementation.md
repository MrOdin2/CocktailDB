# Admin-Dictated Styling Implementation

## Overview
This implementation moves the application theme setting from browser-based localStorage to a centralized backend database. This allows administrators to control the visual appearance of the application for all users, creating a unified and immersive experience.

## Changes Implemented

### Backend (Kotlin/Spring Boot)

#### 1. Database Model
- **AppSettings** (`model/AppSettings.kt`): JPA entity for storing key-value configuration pairs
  - Fields: `id`, `settingKey` (unique), `settingValue`
  - Used for persistent storage of application-wide settings

#### 2. Data Access
- **AppSettingsRepository** (`repository/AppSettingsRepository.kt`): JPA repository
  - Method: `findBySettingKey(settingKey: String): Optional<AppSettings>`

#### 3. Business Logic
- **AppSettingsService** (`service/AppSettingsService.kt`): Service layer for settings management
  - `getTheme()`: Returns current theme (default: "basic")
  - `setTheme(theme: String)`: Saves theme to database
  - `getSetting(key, default)`: Generic setting getter
  - `setSetting(key, value)`: Generic setting setter

#### 4. REST API
- **SettingsController** (`controller/SettingsController.kt`): REST endpoints
  - `GET /api/settings/theme`: Returns current theme as JSON
  - `PUT /api/settings/theme`: Updates theme (validates input)
  - Valid themes: basic, terminal-green, cyberpunk, amber
  - Returns 400 Bad Request for invalid themes

#### 5. Initialization
- **DataInitializer** (`config/DataInitializer.kt`): Updated to set default theme on first run
  - Checks if theme exists in database
  - Sets "basic" as default if no theme is configured

### Frontend (Angular/TypeScript)

#### 1. Service Layer
- **ThemeService** (`services/theme.service.ts`): Updated to use backend API
  - Removed localStorage dependency
  - `loadThemeFromBackend()`: Fetches theme on initialization
  - `setTheme(theme)`: Returns Observable, persists to backend
  - `reloadTheme()`: Refreshes theme from backend
  - Handles errors gracefully with fallback to default theme

#### 2. UI Components
- **SettingsComponent** (`components/settings/settings.component.ts`): Updated to handle Observable
  - Error handling uses console logging instead of browser alerts
  - Subscribes to theme changes from service

- **Settings Template** (`components/settings/settings.component.html`): Updated messaging
  - Informs users that theme is stored server-side
  - Notes that changes apply to all users

## Architecture Decisions

### Why Backend Storage?
1. **Unified Experience**: All users see the same theme
2. **Admin Control**: Foundation for admin-only theme changes (when auth is implemented)
3. **Cross-Device Consistency**: Theme persists across browsers and devices
4. **Immersive Bar Experience**: Allows bar owners to set ambiance-appropriate themes
5. **Corporate Design**: Supports branding and design consistency

### Security Considerations
- Theme endpoint is currently public (no authentication)
- TODO: Restrict PUT /api/settings/theme to admin users when authentication is implemented
- Input validation prevents injection of invalid theme values
- CodeQL security scan: ✅ No vulnerabilities detected

### Data Flow
```
User clicks theme in Settings UI
  → Frontend sends PUT /api/settings/theme
    → Backend validates theme value
      → Backend saves to database
        → Backend returns success response
          → Frontend receives response
            → ThemeService updates current theme
              → ThemeService applies CSS stylesheet
                → User sees new theme
```

## Testing Results

### Backend Tests
✅ Build successful
✅ Theme GET endpoint returns correct values
✅ Theme PUT endpoint saves to database
✅ Theme persists across application restarts
✅ Invalid themes rejected with 400 Bad Request
✅ All valid themes (basic, terminal-green, cyberpunk, amber) work

### Frontend Tests
✅ Build successful
✅ ThemeService fetches theme on initialization
✅ Theme changes trigger Observable updates
✅ Error handling graceful (no user-facing alerts)
✅ UI updates correctly on theme change

### Integration Tests
✅ End-to-end theme change flow works
✅ Multiple browser windows share same theme
✅ Theme survives backend restart
✅ Invalid themes handled gracefully

## API Documentation

### GET /api/settings/theme
Returns the current application theme.

**Response:**
```json
{
  "theme": "basic"
}
```

**Status Codes:**
- 200 OK: Success

### PUT /api/settings/theme
Updates the application theme (admin-only when auth is implemented).

**Request Body:**
```json
{
  "theme": "cyberpunk"
}
```

**Valid Values:**
- "basic" - Professional default theme
- "terminal-green" - Classic 80s green terminal
- "cyberpunk" - Neon cyberpunk aesthetic  
- "amber" - Vintage amber CRT monitor

**Success Response:**
```json
{
  "theme": "cyberpunk"
}
```

**Error Response (Invalid Theme):**
```json
{
  "error": "Invalid theme. Must be one of: basic, terminal-green, cyberpunk, amber"
}
```

**Status Codes:**
- 200 OK: Theme updated successfully
- 400 Bad Request: Invalid theme value

## Future Enhancements

### Short Term
1. Implement authentication/authorization system (see SECURITY_CONCEPT.md)
2. Add `@PreAuthorize("hasRole('ADMIN')")` to PUT endpoint
3. Add audit logging for theme changes
4. Create admin UI section for system settings

### Long Term
1. Add more customizable settings (app name, welcome message, etc.)
2. Support custom theme creation/upload
3. Preview mode for testing themes
4. Schedule theme changes (e.g., happy hour theme)
5. Per-location themes for multi-location deployments

## Migration Notes

### For Users
- Theme selection is now server-side
- Changes apply to all users immediately
- No more per-browser theme preferences
- Previous localStorage settings are ignored

### For Administrators
- Default theme is "basic"
- Change theme via Settings page (will be admin-only when auth is implemented)
- Theme persists in database
- Database backup includes theme setting

### For Developers
- Theme setting stored in `app_settings` table
- Key: "theme", Value: theme name
- Add new themes by updating:
  1. Backend: `SettingsController.VALID_THEMES`
  2. Frontend: `ThemeService.Theme` type
  3. CSS: Create new `styles-themeN-name.css` file

## Conclusion

This implementation successfully moves theme management from client-side to server-side, providing a foundation for admin-controlled application appearance. The system is secure, validated, and ready for integration with the planned authentication system described in SECURITY_CONCEPT.md.
