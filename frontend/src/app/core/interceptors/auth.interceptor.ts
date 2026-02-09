import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('token');
    const router = inject(Router);
    const notificationService = inject(NotificationService);

    let authReq = req;
    if (token) {
        authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                // Token expired or invalid
                localStorage.removeItem('token');
                localStorage.removeItem('user'); // Clean up user data if exists

                // Avoid infinite loop if already on login page
                if (!router.url.includes('/login')) {
                    notificationService.showWarning('Tu sesión ha expirado. Por favor, inicia sesión de nuevo.');
                    router.navigate(['/login']);
                }
            }
            return throwError(() => error);
        })
    );
};
