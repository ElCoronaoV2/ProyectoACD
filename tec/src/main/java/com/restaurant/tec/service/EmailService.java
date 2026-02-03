package com.restaurant.tec.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "Verifica tu cuenta - RestaurantTec";
        String verificationUrl = "https://restaurant-tec.es/verify?token=" + token;
        
        String content = "<h2>Bienvenido a RestaurantTec</h2>"
                + "<p>Por favor, haz clic en el siguiente enlace para verificar tu cuenta:</p>"
                + "<a href=\"" + verificationUrl + "\">Verificar Cuenta</a>"
                + "<p>Este enlace expirará en 24 horas.</p>";

        sendHtmlEmail(to, subject, content);
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Restablecer Contraseña - RestaurantTec";
        String resetUrl = "https://restaurant-tec.es/reset-password?token=" + token;
        
        String content = "<h2>Recuperación de Contraseña</h2>"
                + "<p>Has solicitado restablecer tu contraseña. Haz clic aquí:</p>"
                + "<a href=\"" + resetUrl + "\">Restablecer Contraseña</a>"
                + "<p>Si no has sido tú, ignora este correo.</p>";

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo");
        }
    }
}
