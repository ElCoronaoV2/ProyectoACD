import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SessionExpiredService } from '../services/session-expired.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('token');
    const router = inject(Router);
    const sessionExpiredService = inject(SessionExpiredService);

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
            if (error.status === 401 && token) {
                // Token expired or invalid - only show message if user was logged in
                localStorage.removeItem('token');
                localStorage.removeItem('user'); // Clean up user data if exists

                // Avoid infinite loop if already on login page
                if (!router.url.includes('/login')) {
                    sessionExpiredService.show('Tu sesión ha expirado. Por favor, inicia sesión de nuevo.');
                }
            }
            return throwError(() => error);
        })
    );
};
