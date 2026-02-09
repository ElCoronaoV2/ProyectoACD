import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private apiUrl = 'https://www.restaurant-tec.es/api/admin';

    constructor(private http: HttpClient, private authService: AuthService) { }

    private getHeaders(): HttpHeaders {
        return new HttpHeaders({
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        });
    }

    getCeos(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/ceos`, { headers: this.getHeaders() });
    }

    createCeo(ceoData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/ceos`, ceoData, { headers: this.getHeaders() });
    }

    getAllUsers(role?: string): Observable<any[]> {
        let url = `${this.apiUrl}/users`;
        if (role) {
            url += `?role=${role}`;
        }
        return this.http.get<any[]>(url, { headers: this.getHeaders() });
    }

    createUser(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/users`, userData, { headers: this.getHeaders() });
    }

    getDashboardStats(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/stats`, { headers: this.getHeaders() });
    }

    updateUser(id: number, userData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/users/${id}`, userData, { headers: this.getHeaders() });
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/users/${id}`, { headers: this.getHeaders() });
    }
}
