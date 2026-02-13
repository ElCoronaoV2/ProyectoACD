package com.restaurant.tec.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar emails transaccionales.
 * Gestiona el envío de emails de verificación, recuperación de contraseña,
 * confirmación de reservas y recordatorios de asistencia.
 * 
 * @author RestaurantTec Team
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envía un email de verificación de cuenta a un usuario.
     * El email contiene un enlace con un token único que expira en 24 horas.
     * 
     * @param to dirección de email del destinatario
     * @param token token de verificación único
     */
    @Async
    public void sendVerificationEmail(String to, String token) {
        String subject = "🍽️ Verifica tu cuenta — RestaurantTec";
        String verificationUrl = "https://restaurant-tec.es/verify?token=" + token;

        String content = buildEmailTemplate(
                "¡Bienvenido a RestaurantTec! 🎉",
                "Estás a un paso de descubrir los mejores restaurantes, menús exclusivos y reservas al instante.",
                "Verificar mi Cuenta",
                verificationUrl,
                "Este enlace expirará en <strong>24 horas</strong>. Si no creaste esta cuenta, puedes ignorar este correo.",
                "#FF6B6B",
                "#FF8E8E");

        sendHtmlEmail(to, subject, content);
    }

    /**
     * Envía un email de recuperación de contraseña.
     * El email contiene un enlace con un token único que expira en 24 horas.
     * 
     * @param to dirección de email del destinatario
     * @param token token de recuperación único
     */
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "🔐 Restablecer Contraseña — RestaurantTec";
        String resetUrl = "https://restaurant-tec.es/reset-password?token=" + token;

        String content = buildEmailTemplate(
                "Recupera tu acceso 🔑",
                "Has solicitado restablecer la contraseña de tu cuenta. No te preocupes, es muy fácil.",
                "Restablecer Contraseña",
                resetUrl,
                "Si no has solicitado este cambio, ignora este correo. Tu contraseña seguirá siendo la misma.",
                "#6C5CE7",
                "#A29BFE");

        sendHtmlEmail(to, subject, content);
    }

    /**
     * Envía un email de confirmación de reserva.
     * Incluye detalles de la reserva: restaurante, fecha, hora y número de personas.
     * 
     * @param to dirección de email del destinatario
     * @param localNombre nombre del restaurante
     * @param fecha fecha de la reserva
     * @param hora hora de la reserva
     * @param personas número de comensales
     * @param idReserva ID de la reserva
     */
    @Async
    public void sendBookingConfirmation(String to, String localNombre, String fecha, String hora, Integer personas,
            String idReserva) {
        String subject = "✅ Reserva Confirmada — " + localNombre;

        String message = "¡Tu reserva está confirmada! Te esperamos en <strong>" + localNombre + "</strong>.<br><br>"
                + "📅 Fecha: " + fecha + "<br>"
                + "⏰ Hora: " + hora + "<br>"
                + "👥 Personas: " + personas + "<br>"
                + "🆔 Reserva: #" + idReserva;

        String content = buildEmailTemplate(
                "¡Reserva Confirmada! 🍽️",
                message,
                "Ver mis Reservas",
                "https://restaurant-tec.es/profile",
                "Si necesitas cancelar o modificar tu reserva, por favor contáctanos con antelación.",
                "#00B894", // Green
                "#55EFC4");

        sendHtmlEmail(to, subject, content);
    }

    /**
     * Envía un recordatorio de asistencia para una reserva.
     * Solicita al usuario confirmar su asistencia al restaurante.
     * 
     * @param to dirección de email del destinatario
     * @param localNombre nombre del restaurante
     * @param fecha fecha de la reserva
     * @param hora hora de la reserva
     * @param reservaId ID de la reserva
     */
    @Async
    public void sendAttendanceReminder(String to, String localNombre, String fecha, String hora, Long reservaId) {
        String subject = "🔔 Confirma tu asistencia — " + localNombre;
        // El endpoint real para confirmar sería algo como:
        // https://restaurant-tec.es/api/reservas/{id}/confirmar-asistencia
        // O un frontend page que llame a la API. Para MVP, usaremos un link directo a
        // la API (GET) o una pagina simple.
        // Asumiremos que hay una pagina de confirmacion o un endpoint GET que redirige.
        // Por ahora, usaremos un link a una pagina de confirmacion en el frontend.
        String confirmUrl = "https://restaurant-tec.es/confirm-attendance?id=" + reservaId;

        String message = "Tu reserva en <strong>" + localNombre + "</strong> es pronto.<br>"
                + "📅 " + fecha + " a las " + hora + "<br><br>"
                + "Por favor, confirma tu asistencia para mantener tu mesa.";

        String content = buildEmailTemplate(
                "¡Tu reserva se acerca! ⏰",
                message,
                "Sí, Asistiré",
                confirmUrl,
                "Si no confirmas, tu reserva podría ser liberada para otros clientes.",
                "#FFA502", // Orange
                "#FFD32A");

        sendHtmlEmail(to, subject, content);
    }

    private String buildEmailTemplate(String title, String message, String buttonText,
            String buttonUrl, String footer, String color1, String color2) {
        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
                + "<body style='margin:0;padding:0;background-color:#0F0F1A;font-family:Segoe UI,Roboto,Helvetica Neue,Arial,sans-serif;'>"
                + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#0F0F1A;padding:40px 0;'>"
                + "<tr><td align='center'>"

                // Main card
                + "<table role='presentation' width='600' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;border-radius:20px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.5);'>"

                // Header with gradient
                + "<tr><td style='background:linear-gradient(135deg," + color1 + "," + color2
                + ");padding:50px 40px;text-align:center;'>"
                + "<div style='font-size:48px;margin-bottom:10px;'>🍽️</div>"
                + "<h1 style='color:#ffffff;font-size:28px;font-weight:700;margin:0 0 5px 0;letter-spacing:-0.5px;'>"
                + title + "</h1>"
                + "</td></tr>"

                // Body
                + "<tr><td style='background-color:#1A1A2E;padding:40px;'>"
                + "<p style='color:#B8B8D0;font-size:16px;line-height:1.7;margin:0 0 30px 0;text-align:center;'>"
                + message + "</p>"

                // CTA Button
                + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0'>"
                + "<tr><td align='center'>"
                + "<a href='" + buttonUrl
                + "' style='display:inline-block;padding:16px 48px;background:linear-gradient(135deg," + color1 + ","
                + color2
                + ");color:#ffffff;text-decoration:none;font-size:16px;font-weight:700;border-radius:50px;letter-spacing:0.5px;box-shadow:0 8px 25px rgba(0,0,0,0.3);'>"
                + buttonText
                + "</a>"
                + "</td></tr>"
                + "</table>"

                // Url fallback
                + "<p style='color:#666680;font-size:12px;text-align:center;margin:25px 0 0 0;line-height:1.5;'>Si el botón no funciona, copia y pega este enlace:<br>"
                + "<a href='" + buttonUrl + "' style='color:" + color1 + ";word-break:break-all;text-decoration:none;'>"
                + buttonUrl + "</a></p>"
                + "</td></tr>"

                // Divider
                + "<tr><td style='background-color:#1A1A2E;padding:0 40px;'>"
                + "<div style='height:1px;background:linear-gradient(to right,transparent," + color1
                + "40,transparent);'></div>"
                + "</td></tr>"

                // Footer
                + "<tr><td style='background-color:#1A1A2E;padding:25px 40px 30px;'>"
                + "<p style='color:#555570;font-size:13px;text-align:center;margin:0 0 15px 0;line-height:1.5;'>"
                + footer + "</p>"
                + "<p style='color:#333348;font-size:12px;text-align:center;margin:0;'>"
                + "© 2026 RestaurantTec · Gestión Gastronómica Inteligente"
                + "</p>"
                + "</td></tr>"

                + "</table>"

                + "</td></tr></table>"
                + "</body></html>";
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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
