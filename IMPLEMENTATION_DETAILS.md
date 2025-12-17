# Implementation Details & Design Decisions

## Architecture Overview
The application follows a standard Spring Boot layered architecture:
1.  **Web Layer (`AuditionController`)**: Handles HTTP requests, input validation, and response formatting.
2.  **Service Layer (`AuditionService`)**: Contains business logic. Currently acts as a pass-through but includes filtering logic for Posts.
3.  **Integration Layer (`AuditionIntegrationClient`)**: Manages communication with the upstream `jsonplaceholder` API. It handles `RestTemplate` calls and maps HTTP errors to domain-specific `SystemException`s.

## Key Design Decisions

### 1. Observability (Logging & Tracing)
-   **Micrometer Tracing**: Replaced Spring Cloud Sleuth (deprecated in Boot 3) with Micrometer Tracing + Brave.
-   **Response Header Injection**: A `ResponseHeaderInjector` filter was added to inject `X-Trace-Id` and `X-Span-Id` into HTTP response headers. This allows clients to correlate their requests with server logs.
-   **Logging Interceptor**: A `ClientHttpRequestInterceptor` was added to the `RestTemplate`. This logs the Request URI/Method and Response Status/Body for every upstream call, which is critical for debugging integration issues.

### 2. Error Handling
-   **Global Exception Handler**: `ExceptionControllerAdvice` manages all exceptions.
-   **ProblemDetail**: The API uses the RFC 7807 `ProblemDetail` specification (native in Spring Boot 3) for error responses.
-   **SystemException**: A custom unchecked exception wraps upstream errors.
    -   **404 Handling**: Specifically caught in the Client to return a clean "Resource Not Found" message rather than a generic 500.
    -   **Fallback**: Unknown upstream errors default to 500 but preserve the original status code in the logs.

### 3. Configuration
-   **ObjectMapper**: Configured to be lenient (`FAIL_ON_UNKNOWN_PROPERTIES = false`) to ensure forward compatibility if the upstream API adds new fields.
-   **Security**: Actuator endpoints are restricted. Only `health` and `info` are exposed to prevent leaking environment details or bean definitions.

### 4. Data Filtering
-   Filtering (by title and body) was implemented in the Controller/Service layer using Java Streams.
-   *Trade-off*: In a real database-backed application, this filtering should happen at the database level (SQL) for performance. Since we are consuming a REST API that doesn't support these specific filters via query params, in-memory filtering is the necessary approach.

### 5. Code Quality
-   **Checkstyle & PMD**: Enabled in `build.gradle`.
-   **Lombok**: Used to reduce boilerplate code in Models.
-   **Testing**: Added `WebMvcTest` for the Controller and `Mockito` unit tests for Service and Client layers to ensure high code coverage.

## How to Run
1.  `./gradlew clean build`
2.  `./gradlew bootRun`
3.  Access Swagger UI: `http://localhost:8080/swagger-ui/index.html` (via SpringDoc)