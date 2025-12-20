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
CocktailDB_2/
├── backend/                    # Spring Boot backend
│   ├── src/main/kotlin/com/cocktaildb/
│   │   ├── appsettings/       # Application settings (database storage, theme, language)
│   │   │   ├── AppSettings.kt           # Settings entity
│   │   │   ├── AppSettingsRepository.kt # Settings repository
│   │   │   └── AppSettingsService.kt    # Settings business logic
│   │   ├── cocktail/          # Cocktail domain
│   │   │   ├── Cocktail.kt              # Cocktail entity
│   │   │   ├── CocktailIngredient.kt    # Cocktail ingredient embeddable
│   │   │   ├── CocktailRepository.kt    # Cocktail repository
│   │   │   ├── CocktailDataService.kt   # CRUD operations
│   │   │   ├── CocktailSearchService.kt # Search and filtering logic
│   │   │   ├── PatchCocktailService.kt  # Update logic with ABV/spirit calculation
│   │   │   └── CocktailController.kt    # REST endpoints
│   │   ├── ingredient/        # Ingredient domain
│   │   │   ├── Ingredient.kt            # Ingredient entity
│   │   │   ├── IngredientType.kt        # Ingredient type enum
│   │   │   ├── IngredientRepository.kt  # Ingredient repository
│   │   │   ├── IngredientDataService.kt # CRUD operations
│   │   │   ├── PatchIngredientService.kt # Update logic with stock notifications
│   │   │   ├── StockUpdateService.kt    # SSE stock update broadcasts
│   │   │   └── IngredientController.kt  # REST endpoints
│   │   ├── security/          # Authentication & authorization
│   │   │   ├── Session.kt               # Session entity
│   │   │   ├── UserRole.kt              # User role enum
│   │   │   ├── SessionService.kt        # Session management
│   │   │   ├── PasswordService.kt       # Password hashing/verification
│   │   │   └── SessionAuthenticationFilter.kt # Authentication filter
│   │   ├── controller/        # Shared controllers
│   │   │   ├── AuthController.kt        # Authentication endpoints
│   │   │   ├── SettingsController.kt    # Settings endpoints
│   │   │   └── StockUpdateController.kt # SSE endpoint
│   │   ├── config/            # Configuration classes
│   │   │   ├── CorsConfig.kt            # CORS configuration
│   │   │   ├── SecurityConfig.kt        # Security configuration
│   │   │   └── DataInitializer.kt       # Sample data initialization
│   │   └── util/              # Utilities
│   │       └── PasswordHashGenerator.kt # Password hashing utility
│   ├── src/test/kotlin/com/cocktaildb/  # Test sources (77 tests)
│   │   ├── cocktail/          # Cocktail domain tests
│   │   │   ├── CocktailDataServiceTest.kt    # CRUD tests (8)
│   │   │   ├── CocktailSearchServiceTest.kt  # Search tests (7)
│   │   │   ├── PatchCocktailServiceTest.kt   # ABV/spirit tests (11)
│   │   │   └── CocktailRepositoryTest.kt     # Repository tests (5)
│   │   ├── ingredient/        # Ingredient domain tests
│   │   │   ├── IngredientDataServiceTest.kt  # CRUD tests (6)
│   │   │   ├── PatchIngredientServiceTest.kt # Stock update tests (5)
│   │   │   └── IngredientRepositoryTest.kt   # Repository tests (5)
│   │   ├── security/          # Security tests
│   │   │   ├── PasswordServiceTest.kt        # Password tests (4)
│   │   │   └── SessionServiceTest.kt         # Session tests (8)
│   │   └── controller/        # Controller integration tests
│   │       ├── AuthControllerTest.kt         # Auth API tests (8)
│   │       └── CocktailControllerTest.kt     # Cocktail API tests (10)
│   ├── src/test/resources/
│   │   └── application-test.properties       # Test configuration
│   ├── build.gradle.kts       # Gradle build configuration
│   └── scripts/               # Database backup/restore scripts
├── frontend/                   # Angular frontend
│   ├── src/app/
│   │   ├── components/        # UI components (organized by feature)
│   │   │   ├── admin/         # Admin section container
│   │   │   │   ├── cocktails/     # Cocktail management (admin)
│   │   │   │   ├── ingredients/   # Ingredient management (admin)
│   │   │   │   ├── settings/      # App settings (theme, language, password)
│   │   │   │   └── visualization/ # Data visualizations (cocktail, ingredient, trends)
│   │   │   ├── barkeeper/     # Barkeeper mode (menu, recipe, stock-management, etc.)
│   │   │   ├── visitor/       # Visitor mode (menu, recipe, categories, etc.)
│   │   │   ├── login/         # Authentication
│   │   │   └── util/          # Shared utilities
│   │   ├── guards/            # Route guards (auth, admin, barkeeper)
│   │   ├── services/          # API and shared services
│   │   ├── models/            # TypeScript interfaces (models.ts)
│   │   ├── pipes/             # Custom pipes (translate)
│   │   └── i18n/              # Internationalization (de.ts, en.ts)
│   ├── package.json           # npm dependencies
│   └── nginx.conf             # nginx configuration for production
├── docs/                       # Documentation
│   ├── ARCHITECTURE.md
│   ├── authentication-guide.md
│   ├── DATABASE_MANAGEMENT.md
│   ├── security-quick-reference.md
│   ├── TESTING.md             # Testing guide with patterns and examples
│   └── TESTING_SUMMARY.md     # Testing implementation overview
├── docker-compose.yml         # Container orchestration (main)
├── docker-compose.server.yml  # Server deployment configuration
├── docker-compose.registry.yml # Registry deployment configuration
├── .env.example               # Environment variables template
└── build.ps1 / build.sh       # Build scripts
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
- **Package Structure**: Domain-driven design - each domain (cocktail, ingredient, appsettings, security) contains its own entity, repository, services, and controller
- **Service Layer Architecture**: Services are split by responsibility:
  - **DataService**: Handles basic CRUD operations and direct repository access
    - Example: `CocktailDataService`, `IngredientDataService`
  - **PatchService**: Handles updates with side effects (calculations, validations, notifications)
    - Example: `PatchCocktailService` (calculates ABV/baseSpirit), `PatchIngredientService` (broadcasts stock updates)
  - **SearchService**: Handles complex queries and filtering logic
    - Example: `CocktailSearchService` (available cocktails, search by criteria)
  - **Specialized Services**: Handle specific concerns
    - Example: `StockUpdateService` (SSE broadcasts), `SessionService` (session management)
