package com.restaurant.tec.dto;

import java.util.Map;

public class DashboardStatsDTO {
    private long totalUsers;
    private long usersOnline;
    private long guestsOnline;
    private long unverifiedUsers;
    private Map<String, Long> usersByRole;

    public DashboardStatsDTO(long totalUsers, long usersOnline, long guestsOnline, long unverifiedUsers,
            Map<String, Long> usersByRole) {
        this.totalUsers = totalUsers;
        this.usersOnline = usersOnline;
        this.guestsOnline = guestsOnline;
        this.unverifiedUsers = unverifiedUsers;
        this.usersByRole = usersByRole;
    }

    // Getters
    public long getTotalUsers() {
        return totalUsers;
    }

    public long getUsersOnline() {
        return usersOnline;
    }

    public long getGuestsOnline() {
        return guestsOnline;
    }

    public long getUnverifiedUsers() {
        return unverifiedUsers;
    }

    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }
}
