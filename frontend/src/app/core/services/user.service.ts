import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'https://www.restaurant-tec.es/api/users';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        return new HttpHeaders({
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        });
    }

    updateProfile(userId: number, data: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/profile`, data, { headers: this.getHeaders() });
    }

    getProfile(): Observable<any> {
        // Assuming there is an endpoint /api/users/me or similar, 
        // or we use the ID stored in localStorage if we have it?
        // AuthService stores token, email, name, role. Not ID.
        // But backend /api/users/me is common practice.
        // Or we can rely on what we have in localStorage + what we fetch.
        // Let's assume /api/users/me exists or implement it.
        // Actually, let's use /api/auth/me if strictly Auth related, or /api/users/profile
        // Check AuthController.java?
        // Let's check AuthController first to see if we can get user ID.
        return this.http.get(`${this.apiUrl}/profile`, { headers: this.getHeaders() });
    }
}