- REST controllers should use appropriate HTTP methods and status codes
- Repository layer handles data persistence
- Use `@Entity` for JPA models, `@Service` for services, `@RestController` for controllers
- Controllers are organized by domain (e.g., `CocktailController` in `cocktail/` package)

### Frontend (TypeScript/Angular)
- Use standalone components (no NgModules)
- Follow Angular style guide
- Use TypeScript strict mode
- Prefer reactive programming with RxJS
- Use services for API calls and shared state
- **Component Organization**: Feature-based structure - components grouped by feature area
  - Admin components are under `admin/` subdirectory (cocktails, ingredients, settings, visualization)
  - Barkeeper and visitor modes have their own directories
  - Shared utilities in `util/`
- Components should be focused and reusable
- Use Angular's reactive forms for form handling
- Route guards protect different user roles (auth, admin, barkeeper)
- Internationalization via custom TranslatePipe and i18n service

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

**Authentication**
- `POST /api/auth/login` - Authenticate user (admin or barkeeper)
- `POST /api/auth/logout` - Terminate session
- `GET /api/auth/status` - Check authentication status

**Settings**
- `GET /api/settings/theme` - Get current theme
- `PUT /api/settings/theme` - Set theme (requires admin)

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
- `GET /api/cocktails/available-with-substitutions` - List cocktails with substitution categorization
- `POST /api/cocktails` - Create cocktail
- `PUT /api/cocktails/{id}` - Update cocktail
- `DELETE /api/cocktails/{id}` - Delete cocktail

**Stock Updates (SSE)**
- `GET /api/stock-updates` - Subscribe to real-time stock updates

### Data Models

**Ingredient**
```kotlin
data class Ingredient(
    val id: Long?,
    val name: String,
    val type: IngredientType,
    val abv: Int,
    val inStock: Boolean,
    val substitutes: Set<Ingredient>,
    val alternatives: Set<Ingredient>
)
```

**IngredientDTO** (for API responses to avoid circular references)
```kotlin
data class IngredientDTO(
    val id: Long?,
    val name: String,
    val type: IngredientType,
    val abv: Int,
    val inStock: Boolean,
    val substituteIds: Set<Long>,
    val alternativeIds: Set<Long>
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
    val notes: String?,
    val tags: List<String>,
    val abv: Int,
    val baseSpirit: String,
    val glasswareTypes: List<String>,
    val iceTypes: List<String>,
    val variationOfId: Long?
)
```

