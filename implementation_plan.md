# Backend Implementation Plan: JWT Authentication & Security

The current backend has basic entity structures but lacks security. Passwords are stored in plaintext, and endpoints are unprotected. This plan establishes the security foundation using Spring Security and JWT (JSON Web Tokens).

## User Review Required
> [!IMPORTANT]
> This change will require uncommenting `spring-boot-starter-security` in `pom.xml`.
> **Breaking Change**: Existing users (with plaintext passwords) will no longer be able to login until their passwords are hashed. Since this is a dev environment, we can either manually update them or drop the database.

## Proposed Changes

### Configuration
#### [MODIFY] [pom.xml](file:///home/proyectoacd/ProyectoACD/tec/pom.xml)
- Uncomment `spring-boot-starter-security`.

#### [NEW] `com.restaurant.tec.config.SecurityConfig`
- Define `SecurityFilterChain`.
- Configure public endpoints (`/api/auth/**`, `/api/locales/**` (GET only)).
- Secure sensitive endpoints.
- Register `JwtAuthenticationFilter`.

### Security Components
#### [NEW] `com.restaurant.tec.security.JwtTokenProvider`
- Generate tokens upon login.
- Validate tokens on requests.
- Extract user email from tokens.

#### [NEW] `com.restaurant.tec.security.JwtAuthenticationFilter`
- Intercepts requests to validate "Authorization" header.
- Sets authentication context if token is valid.

#### [NEW] `com.restaurant.tec.security.CustomUserDetailsService`
- Loads user data from database for Spring Security authentication.

### Business Logic
#### [MODIFY] [UserService.java](file:///home/proyectoacd/ProyectoACD/tec/src/main/java/com/restaurant/tec/service/UserService.java)
- Use `BCryptPasswordEncoder` to hash passwords during registration.

#### [NEW] `com.restaurant.tec.controller.AuthController`
- `POST /api/auth/login`: Authenticates user and returns JWT.
- `POST /api/auth/register`: (Existing registration logic moved/linked here).

## Verification Plan

### Automated Tests
- **Unit Tests**: Test `JwtTokenProvider` for correct token generation/validation.
- **Integration Tests**: `MockMvc` tests for Login endpoint.

### Manual Verification
1.  **Register**: Create a new user via API (Postman/Curl).
    - *Verify*: Check database to ensure password is hashed (starts with `$2a$`).
2.  **Login**: Send valid credentials.
    - *Verify*: Receive 200 OK and a JWT token.
3.  **Access Protected Route**: Try to access a secured endpoint (e.g., `POST /api/locales`) without token.
    - *Verify*: Receive 403 Forbidden.
4.  **Access with Token**: Send request with `Authorization: Bearer <token>`.
    - *Verify*: Request succeeds.
