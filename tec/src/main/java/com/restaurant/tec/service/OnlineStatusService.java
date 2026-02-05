package com.restaurant.tec.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineStatusService {

    // Almacena la última actividad de IPs (para usuarios no registrados/guests)
    private final Map<String, LocalDateTime> guestActivity = new ConcurrentHashMap<>();

    // Almacena la última actividad de usuarios registrados (ID -> Timestamp)
    private final Map<Long, LocalDateTime> userActivity = new ConcurrentHashMap<>();

    // Tiempo considerado "online" (ej. 5 minutos)
    private static final int ONLINE_THRESHOLD_MINUTES = 5;

    public void recordGuestActivity(String ip) {
        guestActivity.put(ip, LocalDateTime.now());
        cleanup();
    }

    public void recordUserActivity(Long userId) {
        userActivity.put(userId, LocalDateTime.now());
        cleanup();
    }

    public long getOnlineGuestCount() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(ONLINE_THRESHOLD_MINUTES);
        return guestActivity.values().stream()
                .filter(t -> t.isAfter(threshold))
                .count();
    }

    public long getOnlineUserCount() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(ONLINE_THRESHOLD_MINUTES);
        return userActivity.values().stream()
                .filter(t -> t.isAfter(threshold))
                .count();
    }

    public boolean isUserOnline(Long userId) {
        LocalDateTime lastActive = userActivity.get(userId);
        if (lastActive == null)
            return false;
        return lastActive.isAfter(LocalDateTime.now().minusMinutes(ONLINE_THRESHOLD_MINUTES));
    }

    // Limpieza básica periódica (se ejecuta en cada write por simplicidad,
    // idealmente sería @Scheduled)
    private void cleanup() {
        // En un entorno real, usar un @Scheduled cron job.
        // Aquí por simplicidad no hacemos nada o limpiamos si el mapa crece mucho.
        if (guestActivity.size() > 10000)
            guestActivity.clear();
    }
}