**CocktailIngredient**
```kotlin
data class CocktailIngredient(
    val ingredientId: Long,
    val measureMl: Int
)
```

## Key Features

### Cocktail Variations
Cocktails can be marked as variations of a base recipe using the `variationOfId` field. This allows organizing related cocktails (e.g., Dirty Martini as a variation of Martini). The feature helps users discover recipe variations and understand cocktail families.

### Ingredient Substitutions and Alternatives
Ingredients support two types of relationships:
- **Substitutes**: Direct replacements that don't significantly change the cocktail (e.g., generic "Coconut Rum" for branded "Malibu")
- **Alternatives**: Different ingredients that can be used but may alter the cocktail's character (e.g., "Champagne" vs "Prosecco")

The `/api/cocktails/available-with-substitutions` endpoint categorizes cocktails into three groups:
- **exact**: Can be made with exact in-stock ingredients
- **withSubstitutes**: Can be made using substitutes
- **withAlternatives**: Can be made using alternatives

Relationships are bidirectional and managed through many-to-many join tables. DTOs are used for serialization to avoid circular references.

### OpenAPI Documentation
The API is fully documented using OpenAPI 3.0 specification with SpringDoc:
- **Swagger UI**: Interactive API documentation at `http://localhost:8080/swagger-ui.html`
- **JSON Spec**: Raw OpenAPI specification at `http://localhost:8080/api-docs`
- All endpoints are documented with descriptions, parameters, request/response schemas, and authentication requirements
- Security scheme uses cookie-based session authentication

### Glassware and Ice Types
Cocktails can specify recommended glassware types (e.g., "highball", "martini") and ice types (e.g., "cubed", "crushed") in the `glasswareTypes` and `iceTypes` fields. These are stored as collections and help users prepare cocktails correctly.

For more details, see:
- `docs/INGREDIENT_SUBSTITUTIONS.md` - Complete guide to substitutions feature
- `docs/OPENAPI.md` - OpenAPI documentation guide
- `docs/DATABASE_MANAGEMENT.md` - Flyway migrations and database management

## Development Workflow

### Making Changes
1. Backend changes: Modify Kotlin files in `backend/src/main/kotlin/com/cocktaildb/`
2. Frontend changes: Modify TypeScript files in `frontend/src/app/`
3. Test locally before committing
4. Ensure both backend and frontend build successfully

### Adding New Features
- **New API endpoint**: Add to appropriate domain controller, implement in service layer
- **New model/entity**: Create in appropriate domain package with JPA annotations
- **New CRUD operations**: Add to the domain's DataService
- **New business logic**: Create specialized service (e.g., SearchService, PatchService) or add to existing one
- **New frontend component**: Create standalone component with inline template or separate HTML
- **New API call**: Add method to `api.service.ts`

### Database Changes
- Models use JPA annotations
- Schema is managed by Flyway migrations in `backend/src/main/resources/db/migration/`
- Migrations are versioned (V1, V2, V3, etc.) and run automatically on startup
- For production, Flyway ensures consistent schema evolution across deployments

## Testing

### Backend Testing
The backend has comprehensive test coverage with 77 tests across 11 test classes covering service, controller, and repository layers.

#### Test Infrastructure
- **Testing Framework**: JUnit 5 (Jupiter) - included in Spring Boot Starter Test
- **Mocking Library**: MockK 1.13.8 (Kotlin-friendly mocking)
- **Spring Integration**: SpringMockK 4.0.2 (Spring Boot integration for MockK)
- **Assertions**: JUnit assertions + AssertJ (from Spring Boot Starter Test)
- **Test Database**: H2 in-memory database
- **HTTP Testing**: Spring MockMvc for controller tests

#### Test Architecture (Three-Layer Pattern)

**1. Unit Tests (49 tests)** - Service layer with mocked dependencies
- Located in `src/test/kotlin/com/cocktaildb/{domain}/`
- Use MockK to mock repository and service dependencies
- Fast execution, isolated testing of business logic
- Examples:
  - `PasswordServiceTest` (4 tests) - Password hashing/verification
  - `SessionServiceTest` (8 tests) - Session lifecycle management
  - `CocktailDataServiceTest` (8 tests) - CRUD operations
  - `CocktailSearchServiceTest` (7 tests) - Search and filtering
  - `PatchCocktailServiceTest` (11 tests) - ABV calculation and base spirit determination
  - `IngredientDataServiceTest` (6 tests) - Ingredient operations
  - `PatchIngredientServiceTest` (5 tests) - Stock change detection and broadcasting

