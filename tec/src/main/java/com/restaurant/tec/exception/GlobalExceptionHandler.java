package com.restaurant.tec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura y formatea errores de autenticación y runtime en respuestas HTTP consistentes.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de credenciales inválidas.
     * 
     * @param ex excepción de credenciales incorrectas
     * @return ResponseEntity con mensaje de error formateado (401 Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "INVALID_CREDENTIALS");
        response.put("message", "La contraseña es incorrecta. Por favor, verifica e intenta de nuevo.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja excepciones de usuario no encontrado.
     * 
     * @param ex excepción de usuario no encontrado
     * @return ResponseEntity con mensaje de error formateado (401 Unauthorized)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UsernameNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "USER_NOT_FOUND");
        response.put("message", "No existe una cuenta con ese correo electrónico.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja excepciones de cuenta deshabilitada (no verificada).
     * 
     * @param ex excepción de cuenta deshabilitada
     * @return ResponseEntity con mensaje de error formateado (403 Forbidden)
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabledAccount(DisabledException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "ACCOUNT_DISABLED");
        response.put("message",
                "Tu cuenta no está verificada. Por favor, revisa tu correo electrónico para activarla.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, String>> handleLockedAccount(LockedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "ACCOUNT_LOCKED");
        response.put("message", "Tu cuenta ha sido bloqueada. Contacta al administrador.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "GENERAL_ERROR");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
