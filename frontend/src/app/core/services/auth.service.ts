import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'https://www.restaurant-tec.es/api/auth';
    // Use http://localhost:8080/api/auth for local dev if needed, 
    // but since we deploy to domain, let's try relative or configured URL.
    // Actually, since it's served from same domain via Nginx proxy, 
    // we can just use /api/auth

    constructor(private http: HttpClient) { }

    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userData);
    }

    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
            tap((response: any) => {
                if (response.token) {
                    localStorage.setItem('token', response.token);
                    if (response.email) localStorage.setItem('user_email', response.email);
                    if (response.nombre) localStorage.setItem('user_name', response.nombre);
                }
            })
        );
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user_email');
        localStorage.removeItem('user_name');
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    verifyEmail(token: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/verify?token=${token}`);
    }

    forgotPassword(email: string): Observable<any> {
        // Note: Backend expects param 'email'
        return this.http.post(`${this.apiUrl}/forgot-password?email=${email}`, {});
    }

    resetPassword(token: string, newPassword: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/reset-password?token=${token}`, newPassword);
    }
}
