import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('token');
    console.log('AuthInterceptor - URL:', req.url);
    console.log('AuthInterceptor - Token found:', !!token);

    if (token) {
        console.log('AuthInterceptor - Attaching token to request');
        const cloned = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
        return next(cloned);
    }

    return next(req);
};
