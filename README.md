I just wanted to try out Copilot, and suddenly i had a mostly usable web application. Some stuff is weird, some is cool. 

# CocktailDB
A home solution to manage Cocktails and Ingredients. Keep track of your recipes, see what you can make with what you have.

## Features

- **Ingredient Management**: Track all your cocktail ingredients with details like type (spirit, juice, syrup, etc.), ABV, and stock status
- **Cocktail Recipes**: Store your favorite cocktail recipes with ingredients, measurements, and step-by-step instructions
- **Cocktail Variations**: Mark cocktails as variations of base recipes to organize related drinks (e.g., Dirty Martini as variation of Martini)
- **Ingredient Substitutions**: Define substitute and alternative ingredients for flexible recipe preparation
- **Smart Filtering**: Instantly see which cocktails you can make with ingredients currently in stock, including substitution options
- **Fuzzy Search**: Typo-tolerant search across all search fields - find cocktails and ingredients even with spelling errors
- **Full CRUD Operations**: Create, read, update, and delete both ingredients and cocktails
- **Access Control**: Three access modes (Admin, Barkeeper, Visitor) with role-based permissions - see [SECURITY_CONCEPT.md](SECURITY_CONCEPT.md)
- **OpenAPI Documentation**: Complete API documentation with interactive Swagger UI at `/swagger-ui.html`

## Technology Stack

### Backend
- **Spring Boot 3.1.5** with Kotlin
- **Spring Data JPA** for database access
- **PostgreSQL** for production data storage
- **H2 Database** for development/testing
- **Gradle** for build management

### Frontend
- **Angular 20** with TypeScript
- **Standalone Components**
- **Reactive Forms** for data management
- **nginx** for production deployment

### Deployment
- **Docker & Docker Compose** for containerization
- **PostgreSQL 15** for persistent data storage

## Project Structure

```
CocktailDB/
├── backend/                             # Spring Boot backend
│   ├── src/main/kotlin/com/cocktaildb/  # Backend Kotlin code
│   │   ├── appsettings/                 # Application settings
│   │   ├── cocktail/                    # Cocktail domain (entity, repository, services, controller)
│   │   ├── ingredient/                  # Ingredient domain (entity, repository, services, controller)
│   │   ├── security/                    # Authentication & authorization
│   │   ├── controller/                  # Shared controllers (auth, settings, stock updates)
│   │   ├── config/                      # Configuration (CORS, Security, OpenAPI, DataInitializer)
│   │   └── util/                        # Utilities
│   ├── src/main/resources/
│   │   └── db/migration/                # Flyway database migrations
│   └── build.gradle.kts                 # Gradle build file
└── frontend/                            # Angular frontend
    └── src/app/
        ├── components/                  # UI components
        │   ├── admin/                   # Admin section (cocktails, ingredients, settings, visualization)
        │   ├── barkeeper/               # Barkeeper mode components
        │   ├── visitor/                 # Visitor mode components
        │   ├── login/                   # Authentication
        │   └── util/                    # Shared utilities
        ├── guards/                      # Route guards
        ├── services/                    # API services
        ├── models/                      # TypeScript interfaces
        ├── pipes/                       # Custom pipes
        └── i18n/                        # Internationalization
```

## Getting Started

### Database Profiles

The application supports three database profiles:

- **dev** (default): H2 in-memory database - Quick start, no setup required
- **dev-postgres**: PostgreSQL on localhost - Local development with data persistence
- **prod**: PostgreSQL in Docker - Production deployment (automatically used in Docker)

For information about database migrations and backups, see **[docs/DATABASE_MANAGEMENT.md](docs/DATABASE_MANAGEMENT.md)**.

### Prerequisites
- Java 17 or higher
- Node.js 20+ and npm
- Docker (for PostgreSQL options)

### Running the Backend

#### Option 1: With H2 In-Memory Database (Default - `dev` profile)

1. Navigate to the backend directory and build/run the Spring Boot application:
```bash
cd backend
./gradlew bootRun
```

The backend API will be available at `http://localhost:8080`

#### Option 2: With PostgreSQL (Local Development - `dev-postgres` profile)