**2. Integration Tests (18 tests)** - Controller layer with full Spring context
- Located in `src/test/kotlin/com/cocktaildb/controller/`
- Use `@SpringBootTest` and `@AutoConfigureMockMvc`
- Test REST endpoints with MockMvc
- Test authentication requirements
- Examples:
  - `AuthControllerTest` (8 tests) - Login/logout/status endpoints
  - `CocktailControllerTest` (10 tests) - Cocktail CRUD and search APIs

**3. Repository Tests (10 tests)** - Data layer with H2 database
- Located in `src/test/kotlin/com/cocktaildb/{domain}/`
- Use `@DataJpaTest` for minimal Spring context
- Test database queries and entity persistence
- Examples:
  - `CocktailRepositoryTest` (5 tests) - Cocktail persistence
  - `IngredientRepositoryTest` (5 tests) - Ingredient queries including stock filtering

#### Test Configuration
- **Test Profile**: `application-test.properties` in `src/test/resources/`
- **Database**: H2 in-memory with `create-drop` schema generation
- **Flyway**: Disabled for tests (Hibernate creates schema)
- **Test Credentials**: Admin password "admin", Barkeeper password "barkeeper"
- **Session Timeout**: 60 minutes

#### Running Tests
```bash
cd backend

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.cocktaildb.security.PasswordServiceTest"

# Run tests for a package
./gradlew test --tests "com.cocktaildb.controller.*"

# Build with tests
./gradlew build
```

Test reports are generated at `backend/build/reports/tests/test/index.html`

#### Writing New Tests

**Unit Test Pattern:**
```kotlin
class MyServiceTest {
    private lateinit var myRepository: MyRepository
    private lateinit var myService: MyService
    
    @BeforeEach
    fun setup() {
        myRepository = mockk()
        myService = MyService(myRepository)
    }
    
    @Test
    fun `should do something`() {
        // Given
        every { myRepository.findAll() } returns listOf(...)
        
        // When
        val result = myService.doSomething()
        
        // Then
        assertEquals(expected, result)
        verify { myRepository.findAll() }
    }
}
```

**Integration Test Pattern:**
```kotlin
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var sessionService: SessionService
    
    private lateinit var sessionId: String
    
    @BeforeEach
    fun setup() {
        sessionId = sessionService.createSession(UserRole.ADMIN)
    }
    
    @Test
    fun `should create resource`() {
        mockMvc.perform(
            post("/api/resources")
                .cookie(Cookie("sessionId", sessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
    }
}
```

**Repository Test Pattern:**
```kotlin
@DataJpaTest
@ActiveProfiles("test")
class MyRepositoryTest {
    @Autowired
    private lateinit var myRepository: MyRepository
    
    @BeforeEach
    fun setup() {
        myRepository.deleteAll()
    }
    
    @Test
    fun `should save and retrieve entity`() {
        // Given
        val entity = MyEntity(name = "Test")
        
        // When
        val saved = myRepository.save(entity)
        val retrieved = myRepository.findById(saved.id!!).orElse(null)
        
        // Then
        assertNotNull(retrieved)
        assertEquals("Test", retrieved.name)
    }
}
```

#### Test Best Practices
1. **Descriptive Names**: Use backticks for readable test names (e.g., `` `should return cocktail when exists` ``)
2. **Given-When-Then**: Structure tests clearly with setup, execution, and verification
3. **One Assertion Per Test**: Focus each test on a single behavior
4. **Mock External Dependencies**: Don't make real external API calls
5. **Clean Up**: Use `@Transactional` or `@BeforeEach` cleanup for test isolation
6. **Test Edge Cases**: Include tests for null values, empty lists, boundary conditions
7. **Authentication in Tests**: Create session via SessionService and pass as cookie for authenticated endpoints

#### Test Coverage Guidelines
- **New Services**: Always add unit tests covering main functionality and edge cases
- **New Controllers**: Add integration tests for all endpoints
- **New Repositories**: Add tests for custom query methods
- **Bug Fixes**: Add regression tests to prevent recurrence
- **Refactoring**: Ensure all tests still pass

For detailed testing documentation, see `docs/TESTING.md` and `docs/TESTING_SUMMARY.md`.

### Frontend Testing
- Frontend: Use Jasmine and Karma (Angular's default testing framework)
- Test coverage for frontend is pending implementation

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
