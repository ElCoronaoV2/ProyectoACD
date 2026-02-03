import { Injectable, inject } from '@angular/core';
import { Router, CanActivateFn, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn()) {
        const requiredRoles = route.data['roles'] as Array<string>;

        if (requiredRoles && requiredRoles.length > 0) {
            const userRole = authService.getUserRole();
            // Check if user has one of the required roles
            // Handle prefix differences (ROLE_CEO vs CEO)
            const hasRole = requiredRoles.some(role =>
                userRole === role || userRole === `ROLE_${role}`
            );

            if (hasRole) {
                return true;
            } else {
                // Role not authorized, redirect to their dashboard or home
                return router.createUrlTree(['/']);
            }
        }
        return true;
    }

    // Not logged in
    return router.createUrlTree(['/login']);
};
