package com.restaurant.tec.config;

import com.restaurant.tec.service.OnlineStatusService;
import com.restaurant.tec.repository.UserRepository;
import com.restaurant.tec.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ActivityInterceptor implements HandlerInterceptor {

    @Autowired
    private OnlineStatusService onlineStatusService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = request.getRemoteAddr();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // Authenticated User
            String email = auth.getName();
            userRepository.findByEmail(email).ifPresent(user -> {
                onlineStatusService.recordUserActivity(user.getId());
                // Update DB timestamp occasionally? Done in PreUpdate/PrePersist but that
                // requires save.
                // For now, Service memory map is enough for "Current Online" view.
            });
        } else {
            // Guest
            onlineStatusService.recordGuestActivity(ip);
        }

        return true;
    }
}
