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

    @Transactional
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
        user.setRol("USER");
        user.setEnabled(false); // Usuario inactivo hasta verificar

        UserEntity savedUser = userRepository.save(user);

        // Generar token y enviar email
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser, "EMAIL_VERIFICATION");
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);

        return savedUser;
    }

    @Transactional
    public void verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
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
    public UserEntity updateProfile(Long userId, String nombre, String telefono, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombre != null) user.setNombre(nombre);
        if (telefono != null) user.setTelefono(telefono);
        if (alergenos != null) user.setAlergenos(alergenos);

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateAlergenos(Long userId, String alergenos) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setAlergenos(alergenos);
        return userRepository.save(user);
    }
}