1. Start a local PostgreSQL database:
```bash
docker run -d \
  --name cocktaildb-dev-postgres \
  -e POSTGRES_DB=cocktaildb \
  -e POSTGRES_USER=cocktaildb \
  -e POSTGRES_PASSWORD=cocktaildb \
  -p 5432:5432 \
  postgres:15-alpine
```

2. Run the backend with the `dev-postgres` profile:
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev-postgres'
```

The backend API will be available at `http://localhost:8080` with data persisted in PostgreSQL.

### Running the Frontend

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies (first time only):
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will be available at `http://localhost:4200`

### Running Both Together

For the full experience, run both the backend and frontend simultaneously:

**Terminal 1 - Backend (with H2):**
```bash
cd backend
./gradlew bootRun
```

Or with PostgreSQL:
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev-postgres'
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm start
```

Then open your browser to `http://localhost:4200`

## API Documentation

CocktailDB provides comprehensive OpenAPI 3.0 documentation for all API endpoints.

### Interactive API Documentation (Swagger UI)

Access the interactive Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

The Swagger UI allows you to:
- Explore all available API endpoints
- Test API calls directly from your browser
- View request/response schemas
- See authentication requirements
- Try out different parameters and request bodies

### OpenAPI Specification

The raw OpenAPI 3.0 JSON specification is available at:
```
http://localhost:8080/api-docs
```

This can be imported into tools like Postman, Insomnia, or used for client code generation.

For detailed OpenAPI documentation, see **[docs/OPENAPI.md](docs/OPENAPI.md)**.

## API Endpoints

### Ingredients
- `GET /api/ingredients` - Get all ingredients
- `GET /api/ingredients/{id}` - Get ingredient by ID
- `GET /api/ingredients/in-stock` - Get ingredients currently in stock
- `POST /api/ingredients` - Create new ingredient
- `PUT /api/ingredients/{id}` - Update ingredient
- `DELETE /api/ingredients/{id}` - Delete ingredient

### Cocktails
- `GET /api/cocktails` - Get all cocktails
- `GET /api/cocktails/{id}` - Get cocktail by ID
- `GET /api/cocktails/available` - Get cocktails that can be made with in-stock ingredients
- `GET /api/cocktails/available-with-substitutions` - Get cocktails categorized by ingredient availability (exact, substitutes, alternatives)
- `POST /api/cocktails` - Create new cocktail
- `PUT /api/cocktails/{id}` - Update cocktail
- `DELETE /api/cocktails/{id}` - Delete cocktail

## Data Models

### Ingredient
```kotlin
{
  "id": Long,
  "name": String,
  "type": IngredientType,  // SPIRIT, LIQUEUR, WINE, BEER, JUICE, SODA, SYRUP, BITTERS, GARNISH, OTHER
  "abv": Int,              // Alcohol by volume percentage
  "inStock": Boolean,      // Whether you have this ingredient
  "substituteIds": [Long], // IDs of ingredients that can directly substitute for this one
  "alternativeIds": [Long] // IDs of alternative ingredients that may alter the cocktail
}
```

**Ingredient Substitutions**: 
- **Substitutes**: Direct replacements (e.g., generic "Coconut Rum" for "Malibu")
- **Alternatives**: Different but usable ingredients (e.g., "Champagne" vs "Prosecco")
- See [docs/INGREDIENT_SUBSTITUTIONS.md](docs/INGREDIENT_SUBSTITUTIONS.md) for complete documentation

### Cocktail
```kotlin
{
  "id": Long,
  "name": String,
  "ingredients": [
    {
      "ingredientId": Long,
      "measure": String    // e.g., "2 oz", "1 dash"
    }
  ],
  "steps": [String],       // Step-by-step instructions
  "notes": String,         // Optional notes
  "tags": [String],        // Tags for categorization
  "abv": Int,              // Calculated alcohol percentage
  "baseSpirit": String,    // Calculated base spirit
  "glasswareTypes": [String], // Recommended glassware (e.g., "highball", "martini")
  "iceTypes": [String],    // Recommended ice (e.g., "cubed", "crushed")
  "variationOfId": Long    // Optional: ID of base cocktail if this is a variation
}
```

## Usage Example

1. **Add Ingredients**: Navigate to the Ingredients page and add your available ingredients (e.g., Vodka, Lime Juice, Simple Syrup). Mark them as "In Stock".

