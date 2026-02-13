package com.restaurant.tec.controller;

import com.restaurant.tec.dto.LoginRequest;
import com.restaurant.tec.dto.LoginResponse;
import com.restaurant.tec.dto.RegisterRequest;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.security.JwtTokenProvider;
import com.restaurant.tec.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.restaurant.tec.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthController
 * Valida todos los endpoints de autenticación y registro
 * 
 * @author QA Team
 * @version 1.0
 */
@DisplayName("AuthController Unit Tests")
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private UserEntity testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }

    /**
     * Configura datos de prueba reutilizables
     */
    private void setupTestData() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNombre("Test User");
        testUser.setRol(Role.USER);
        testUser.setAlergenos("Mariscos");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setNombre("New User");
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("Login exitoso con credenciales válidas")
    void testLoginSuccess() {
        // Given
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userService.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<LoginResponse> response = authController.authenticateUser(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().getToken());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
        assertEquals(testUser.getId(), response.getBody().getId());
        verify(authenticationManager).authenticate(any());
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    @DisplayName("Login falla con credenciales inválidas")
    void testLoginFailure() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // When & Then
        assertThrows(org.springframework.security.core.AuthenticationException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Login falla cuando usuario no existe")
    void testLoginUserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Registro exitoso de nuevo usuario")
    void testRegisterSuccess() {
        // Given
        when(userService.registerUser(registerRequest)).thenReturn(testUser);

        // When
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("registrado"));
        verify(userService).registerUser(registerRequest);
    }

    @Test
    @DisplayName("Registro falla cuando email ya existe")
    void testRegisterEmailAlreadyExists() {
        // Given
        when(userService.registerUser(registerRequest))
                .thenThrow(new RuntimeException("Email ya registrado"));

        // When
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Email ya registrado"));
        verify(userService).registerUser(registerRequest);
    }

    @Test
    @DisplayName("Registro falla con contraseña débil")
    void testRegisterWeakPassword() {
        // Given
        registerRequest.setPassword("123");
        when(userService.registerUser(registerRequest))
                .thenThrow(new RuntimeException("Contraseña muy débil"));

        // When
        ResponseEntity<?> response = authController.registerUser(registerRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).registerUser(registerRequest);
    }

    // ==================== VERIFY TESTS ====================

    @Test
    @DisplayName("Verificación de email exitosa")
    void testVerifyUserSuccess() {
        // Given
        String token = "verification.token.here";
        doNothing().when(userService).verifyUser(token);

        // When
        ResponseEntity<?> response = authController.verifyUser(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Cuenta verificada"));
        verify(userService).verifyUser(token);
    }

    @Test
    @DisplayName("Verificación falla con token inválido")
    void testVerifyUserInvalidToken() {
        // Given
        String invalidToken = "invalid.token";
        doThrow(new RuntimeException("Token inválido o expirado"))
                .when(userService).verifyUser(invalidToken);

        // When
        ResponseEntity<?> response = authController.verifyUser(invalidToken);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).verifyUser(invalidToken);
    }

    @Test
    @DisplayName("Verificación falla cuando usuario ya está verificado")
    void testVerifyUserAlreadyVerified() {
        // Given
        String token = "verification.token";
        doThrow(new RuntimeException("Usuario ya verificado"))
                .when(userService).verifyUser(token);

        // When
        ResponseEntity<?> response = authController.verifyUser(token);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).verifyUser(token);
    }

    // ==================== FORGOT PASSWORD TESTS ====================

    @Test
    @DisplayName("Solicitud de recuperación de contraseña exitosa")
    void testForgotPasswordSuccess() {
        // Given
        String email = "test@example.com";
        doNothing().when(userService).requestPasswordReset(email);

        // When
        ResponseEntity<?> response = authController.forgotPassword(email);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("enlace"));
        verify(userService).requestPasswordReset(email);
    }

    @Test
    @DisplayName("Solicitud de recuperación retorna mensaje genérico (seguridad)")
    void testForgotPasswordGenericMessage() {
        // Given - Usuario no existe, pero no debe revelar
        String email = "nonexistent@example.com";
        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(userService).requestPasswordReset(email);

        // When
        ResponseEntity<?> response = authController.forgotPassword(email);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Mensaje genérico por seguridad (no revela si existe o no)
        assertTrue(response.getBody().toString().contains("enlace"));
    }

    // ==================== RESET PASSWORD TESTS ====================

    @Test
    @DisplayName("Restablecimiento de contraseña exitoso")
    void testResetPasswordSuccess() {
        // Given
        String token = "reset.token.here";
        String newPassword = "newPassword123";
        doNothing().when(userService).resetPassword(token, newPassword);

        // When
        ResponseEntity<?> response = authController.resetPassword(token, newPassword);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("actualizada"));
        verify(userService).resetPassword(token, newPassword);
    }

    @Test
    @DisplayName("Restablecimiento falla con token inválido")
    void testResetPasswordInvalidToken() {
        // Given
        String invalidToken = "invalid.token";
        String newPassword = "newPassword123";
        doThrow(new RuntimeException("Token inválido o expirado"))
                .when(userService).resetPassword(invalidToken, newPassword);

        // When
        ResponseEntity<?> response = authController.resetPassword(invalidToken, newPassword);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).resetPassword(invalidToken, newPassword);
    }

    @Test
    @DisplayName("Restablecimiento falla con contraseña débil")
    void testResetPasswordWeakPassword() {
        // Given
        String token = "reset.token";
        String weakPassword = "123";
        doThrow(new RuntimeException("Contraseña muy débil"))
                .when(userService).resetPassword(token, weakPassword);

        // When
        ResponseEntity<?> response = authController.resetPassword(token, weakPassword);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).resetPassword(token, weakPassword);
    }

    @Test
    @DisplayName("Restablecimiento falla cuando token ha expirado")
    void testResetPasswordExpiredToken() {
        // Given
        String expiredToken = "expired.token";
        String newPassword = "newPassword123";
        doThrow(new RuntimeException("El token de recuperación ha expirado. Solicita uno nuevo."))
                .when(userService).resetPassword(expiredToken, newPassword);

        // When
        ResponseEntity<?> response = authController.resetPassword(expiredToken, newPassword);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).resetPassword(expiredToken, newPassword);
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Flujo completo: Register → Verify → Login")
    void testCompleteAuthenticationFlow() {
        // Step 1: Register
        when(userService.registerUser(registerRequest)).thenReturn(testUser);
        ResponseEntity<?> registerResponse = authController.registerUser(registerRequest);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // Step 2: Verify
        String verificationToken = "verification.token";
        doNothing().when(userService).verifyUser(verificationToken);
        ResponseEntity<?> verifyResponse = authController.verifyUser(verificationToken);
        assertEquals(HttpStatus.OK, verifyResponse.getStatusCode());

        // Step 3: Login - usar el mismo email del registro
        LoginRequest flowLoginRequest = new LoginRequest();
        flowLoginRequest.setEmail(registerRequest.getEmail());
        flowLoginRequest.setPassword(registerRequest.getPassword());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt.token");
        when(userService.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(testUser));

        ResponseEntity<LoginResponse> loginResponse = authController.authenticateUser(flowLoginRequest);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody().getToken());
    }

    @Test
    @DisplayName("Flujo de recuperación de contraseña completo")
    void testPasswordRecoveryFlow() {
        // Step 1: Forgot Password
        String email = "test@example.com";
        doNothing().when(userService).requestPasswordReset(email);
        ResponseEntity<?> forgotResponse = authController.forgotPassword(email);
        assertEquals(HttpStatus.OK, forgotResponse.getStatusCode());

        // Step 2: Reset Password
        String resetToken = "reset.token.here";
        String newPassword = "newPassword123";
        doNothing().when(userService).resetPassword(resetToken, newPassword);
        ResponseEntity<?> resetResponse = authController.resetPassword(resetToken, newPassword);
        assertEquals(HttpStatus.OK, resetResponse.getStatusCode());
    }
}
