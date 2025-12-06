# Backend Testing Documentation

## Overview

This document describes the testing strategy and test infrastructure for the CocktailDB backend.

## Test Strategy

The backend tests are organized into three layers following Spring Boot testing best practices:

### 1. Unit Tests
Tests for individual service classes with mocked dependencies.

**Location**: `src/test/kotlin/com/cocktaildb/`

**Examples**:
- `PasswordServiceTest` - Tests password hashing and verification
- `SessionServiceTest` - Tests session management logic
- `CocktailDataServiceTest` - Tests cocktail CRUD operations
- `CocktailSearchServiceTest` - Tests cocktail search and filtering logic
- `IngredientDataServiceTest` - Tests ingredient CRUD operations

**Characteristics**:
- Fast execution
- No Spring context loading
- Dependencies are mocked using MockK
- Focus on business logic

### 2. Integration Tests
Tests for controller endpoints with full Spring context.

**Location**: `src/test/kotlin/com/cocktaildb/controller/`

**Examples**:
- `AuthControllerTest` - Tests authentication endpoints
- `CocktailControllerTest` - Tests cocktail REST API endpoints

**Characteristics**:
- Full Spring Boot application context
- MockMvc for HTTP request simulation
- Real database (H2 in-memory)
- Tests API contracts and HTTP responses

### 3. Repository Tests
Tests for JPA repository layer with database.

**Location**: `src/test/kotlin/com/cocktaildb/{domain}/`

**Examples**:
- `CocktailRepositoryTest` - Tests cocktail persistence
- `IngredientRepositoryTest` - Tests ingredient persistence

**Characteristics**:
- Uses `@DataJpaTest` for minimal context
- H2 in-memory database
- Tests database queries and entity mappings

## Technology Stack

- **Testing Framework**: JUnit 5 (Jupiter)
- **Mocking Library**: MockK 1.13.8 (Kotlin-friendly mocking)
- **Spring Test Support**: SpringMockK 4.0.2
- **Assertions**: JUnit assertions + AssertJ (from Spring Boot Starter Test)
- **Database**: H2 in-memory database for tests
- **HTTP Testing**: Spring MockMvc

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests for Specific Class
```bash
./gradlew test --tests "com.cocktaildb.security.PasswordServiceTest"
```

### Run Tests for Specific Package
```bash
./gradlew test --tests "com.cocktaildb.controller.*"
```

### Run with Detailed Output
```bash
./gradlew test --info
```

### Generate Test Report
```bash
./gradlew test
# Report available at: build/reports/tests/test/index.html
```

## Test Configuration

### Test Profile
Tests use the `test` profile configured in `src/test/resources/application-test.properties`:

```properties
# H2 in-memory database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Flyway disabled (Hibernate creates schema)
spring.flyway.enabled=false

# Test credentials
admin.password.hash=$2a$12$...  # Password: "admin"
barkeeper.password.hash=$2a$12$...  # Password: "barkeeper"
```

### Dependencies
Test dependencies are configured in `build.gradle.kts`:

```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.springframework.security:spring-security-test")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("com.ninja-squad:springmockk:4.0.2")
```

## Writing New Tests

### Unit Test Example
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

### Integration Test Example
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
    
    private lateinit var adminSessionId: String
    
    @BeforeEach
    fun setup() {
        adminSessionId = sessionService.createSession(UserRole.ADMIN)
    }
    
    @Test
    fun `should create resource`() {
        mockMvc.perform(
            post("/api/resources")
                .cookie(Cookie("sessionId", adminSessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{...}")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
    }
}
```

### Repository Test Example
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

## Test Coverage

Current test coverage includes:

### Services (Unit Tests)
- ✅ PasswordService - 5 tests
- ✅ SessionService - 8 tests
- ✅ CocktailDataService - 7 tests
- ✅ CocktailSearchService - 7 tests
- ✅ IngredientDataService - 6 tests

### Controllers (Integration Tests)
- ✅ AuthController - 8 tests
- ✅ CocktailController - 10 tests

### Repositories (Integration Tests)
- ✅ CocktailRepository - 6 tests
- ✅ IngredientRepository - 5 tests

**Total: 62+ tests**

## Best Practices

1. **Use descriptive test names**: Use backticks for readable test names
   ```kotlin
   @Test
   fun `should return cocktail when exists`()
   ```

2. **Follow Given-When-Then pattern**: Structure tests clearly
   ```kotlin
   // Given
   val input = ...
   
   // When
   val result = service.doSomething(input)
   
   // Then
   assertEquals(expected, result)
   ```

3. **Test one thing per test**: Keep tests focused

4. **Use appropriate test type**: 
   - Unit tests for business logic
   - Integration tests for API contracts
   - Repository tests for database queries

5. **Clean up test data**: Use `@Transactional` or `@BeforeEach` cleanup

6. **Mock external dependencies**: Don't make real external API calls

7. **Test edge cases**: Test null values, empty lists, boundary conditions

## Continuous Integration

Tests are automatically run in CI/CD pipelines:
- All tests must pass before merging
- Test reports are generated and archived
- Coverage reports can be integrated (future improvement)

## Future Improvements

Potential enhancements to the test infrastructure:

1. **Code Coverage**: Add JaCoCo for coverage reporting
2. **Mutation Testing**: Add PITest for mutation testing
3. **Performance Tests**: Add JMH benchmarks for critical paths
4. **Contract Tests**: Add Spring Cloud Contract for API contracts
5. **Testcontainers**: Use Testcontainers for PostgreSQL integration tests
6. **Parameterized Tests**: Use `@ParameterizedTest` for data-driven tests

## Troubleshooting

### Common Issues

**Issue**: Tests fail with "Session not found"
- **Solution**: Ensure you're creating a session in `@BeforeEach` and passing the session cookie

**Issue**: Tests fail with authentication error
- **Solution**: Check that you're using correct test passwords ("admin" or "barkeeper")

**Issue**: Database state issues between tests
- **Solution**: Use `@Transactional` annotation or cleanup in `@BeforeEach`

**Issue**: MockK verification fails
- **Solution**: Ensure mock setup matches actual calls exactly

## References

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [MockK Documentation](https://mockk.io/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
