# CocktailDB - Copilot Instructions

## Project Overview
CocktailDB is a full-stack web application for managing cocktail recipes and ingredients. It helps users track their ingredient inventory and discover which cocktails they can make with available ingredients.

## Architecture
- **Backend**: Spring Boot 3.1.5 with Kotlin, providing RESTful APIs
- **Frontend**: Angular 20 with TypeScript using standalone components
- **Database**: PostgreSQL (production), H2 (development/testing)
- **Deployment**: Docker & Docker Compose

## Project Structure
```
CocktailDB/
├── backend/                    # Spring Boot backend
│   ├── src/main/kotlin/com/cocktaildb/
│   │   ├── controller/        # REST API controllers
│   │   ├── model/             # JPA entities
│   │   ├── repository/        # Data repositories
│   │   ├── service/           # Business logic
│   │   └── config/            # Configuration classes
│   └── build.gradle.kts       # Gradle build configuration
├── frontend/                   # Angular frontend
│   ├── src/app/
│   │   ├── components/        # UI components
│   │   ├── models/            # TypeScript interfaces
│   │   └── services/          # API services
│   └── package.json           # npm dependencies
└── docker-compose.yml         # Container orchestration
```

## Technology Stack

### Backend
- **Language**: Kotlin
- **Framework**: Spring Boot 3.1.5
- **Java Version**: 17
- **Build Tool**: Gradle with Kotlin DSL
- **ORM**: Spring Data JPA
- **Databases**: 
  - H2 (in-memory for dev profile)
  - PostgreSQL 15 (for dev-postgres and prod profiles)

### Frontend
- **Language**: TypeScript
- **Framework**: Angular 20
- **Component Style**: Standalone components (no NgModules)
- **Forms**: Reactive Forms
- **HTTP Client**: Angular HttpClient
- **Build Tool**: Angular CLI

## Coding Conventions

### Backend (Kotlin)
- Use Kotlin idioms and features (data classes, null safety, extension functions)
- Follow Spring Boot best practices
- Use dependency injection via constructor injection
- REST controllers should use appropriate HTTP methods and status codes
- Service layer handles business logic
- Repository layer handles data persistence
- Use `@Entity` for JPA models, `@Service` for services, `@RestController` for controllers

### Frontend (TypeScript/Angular)
- Use standalone components (no NgModules)
- Follow Angular style guide
- Use TypeScript strict mode
- Prefer reactive programming with RxJS
- Use services for API calls and shared state
- Components should be focused and reusable
- Use Angular's reactive forms for form handling

### Component Design Principles
- **Single Responsibility**: Each component should have one clear purpose
- **Small and Focused**: Components should be small (< 300 lines of TypeScript)
- **Modularity**: Break down large components into smaller subcomponents
- **Reusability**: Design components to be reusable across different contexts
- **Router Integration**: Use Angular Router for navigation to enable browser back/forward buttons
- **Accessibility**: Always include proper ARIA labels, semantic HTML, and keyboard navigation
  - Use `<button>` for actions, `<a>` for navigation
  - Include `aria-label` for icon-only buttons
  - Associate form labels with inputs using `for` and `id` attributes
  - Ensure sufficient color contrast (WCAG AA: 4.5:1 for text)
  - Make interactive elements keyboard accessible
- **Mobile-First**: Design for mobile screens first, then enhance for larger screens
- **Performance**: Use OnPush change detection when possible, avoid unnecessary subscriptions
- **Navigation**: Prefer router-based navigation over internal view switching for better UX

### Responsive Design Requirements
The application must be fully responsive and optimized for various device sizes:

**Target Devices:**
- **Smartphones**: 1792x828 to 2556x1179 pixels (portrait and landscape)
- **Tablets**: 2388x1668 pixels (portrait and landscape)
- **Desktop**: 1024px and above

**Breakpoints:**
- Mobile (small): 320px - 767px
- Tablet (medium): 768px - 1023px
- Desktop (large): 1024px and above

**Responsive Design Principles:**
- Use CSS Grid and Flexbox for flexible layouts
- Implement mobile-first approach with `min-width` media queries
- Convert tables to card layouts on mobile devices (< 768px)
- Ensure touch targets are at least 44x44 pixels for mobile
- Use responsive typography that scales appropriately
- Make forms stack vertically on mobile, multi-column on tablet/desktop
- Optimize navigation for mobile (hamburger menu, simplified nav)
- Ensure images and charts scale properly
- Test at all breakpoints to ensure usability
- Use `viewport` meta tag for proper mobile rendering
- Avoid horizontal scrolling on any device size
- Make buttons and interactive elements easily tappable on touch devices

