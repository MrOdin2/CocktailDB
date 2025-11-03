# CocktailDB
A home-solution to manage Cocktails and Ingredients. Keep track of your recipes, see what you can make with what you have.

## Features

- **Ingredient Management**: Track all your cocktail ingredients with details like type (spirit, juice, syrup, etc.), ABV, and stock status
- **Cocktail Recipes**: Store your favorite cocktail recipes with ingredients, measurements, and step-by-step instructions
- **Smart Filtering**: Instantly see which cocktails you can make with ingredients currently in stock
- **Full CRUD Operations**: Create, read, update, and delete both ingredients and cocktails

## Technology Stack

### Backend
- **Spring Boot 3.1.5** with Kotlin
- **Spring Data JPA** for database access
- **H2 Database** for in-memory storage
- **Gradle** for build management

### Frontend
- **Angular 18** with TypeScript
- **Standalone Components**
- **Reactive Forms** for data management

## Project Structure

```
CocktailDB/
├── backend/                             # Spring Boot backend
│   ├── src/main/kotlin/com/cocktaildb/  # Backend Kotlin code
│   │   ├── controller/                  # REST API controllers
│   │   ├── model/                       # JPA entities
│   │   ├── repository/                  # Data repositories
│   │   └── service/                     # Business logic
│   └── build.gradle.kts                 # Gradle build file
└── frontend/                            # Angular frontend
    └── src/app/
        ├── components/                  # UI components
        ├── models/                      # TypeScript interfaces
        └── services/                    # API service
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm

### Running the Backend

1. Navigate to the backend directory and build/run the Spring Boot application:
```bash
cd backend
./gradlew bootRun
```

The backend API will be available at `http://localhost:8080`

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

**Terminal 1 - Backend:**
```bash
cd backend
./gradlew bootRun
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm start
```

Then open your browser to `http://localhost:4200`

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
  "inStock": Boolean       // Whether you have this ingredient
}
```

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
  "notes": String          // Optional notes
}
```

## Usage Example

1. **Add Ingredients**: Navigate to the Ingredients page and add your available ingredients (e.g., Vodka, Lime Juice, Simple Syrup). Mark them as "In Stock".

2. **Create Cocktails**: Go to the Cocktails page and create recipes using your ingredients. For example, create a "Vodka Gimlet" with vodka, lime juice, and simple syrup.

3. **Filter Available Cocktails**: Toggle "Show only cocktails I can make" to see recipes you can create with your current stock.

4. **Update Stock**: As you use ingredients or buy new ones, update their stock status on the Ingredients page.

## Building for Production

### Backend
```bash
cd backend
./gradlew build
java -jar build/libs/cocktaildb-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
```

The production build will be in `frontend/dist/cocktaildb-frontend`

## Security Notes

The frontend uses Angular 18 which includes recent security updates. When running `npm install`, you may see some npm audit warnings about vulnerabilities in development dependencies (esbuild, tmp, etc.). These are:

- **Development-only dependencies**: The reported vulnerabilities are in packages used only during development (dev server, CLI tools), not in the production build.
- **Low to moderate severity**: All vulnerabilities are rated as low to moderate, affecting only the development environment.
- **Not affecting production**: The production build (`npm run build`) does not include these development dependencies.

To completely eliminate all warnings would require upgrading to Angular 20+, which would introduce breaking changes to the application. The current setup with Angular 18 provides a good balance between security and stability.

If you're concerned about development environment security:
- Only run the development server on trusted networks
- Don't expose the dev server (port 4200) to the public internet
- Use the production build for any deployment


## License

This project is open source and available for personal use.
 
