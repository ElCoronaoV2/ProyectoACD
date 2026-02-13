package com.restaurant.tec.controller;

import com.restaurant.tec.dto.LoginRequest;
import com.restaurant.tec.dto.LoginResponse;
import com.restaurant.tec.dto.RegisterRequest;
import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.security.JwtTokenProvider;
import com.restaurant.tec.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar la autenticación y registro de usuarios.
 * Proporciona endpoints para login, registro, verificación de email y recuperación de contraseña.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "https://restaurant-tec.es", "https://www.restaurant-tec.es", "http://localhost:4200" })
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Autentica un usuario con email y contraseña.
     * 
     * @param loginRequest datos de inicio de sesión (email y contraseña)
     * @return ResponseEntity con el token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        UserEntity user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getRol(),
                user.getAlergenos()));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Envía un email de verificación al usuario.
     * 
     * @param registerRequest datos de registro del usuario
     * @return ResponseEntity con mensaje de éxito o error
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.ok("Usuario registrado. Por favor, revisa tu correo para verificar tu cuenta.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Verifica la cuenta de un usuario usando un token.
     * 
     * @param token token de verificación enviado por email
     * @return ResponseEntity con mensaje de éxito o error
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            userService.verifyUser(token);
            return ResponseEntity.ok("Cuenta verificada exitosamente. Ya puedes iniciar sesión.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     * Envía un email con enlace para restablecer la contraseña.
     * 
     * @param email email del usuario que olvidó su contraseña
     * @return ResponseEntity con mensaje genérico (por seguridad)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            userService.requestPasswordReset(email);
            return ResponseEntity.ok("Si el correo existe, se ha enviado un enlace para restablecer la contraseña.");
        } catch (RuntimeException e) {
            // Por seguridad, no decimos si el email existe o no, pero el log podría
            // mostrarlo
            return ResponseEntity.ok("Si el correo existe, se ha enviado un enlace para restablecer la contraseña.");
        }
    }

    /**
     * Restablece la contraseña de un usuario usando un token.
     * 
     * @param token token de recuperación de contraseña
     * @param newPassword nueva contraseña del usuario
     * @return ResponseEntity con mensaje de éxito o error
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
