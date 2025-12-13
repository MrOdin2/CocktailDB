# OpenAPI Documentation

## Overview

CocktailDB API is fully documented using OpenAPI 3.0 specification. The API documentation is automatically generated from code annotations and is available through interactive Swagger UI.

## Accessing the Documentation

### Swagger UI (Interactive Documentation)

Access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

The Swagger UI provides:
- Interactive API exploration
- Ability to test API endpoints directly from the browser
- Request/response examples
- Schema definitions
- Authentication testing

### OpenAPI JSON Specification

Access the raw OpenAPI specification in JSON format at:

```
http://localhost:8080/api-docs
```

This endpoint returns the complete OpenAPI 3.0 specification that can be:
- Imported into API testing tools (Postman, Insomnia)
- Used for client code generation
- Validated against OpenAPI standards
- Integrated into CI/CD pipelines

## API Structure

### Tags (Endpoint Groups)

The API is organized into the following groups:

- **Authentication**: Login, logout, and session management
- **Cocktails**: Cocktail CRUD operations and search
- **Ingredients**: Ingredient management and inventory
- **Settings**: Application settings
- **Stock Updates**: Real-time stock notifications via SSE

### Authentication

The API uses cookie-based session authentication:

- **Security Scheme**: `cookieAuth`
- **Type**: API Key (Cookie)
- **Cookie Name**: `sessionId`
- **Description**: HTTP-only session cookie set upon successful login

Endpoints requiring authentication are marked with a lock icon (🔒) in Swagger UI.

### Response Codes

All endpoints follow standard HTTP response codes:

- **200 OK**: Successful GET/PUT request
- **201 Created**: Successful POST request (resource created)
- **204 No Content**: Successful DELETE request
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required or invalid
- **404 Not Found**: Resource not found

## Key Endpoints

### Authentication

- `POST /api/auth/login` - Authenticate user and create session
- `POST /api/auth/logout` - Terminate session
- `GET /api/auth/status` - Check authentication status

### Cocktails

- `GET /api/cocktails` - Get all cocktails
- `GET /api/cocktails/{id}` - Get cocktail by ID
- `POST /api/cocktails` 🔒 - Create new cocktail (Admin)
- `PUT /api/cocktails/{id}` 🔒 - Update cocktail (Admin)
- `DELETE /api/cocktails/{id}` 🔒 - Delete cocktail (Admin)
- `GET /api/cocktails/available` - Get makeable cocktails
- `GET /api/cocktails/available-with-substitutions` - Get cocktails with substitution info
- `GET /api/cocktails/search` - Search cocktails by criteria

### Ingredients

- `GET /api/ingredients` - Get all ingredients
- `GET /api/ingredients/{id}` - Get ingredient by ID
- `POST /api/ingredients` 🔒 - Create new ingredient (Admin)
- `PUT /api/ingredients/{id}` 🔒 - Update ingredient (Admin/Barkeeper)
- `DELETE /api/ingredients/{id}` 🔒 - Delete ingredient (Admin)
- `GET /api/ingredients/in-stock` - Get in-stock ingredients

### Settings

- `GET /api/settings/theme` - Get current theme
- `PUT /api/settings/theme` 🔒 - Update theme (Admin)

### Stock Updates

- `GET /api/stock-updates` - Subscribe to real-time stock updates (SSE)

## Data Models

### Cocktail

```json
{
  "id": 1,
  "name": "Mojito",
  "ingredients": [
    {
      "ingredientId": 1,
      "measureMl": 60.0
    }
  ],
  "steps": [
    "Muddle mint and lime",
    "Add rum and ice",
    "Top with soda water"
  ],
  "notes": "Best served in a highball glass",
  "tags": ["refreshing", "summer", "classic"],
  "abv": 12,
  "baseSpirit": "rum"
}
```

