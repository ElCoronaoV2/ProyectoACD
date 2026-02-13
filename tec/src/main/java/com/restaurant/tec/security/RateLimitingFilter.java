package com.restaurant.tec.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiter para endpoints públicos sensibles
 * - /api/auth/register: 5 intentos por IP cada 15 minutos
 * - /api/auth/login: 10 intentos por IP cada 15 minutos
 * - /api/reservas/guest: 3 intentos por IP cada 5 minutos
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final long TIME_WINDOW_MILLIS = 15 * 60 * 1000; // 15 minutos
    private static final long RESERVATION_TIME_WINDOW_MILLIS = 5 * 60 * 1000; // 5 minutos
    private static final int REGISTER_LIMIT = 5;
    private static final int LOGIN_LIMIT = 10;
    private static final int RESERVATION_LIMIT = 3;

    // Estructura: ip -> {endpoint -> {count, timestamp}}
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, RequestCounter>> requestCounts 
            = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Solo aplicar rate limiting a endpoints públicos sensibles
        if (isRateLimitedEndpoint(requestPath, method)) {
            if (!isAllowed(clientIp, requestPath)) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Demasiados intentos. Intenta más tarde.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedEndpoint(String path, String method) {
        return (path.startsWith("/api/auth/register") && "POST".equals(method)) ||
               (path.startsWith("/api/auth/login") && "POST".equals(method)) ||
               (path.startsWith("/api/auth/forgot-password") && "POST".equals(method)) ||
               (path.startsWith("/api/reservas/guest") && "POST".equals(method));
    }

    private boolean isAllowed(String ip, String endpoint) {
        ConcurrentHashMap<String, RequestCounter> ipRequests = 
                requestCounts.computeIfAbsent(ip, k -> new ConcurrentHashMap<>());

        RequestCounter counter = ipRequests.computeIfAbsent(endpoint, 
                k -> new RequestCounter(getTimeWindow(endpoint)));

        long now = System.currentTimeMillis();
        
        // Resetear si pasó la ventana de tiempo
        if (now - counter.firstRequestTime > counter.timeWindow) {
            counter.count.set(0);
            counter.firstRequestTime = now;
        }

        int limit = getLimit(endpoint);
        int currentCount = counter.count.incrementAndGet();

        return currentCount <= limit;
    }

    private int getLimit(String endpoint) {
        if (endpoint.contains("/auth/register")) return REGISTER_LIMIT;
        if (endpoint.contains("/auth/login")) return LOGIN_LIMIT;
        if (endpoint.contains("/auth/forgot-password")) return REGISTER_LIMIT;
        if (endpoint.contains("/reservas/guest")) return RESERVATION_LIMIT;
        return 10; // default
    }

    private long getTimeWindow(String endpoint) {
        if (endpoint.contains("/reservas/guest")) return RESERVATION_TIME_WINDOW_MILLIS;
        return TIME_WINDOW_MILLIS;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private static class RequestCounter {
        final AtomicInteger count = new AtomicInteger(0);
        long firstRequestTime = System.currentTimeMillis();
        final long timeWindow;

        RequestCounter(long timeWindow) {
            this.timeWindow = timeWindow;
        }
    }
}