2. **Create Cocktails**: Go to the Cocktails page and create recipes using your ingredients. For example, create a "Vodka Gimlet" with vodka, lime juice, and simple syrup.

3. **Filter Available Cocktails**: Toggle "Show only cocktails I can make" to see recipes you can create with your current stock.

4. **Update Stock**: As you use ingredients or buy new ones, update their stock status on the Ingredients page.

## Deployment

### Using Docker (Recommended)

The easiest way to deploy CocktailDB is using Docker Compose, which sets up all services including the PostgreSQL database for persistent data storage.

#### Prerequisites
- Docker and Docker Compose installed on your system

#### Quick Start

1. Clone the repository:
```bash
git clone https://github.com/MrOdin2/CocktailDB.git
cd CocktailDB
```

2. (Optional) Create a `.env` file from the example and customize if needed:
```bash
cp .env.example .env
```

3. Build the application:
```bash
./build.sh
```

This script will:
- Build the backend JAR file using Gradle
- Build the frontend production bundle using npm

4. Start all services:
```bash
docker compose up -d
```

This will:
- Build and start the backend Docker container
- Build and start the frontend Docker container (with nginx)
- Start a PostgreSQL database for persistent storage

5. Access the application:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432

**For Local Network Access**: Replace `localhost` with your server's IP address (e.g., `http://192.168.1.100`). See [Local Network Testing Guide](docs/local-network-testing.md) for detailed instructions on accessing from multiple devices.

6. Stop the services:
```bash
docker compose down
```

7. To remove all data (including the database):
```bash
docker compose down -v
```

#### Docker Architecture

The Docker setup consists of three services:

1. **postgres**: PostgreSQL 15 database for persistent data storage with automated daily backups
2. **backend**: Spring Boot application running on port 8080 with Flyway database migrations
3. **frontend**: Angular application served by nginx on port 80

Data is persisted in a Docker volume named `postgres_data`, and backups are stored in `postgres_backups`. Your cocktails and ingredients will be preserved even when containers are stopped.

**Database Management**: The application uses Flyway for database migrations and includes automated daily backups. Migrations are version-controlled SQL files in `backend/src/main/resources/db/migration/` that run automatically on startup. See **[docs/DATABASE_MANAGEMENT.md](docs/DATABASE_MANAGEMENT.md)** for details on migrations, backups, and restoration.

### Local Network Testing

CocktailDB is configured to support access from multiple devices on your local network (phones, tablets, laptops). This makes it easy to test the application on different devices or allow multiple users to access the same instance.

For detailed setup instructions, troubleshooting, and firewall configuration, see the [Local Network Testing Guide](docs/local-network-testing.md).

**Quick Summary:**
1. Find your server's IP address: Run `./find-ip.sh` (or manually check with `ip addr` or `ifconfig`)
2. Deploy using Docker Compose as described above
3. Access from other devices using `http://[SERVER_IP]` (e.g., `http://192.168.1.100`)
4. CORS is configured to allow all origins by default for local testing

### Manual Build for Production

#### Backend
```bash
cd backend
./gradlew build
java -jar build/libs/cocktaildb-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend
npm run build
```

The production build will be in `frontend/dist/cocktaildb-frontend`

## Usage Modes & Access Control

CocktailDB is designed with three distinct usage modes to serve different user needs:

### 1. 🎯 Visitor Mode (Public Access)
**Purpose**: Public cocktail discovery and browsing without authentication

**Who it's for**: Guests, friends, or anyone who wants to discover what cocktails can be made

**Features**:
- Browse available cocktails (those that can be made with in-stock ingredients)
- View cocktail recipes with detailed instructions
- Random cocktail picker with filters (alcoholic/non-alcoholic, by base spirit)
- Browse cocktails by categories
- No login required - instant access

**Navigation**: `http://localhost/visitor` (default landing page)

**Note**: For development, use port 4200 (`http://localhost:4200/visitor`). For production deployment with Docker, use port 80 (`http://localhost/visitor`).

### 2. 🍸 Barkeeper Mode (Authenticated)
**Purpose**: Professional cocktail service interface for bartenders

**Who it's for**: Working bartenders who need quick access to recipes and stock management

**Features**:
- View complete cocktail library (all recipes, not just available ones)
- Alphabetical cocktail browser for fast lookup
- Advanced search and filtering
- Stock management - update ingredient availability on the fly
- Random cocktail suggestions with filters
- Streamlined, mobile-friendly interface optimized for bar use