**Note**: 
- `abv` and `baseSpirit` are automatically calculated
- For count-based ingredients (GARNISH, OTHER), use negative `measureMl` values (e.g., -2 for 2 pieces)

### Ingredient

```json
{
  "id": 1,
  "name": "White Rum",
  "type": "SPIRIT",
  "abv": 40,
  "inStock": true,
  "substituteIds": [2, 3],
  "alternativeIds": [4, 5]
}
```

**Ingredient Types**:
- `SPIRIT`: Base spirits (vodka, rum, whiskey)
- `LIQUEUR`: Flavored spirits
- `WINE`: Wine-based ingredients
- `BEER`: Beer or malt beverages
- `JUICE`: Fruit or vegetable juice
- `SODA`: Carbonated soft drinks
- `SYRUP`: Sweeteners or flavored syrups
- `BITTERS`: Aromatic bitters
- `GARNISH`: Garnishes (count-based)
- `OTHER`: Other ingredients (count-based)

## Configuration

### SpringDoc OpenAPI Configuration

The OpenAPI configuration is defined in `OpenApiConfig.kt`:

```kotlin
@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("CocktailDB API")
                    .version("1.0.0")
                    .description("API for managing cocktail recipes and ingredients")
            )
            .components(
                Components()
                    .addSecuritySchemes("cookieAuth", SecurityScheme()...)
            )
    }
}
```

### Application Properties

OpenAPI settings in `application.properties`:

```properties
# OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

## Adding Documentation to New Endpoints

When adding new API endpoints, follow these patterns:

### Controller Annotations

```kotlin
@RestController
@RequestMapping("/api/myresource")
@Tag(name = "My Resource", description = "Description of resource endpoints")
class MyController {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get resource by ID",
        description = "Detailed description of what this endpoint does"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Resource found",
                content = [Content(schema = Schema(implementation = MyResource::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Resource not found"
            )
        ]
    )
    fun getById(
        @Parameter(description = "ID of the resource", required = true)
        @PathVariable id: Long
    ): ResponseEntity<MyResource> {
        // Implementation
    }
}
```

### Model Annotations

```kotlin
@Schema(description = "Description of the model")
data class MyModel(
    @Schema(description = "Field description", required = true, example = "example value")
    val field: String
)
```

## Best Practices

1. **Always add descriptions**: Every endpoint and field should have a clear description
2. **Use examples**: Provide realistic examples for better understanding
3. **Document security**: Mark protected endpoints with `@SecurityRequirement`
4. **Specify response codes**: Document all possible HTTP response codes
5. **Keep it updated**: Update documentation when changing API behavior
6. **Use tags**: Group related endpoints for better organization

## Testing the API

### Using Swagger UI

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Find the endpoint you want to test
3. Click "Try it out"
4. Fill in required parameters
5. Click "Execute"
6. Review the response

### For Protected Endpoints

1. First, authenticate via `/api/auth/login`
2. The session cookie will be automatically stored
3. Test protected endpoints (marked with 🔒)
4. The cookie will be included automatically

### Using cURL

```bash
# Get all cocktails
curl http://localhost:8080/api/cocktails

# Login and save session cookie
curl -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"password":"admin","role":"ADMIN"}'

# Use session cookie for protected endpoint
curl -b cookies.txt -X POST http://localhost:8080/api/cocktails \
  -H "Content-Type: application/json" \
  -d '{"name":"New Cocktail",...}'
```

## Deployment Considerations

### Production

In production deployments:

1. **HTTPS Only**: OpenAPI endpoints should be served over HTTPS
2. **Access Control**: Consider restricting access to OpenAPI documentation
3. **CORS**: Ensure CORS is properly configured for Swagger UI
4. **Base URL**: Update base URL in OpenAPI config if deployed on different domain

### Docker

When running in Docker, access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

or on your production domain:

```
https://your-domain.com/swagger-ui.html
```

## Further Reading

- [OpenAPI Specification](https://swagger.io/specification/)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
