package com.restaurant.tec.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class N8nService {

    @Value("${n8n.webhook.confirmation}")
    private String confirmationWebhookUrl;

    @Value("${n8n.webhook.reminder}")
    private String reminderWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendBookingConfirmation(String email, String nombre, String localNombre,
            String fecha, String hora, Integer personas, String reservaId, String observaciones, String ceoEmail) {
        if (confirmationWebhookUrl == null || confirmationWebhookUrl.isEmpty()) {
            System.out.println("N8n confirmation webhook URL not configured.");
            return;
        }

        String safeName = nombre != null ? nombre : "Cliente";
        String safeObs = observaciones != null ? observaciones : "";
        String safeCeoEmail = ceoEmail != null ? ceoEmail : "";

        // Pre-render the full HTML email
        String htmlBody = buildConfirmationHtml(safeName, localNombre, fecha, hora, personas, reservaId, safeObs);

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("subject", "Reserva Confirmada - " + localNombre);
        payload.put("htmlBody", htmlBody);
        payload.put("ceoEmail", safeCeoEmail);
        // Also send individual fields in case n8n needs them for other nodes
        payload.put("nombre", safeName);
        payload.put("local", localNombre);
        payload.put("fecha", fecha);
        payload.put("hora", hora);
        payload.put("personas", personas);
        payload.put("reservaId", reservaId);

        sendWebhook(confirmationWebhookUrl, payload);
    }

    public void sendBookingReminder(String email, String nombre, String localNombre,
            String fecha, String hora, Integer personas, String reservaId) {
        if (reminderWebhookUrl == null || reminderWebhookUrl.isEmpty()) {
            System.out.println("N8n reminder webhook URL not configured.");
            return;
        }

        String safeName = nombre != null ? nombre : "Cliente";
        String confirmLink = "https://www.restaurant-tec.es/reservas/confirmar/" + reservaId;

        String htmlBody = buildReminderHtml(safeName, localNombre, fecha, hora, personas, reservaId, confirmLink);

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("subject", "Recordatorio de Reserva - " + localNombre);
        payload.put("htmlBody", htmlBody);
        // Also send individual fields
        payload.put("nombre", safeName);
        payload.put("local", localNombre);
        payload.put("fecha", fecha);
        payload.put("hora", hora);
        payload.put("personas", personas);
        payload.put("reservaId", reservaId);
        payload.put("confirmLink", confirmLink);

        sendWebhook(reminderWebhookUrl, payload);
    }

    private String buildConfirmationHtml(String nombre, String local, String fecha,
            String hora, Integer personas, String reservaId, String observaciones) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background-color:#1a1a2e;font-family:Arial,Helvetica,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#1a1a2e;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#16213e;border-radius:16px;overflow:hidden;box-shadow:0 4px 30px rgba(0,0,0,0.3);'>"
                // Header
                + "<tr><td style='background:linear-gradient(135deg,#0f3460,#533483);padding:40px 30px;text-align:center;'>"
                + "<div style='width:70px;height:70px;background:rgba(255,255,255,0.15);border-radius:50%;margin:0 auto 15px;line-height:70px;font-size:36px;'>&#10004;</div>"
                + "<h1 style='color:#ffffff;margin:0 0 5px;font-size:26px;'>Reserva Confirmada</h1>"
                + "<p style='color:#e0c3fc;margin:0;font-size:14px;'>Referencia #" + reservaId + "</p>"
                + "</td></tr>"
                // Body
                + "<tr><td style='padding:30px;'>"
                + "<p style='color:#e0e0e0;font-size:16px;margin:0 0 20px;'>Hola <strong style=\"color:#ffffff;\">"
                + nombre + "</strong>,</p>"
                + "<p style='color:#b0b0b0;font-size:14px;margin:0 0 25px;'>Tu reserva ha sido registrada con &eacute;xito. Aqu&iacute; tienes los detalles:</p>"
                // Details card
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#0f3460;border-radius:12px;overflow:hidden;margin-bottom:25px;'>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Restaurante</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + local + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Fecha</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + fecha + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Hora</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + hora + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Personas</span>"
                + "<p style='color:#e94560;font-size:20px;margin:5px 0 0;font-weight:bold;'>" + personas + "</p>"
                + "</td></tr>"
                + "</table>"
                // Observations
                + (observaciones != null && !observaciones.isEmpty()
                        ? "<div style='background-color:#0f3460;border-radius:12px;padding:18px 20px;margin-bottom:25px;'>"
                                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Observaciones</span>"
                                + "<p style='color:#e0e0e0;font-size:14px;margin:5px 0 0;'>" + observaciones + "</p>"
                                + "</div>"
                        : "")
                // CTA button
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:10px 0 0;'>"
                + "<a href='https://www.restaurant-tec.es' style='display:inline-block;background:linear-gradient(135deg,#e94560,#533483);color:#ffffff;text-decoration:none;padding:14px 35px;border-radius:8px;font-size:15px;font-weight:bold;'>Ver mi reserva</a>"
                + "</td></tr></table>"
                + "</td></tr>"
                // Footer
                + "<tr><td style='background-color:#0f3460;padding:20px 30px;text-align:center;border-top:1px solid rgba(255,255,255,0.1);'>"
                + "<p style='color:#888;font-size:12px;margin:0;'>Restaurant-Tec 2026</p>"
                + "<p style='color:#666;font-size:11px;margin:5px 0 0;'>Mensaje autom&aacute;tico, por favor no respondas.</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private String buildReminderHtml(String nombre, String local, String fecha,
            String hora, Integer personas, String reservaId, String confirmLink) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background-color:#1a1a2e;font-family:Arial,Helvetica,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#1a1a2e;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='background-color:#16213e;border-radius:16px;overflow:hidden;box-shadow:0 4px 30px rgba(0,0,0,0.3);'>"
                // Header
                + "<tr><td style='background:linear-gradient(135deg,#e94560,#533483);padding:40px 30px;text-align:center;'>"
                + "<div style='width:70px;height:70px;background:rgba(255,255,255,0.15);border-radius:50%;margin:0 auto 15px;line-height:70px;font-size:36px;'>&#128276;</div>"
                + "<h1 style='color:#ffffff;margin:0 0 5px;font-size:26px;'>Recordatorio de Reserva</h1>"
                + "<p style='color:#e0c3fc;margin:0;font-size:14px;'>Referencia #" + reservaId + "</p>"
                + "</td></tr>"
                // Body
                + "<tr><td style='padding:30px;'>"
                + "<p style='color:#e0e0e0;font-size:16px;margin:0 0 20px;'>Hola <strong style=\"color:#ffffff;\">"
                + nombre + "</strong>,</p>"
                + "<p style='color:#b0b0b0;font-size:14px;margin:0 0 25px;'>Te recordamos que tienes una reserva pr&oacute;ximamente:</p>"
                // Details card
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#0f3460;border-radius:12px;overflow:hidden;margin-bottom:25px;'>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Restaurante</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + local + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Fecha</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + fecha + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;border-bottom:1px solid rgba(255,255,255,0.1);'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Hora</span>"
                + "<p style='color:#ffffff;font-size:16px;margin:5px 0 0;font-weight:bold;'>" + hora + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:18px 20px;'>"
                + "<span style='color:#a0a0a0;font-size:12px;text-transform:uppercase;letter-spacing:1px;'>Personas</span>"
                + "<p style='color:#e94560;font-size:20px;margin:5px 0 0;font-weight:bold;'>" + personas + "</p>"
                + "</td></tr>"
                + "</table>"
                // CTA button
                + "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:10px 0 0;'>"
                + "<a href='" + confirmLink
                + "' style='display:inline-block;background:linear-gradient(135deg,#e94560,#533483);color:#ffffff;text-decoration:none;padding:14px 35px;border-radius:8px;font-size:15px;font-weight:bold;'>Confirmar asistencia</a>"
                + "</td></tr></table>"
                + "</td></tr>"
                // Footer
                + "<tr><td style='background-color:#0f3460;padding:20px 30px;text-align:center;border-top:1px solid rgba(255,255,255,0.1);'>"
                + "<p style='color:#888;font-size:12px;margin:0;'>Restaurant-Tec 2026</p>"
                + "<p style='color:#666;font-size:11px;margin:5px 0 0;'>Mensaje autom&aacute;tico, por favor no respondas.</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private void sendWebhook(String url, Map<String, Object> payload) {
        try {
            // Debug: Print JSON payload (without htmlBody to keep logs clean)
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> logPayload = new HashMap<>(payload);
            if (logPayload.containsKey("htmlBody")) {
                logPayload.put("htmlBody", "[HTML_CONTENT_" + ((String) payload.get("htmlBody")).length() + "_chars]");
            }
            String jsonPayload = mapper.writeValueAsString(logPayload);
            System.out.println("Sending n8n payload to " + url + ": " + jsonPayload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("N8n webhook sent successfully to: " + url);
        } catch (Exception e) {
            System.err.println("Error sending n8n webhook: " + e.getMessage());
        }
    }
}