**Authentication**: Required - login with barkeeper credentials

**Navigation**: `http://localhost/barkeeper/menu` (after login)

**Note**: For development, use port 4200 (`http://localhost:4200/barkeeper/menu`).

### 3. ⚙️ Admin Mode (Full Access)
**Purpose**: Complete system administration and recipe management

**Who it's for**: Home bartenders managing their personal cocktail database

**Features**:
- Full CRUD operations on cocktails (Create, Read, Update, Delete)
- Full CRUD operations on ingredients
- Data visualization and analytics
- System settings and configuration
- Import/export functionality
- Everything barkeeper mode offers, plus full editing capabilities

**Authentication**: Required - login with admin credentials

**Navigation**: `http://localhost/cocktails` or `http://localhost/ingredients` (after login)

**Note**: For development, use port 4200 (`http://localhost:4200/cocktails`).

### Access Summary

| Feature | Visitor | Barkeeper | Admin |
|---------|---------|-----------|-------|
| View available cocktails | ✅ | ✅ | ✅ |
| View all cocktails | ❌ | ✅ | ✅ |
| View ingredients | ❌ | ✅ | ✅ |
| Update ingredient stock | ❌ | ✅ | ✅ |
| Create/Edit/Delete cocktails | ❌ | ❌ | ✅ |
| Create/Edit/Delete ingredients | ❌ | ❌ | ✅ |
| Data visualizations | ❌ | ❌ | ✅ |
| System settings | ❌ | ❌ | ✅ |

### Entry Points & User Flow

**First-time visitors**: 
1. Navigate to `http://localhost` → Automatically redirected to Visitor Mode
2. Browse available cocktails or use random picker
3. No login barriers

**Barkeepers**:
1. Navigate to `http://localhost/login`
2. Select "Barkeeper" role and enter password
3. Redirected to barkeeper menu with quick-access tools
4. Use "Logout" button in menu to end session

**Administrators**:
1. Navigate to `http://localhost/login`
2. Select "Admin" role and enter password
3. Redirected to main admin interface
4. Access all features through navigation menu

### Security Architecture

For detailed information about the security implementation, authentication, and session management:
- **[SECURITY_CONCEPT.md](SECURITY_CONCEPT.md)** - Complete security concept, design principles, and user flows
- **[docs/authentication-guide.md](docs/authentication-guide.md)** - Implementation guide with code examples
- **[docs/security-quick-reference.md](docs/security-quick-reference.md)** - Quick setup and troubleshooting guide
- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** - System architecture and contributor guide

## Developer Documentation

For contributors and developers:
- **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Complete architecture guide with diagrams, patterns, and best practices
- **[docs/ACCESSIBILITY.md](docs/ACCESSIBILITY.md)** - Comprehensive accessibility assessment and improvement plan (A11Y Project, WCAG 2.1)
- **[docs/ACCESSIBILITY_QUICK_REFERENCE.md](docs/ACCESSIBILITY_QUICK_REFERENCE.md)** - Quick reference guide for accessible development
- **[docs/OPENAPI.md](docs/OPENAPI.md)** - OpenAPI/Swagger UI documentation and usage guide
- **[docs/DATABASE_MANAGEMENT.md](docs/DATABASE_MANAGEMENT.md)** - Database migrations with Flyway and backup/restore procedures
- **[docs/INGREDIENT_SUBSTITUTIONS.md](docs/INGREDIENT_SUBSTITUTIONS.md)** - Ingredient substitutions and alternatives feature guide
- **[docs/NON_VOLUME_MEASURES.md](docs/NON_VOLUME_MEASURES.md)** - Guide to handling count-based ingredients (garnishes, etc.) alongside volume measures
- **[docs/FUZZY_SEARCH.md](docs/FUZZY_SEARCH.md)** - Fuzzy search implementation using Levenshtein distance for typo-tolerant searching
- **[.github/copilot-instructions.md](.github/copilot-instructions.md)** - Repository conventions and coding standards
- **[SECURITY_CONCEPT.md](SECURITY_CONCEPT.md)** - Contributor guidance on access control implementation

## License

This project is open source and available for personal use.
 
