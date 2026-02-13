package com.restaurant.tec.service;

import com.restaurant.tec.entity.UserEntity;
import com.restaurant.tec.entity.VerificationToken;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.repository.VerificationTokenRepository;
import com.restaurant.tec.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar usuarios del sistema.
 * Proporciona funcionalidades de registro, verificación de email, recuperación de contraseña
 * y actualización de perfiles de usuario.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.restaurant.tec.repository.LocalRepository localRepository;

    /**
     * Registra un nuevo usuario en el sistema.
     * Encripta la contraseña, crea el usuario y envía email de verificación.
     * 
     * @param request datos del registro (email, contraseña, nombre, etc.)
     * @return UserEntity el usuario registrado
     * @throws RuntimeException si el email ya está registrado
     */
    @Transactional
    @SuppressWarnings("null")
    public UserEntity registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setTelefono(request.getTelefono());
        user.setAlergenos(request.getAlergenos());

        // Asignar rol: SEGURIDAD - Solo se pueden crear usuarios normales (USER) en self-registration
        // Los roles especiales (DIRECTOR, CEO, EMPLEADO) solo pueden crearse vía admin endpoints
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            String requestedRole = request.getRol().toUpperCase();
            // Solo permitir USER role en registro público
            if (requestedRole.equals("DIRECTOR") || requestedRole.equals("CEO") || requestedRole.equals("EMPLEADO")) {
                throw new RuntimeException("No puedes crear una cuenta con rol de empleado. Contacta al administrador.");
            }
            try {
                user.setRol(com.restaurant.tec.entity.Role.valueOf(requestedRole));
            } catch (IllegalArgumentException e) {
                user.setRol(com.restaurant.tec.entity.Role.USER);
            }
        } else {
            user.setRol(com.restaurant.tec.entity.Role.USER);
        }

        user.setEnabled(true); // Usuarios creados por admin (o CEO/Director) nacen habilitados para evitar
                               // flujo de email si se desea

        UserEntity savedUser = userRepository.save(user);

        // Lógica de asignación de restaurantes
        if (user.getRol() == com.restaurant.tec.entity.Role.EMPLEADO && request.getRestauranteId() != null) {
            com.restaurant.tec.entity.LocalEntity local = localRepository.findById(request.getRestauranteId())
                    .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
            user.setRestauranteTrabajo(local);
            userRepository.save(user);
        } else if (user.getRol() == com.restaurant.tec.entity.Role.CEO && request.getRestauranteIds() != null) {
            for (Long localId : request.getRestauranteIds()) {
                com.restaurant.tec.entity.LocalEntity local = localRepository.findById(localId)
                        .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + localId));
                local.setPropietario(savedUser);
                localRepository.save(local);
            }
        }

        // Si el usuario se registra por sí mismo (flujo normal), enviar email.
        // Si es creado por admin, quizás no queramos enviar verificación de email sino
        // solo activarlo.
        // Asumo que si se pasa rol, es creación administrativa, así que no enviamos
        // verificación
        // pero sí un correo de bienvenida quizás (omitido por ahora).
        // Si NO se pasa rol (registro público), mantenemos flujo original.
        if (request.getRol() == null) {
            user.setEnabled(false);
            userRepository.save(user); // Re-save as disabled

            // Generar token y enviar email
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, savedUser, "EMAIL_VERIFICATION");
            tokenRepository.save(verificationToken);
            tokenRepository.flush();
            emailService.sendVerificationEmail(user.getEmail(), token);
        }

        return savedUser;
    }

    @Transactional
    public void verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        // Validar que sea un token de verificación de email
        if (!"EMAIL_VERIFICATION".equals(verificationToken.getType())) {
            throw new RuntimeException("Token inválido para verificación de cuenta");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado. Por favor, regístrate de nuevo.");
        }

        UserEntity user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user); // Guarda el usuario activado

        tokenRepository.delete(verificationToken); // Elimina el token usado
    }

    @Transactional
    public void requestPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = new VerificationToken(token, user, "PASSWORD_RESET");
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (!"PASSWORD_RESET".equals(resetToken.getType())) {
            throw new RuntimeException("Token inválido para este propósito");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @SuppressWarnings("null")
    public UserEntity updateProfile(Long userId, String nombre, String telefono, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombre != null)
            user.setNombre(nombre);
        if (telefono != null)
            user.setTelefono(telefono);
        if (alergenos != null)
            user.setAlergenos(alergenos);

        return userRepository.save(user);
    }

    @Transactional
    @SuppressWarnings("null")
    public UserEntity updateAlergenos(Long userId, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setAlergenos(alergenos);
        return userRepository.save(user);
    }
}
