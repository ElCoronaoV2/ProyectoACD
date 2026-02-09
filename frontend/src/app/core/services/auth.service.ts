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
                    if (response.rol) localStorage.setItem('user_role', response.rol);
                    if (response.alergenos) localStorage.setItem('user_alergenos', response.alergenos);
                    if (response.id) localStorage.setItem('user_id', response.id.toString());
                }
            })
        );
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user_email');
        localStorage.removeItem('user_name');
        localStorage.removeItem('user_role');
        localStorage.removeItem('user_alergenos');
        localStorage.removeItem('user_id');
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    getUserRole(): string | null {
        return localStorage.getItem('user_role');
    }

    // Obtiene los datos del usuario actual desde localStorage
    getCurrentUser(): { id?: number; email?: string; nombre?: string; alergenos?: string; rol?: string } | null {
        if (!this.isLoggedIn()) return null;
        return {
            id: localStorage.getItem('user_id') ? parseInt(localStorage.getItem('user_id')!) : undefined,
            email: localStorage.getItem('user_email') || undefined,
            nombre: localStorage.getItem('user_name') || undefined,
            alergenos: localStorage.getItem('user_alergenos') || undefined,
            rol: localStorage.getItem('user_role') || undefined
        };
    }

    isDirector(): boolean { return this.getUserRole() === 'DIRECTOR' || this.getUserRole() === 'ROLE_DIRECTOR'; }
    isCEO(): boolean { return this.getUserRole() === 'CEO' || this.getUserRole() === 'ROLE_CEO'; }
    isEmployee(): boolean { return this.getUserRole() === 'EMPLEADO' || this.getUserRole() === 'ROLE_EMPLEADO'; }
    isClient(): boolean { return this.getUserRole() === 'USER' || this.getUserRole() === 'ROLE_CLIENTE' || !this.getUserRole(); }

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