### Component Organization
When creating feature areas (like barkeeper mode):
- Create a feature directory (e.g., `components/barkeeper/`)
- Split functionality into focused subcomponents (e.g., `menu/`, `recipe/`, `stock-management/`)
- Each subcomponent gets its own route for browser navigation support
- Share state through services or route parameters, not parent components

## Database Configuration

### Profiles
- **dev** (default): Uses H2 in-memory database
- **dev-postgres**: Uses PostgreSQL on localhost (requires Docker container)
- **prod**: Uses PostgreSQL in Docker (automatically used in Docker Compose)

### Connection Details
- Dev PostgreSQL: `localhost:5432/cocktaildb` (user: cocktaildb, password: cocktaildb)
- Prod PostgreSQL: Configured via environment variables in docker-compose.yml

## Build & Run

### Backend
```bash
cd backend
# Run with H2 (default)
./gradlew bootRun

# Run with PostgreSQL
./gradlew bootRun --args='--spring.profiles.active=dev-postgres'

# Build
./gradlew build

# Run tests
./gradlew test
```

### Frontend
```bash
cd frontend
# Install dependencies
npm install

# Development server
npm start

# Build for production
npm run build

# Run tests
npm test
```

### Docker
```bash
# Build everything
./build.sh

# Start all services
docker compose up -d

# Stop services
docker compose down

# Remove all data
docker compose down -v
```

## API Structure

### REST Endpoints
All API endpoints are under `/api` prefix:

**Ingredients**
- `GET /api/ingredients` - List all ingredients
- `GET /api/ingredients/{id}` - Get ingredient by ID
- `GET /api/ingredients/in-stock` - List in-stock ingredients
- `POST /api/ingredients` - Create ingredient
- `PUT /api/ingredients/{id}` - Update ingredient
- `DELETE /api/ingredients/{id}` - Delete ingredient

**Cocktails**
- `GET /api/cocktails` - List all cocktails
- `GET /api/cocktails/{id}` - Get cocktail by ID
- `GET /api/cocktails/available` - List makeable cocktails
- `POST /api/cocktails` - Create cocktail
- `PUT /api/cocktails/{id}` - Update cocktail
- `DELETE /api/cocktails/{id}` - Delete cocktail

### Data Models

**Ingredient**
```kotlin
data class Ingredient(
    val id: Long?,
    val name: String,
    val type: IngredientType,
    val abv: Int,
    val inStock: Boolean
)
```

**IngredientType** enum: SPIRIT, LIQUEUR, WINE, BEER, JUICE, SODA, SYRUP, BITTERS, GARNISH, OTHER

**Cocktail**
```kotlin
data class Cocktail(
    val id: Long?,
    val name: String,
    val ingredients: List<CocktailIngredient>,
    val steps: List<String>,
    val notes: String?
)
```

**CocktailIngredient**
```kotlin
data class CocktailIngredient(
    val ingredientId: Long,
    val measure: String
)
```

## Development Workflow

### Making Changes
1. Backend changes: Modify Kotlin files in `backend/src/main/kotlin/com/cocktaildb/`
2. Frontend changes: Modify TypeScript files in `frontend/src/app/`
3. Test locally before committing
4. Ensure both backend and frontend build successfully

### Adding New Features
- **New API endpoint**: Add to appropriate controller, implement in service layer
- **New model/entity**: Create in `model` package with JPA annotations
- **New frontend component**: Create standalone component with inline template or separate HTML
- **New API call**: Add method to `api.service.ts`

### Database Changes
- Models use JPA annotations
- Schema is auto-generated by Hibernate (ddl-auto setting varies by profile)
- For production, consider using Flyway or Liquibase for migrations

## Testing
Currently, the project does not have extensive test coverage. When adding tests:
- Backend: Use JUnit 5 and Spring Boot Test
- Frontend: Use Jasmine and Karma (Angular's default testing framework)

## Common Patterns

### Backend
- Controllers return `ResponseEntity<T>` for proper HTTP responses
- Services throw exceptions that are handled by Spring's exception handling
- Use `@CrossOrigin` for CORS (configured in controllers)
- Repositories extend `JpaRepository` or `CrudRepository`

### Frontend
- Services use `HttpClient` and return Observables
- Components subscribe to Observables and handle async data
- Use Angular's built-in directives (`*ngIf`, `*ngFor`, etc.)
- Reactive forms use `FormBuilder` and validators

## Deployment Considerations
- Backend runs on port 8080
- Frontend runs on port 4200 (dev) or 80 (production with nginx)
- PostgreSQL runs on port 5432
- Environment variables configured in `.env` file (use `.env.example` as template)
- Production builds are containerized with Docker

## Important Notes
- The project uses standalone Angular components (no NgModules)
- CORS is enabled for frontend-backend communication
- The backend auto-initializes with sample data in dev profiles
- Database connection details differ by profile (see application.properties)
- Frontend uses environment files for API URL configuration
