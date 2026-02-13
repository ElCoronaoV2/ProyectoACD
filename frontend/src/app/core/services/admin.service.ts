import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private readonly apiUrl = `${environment.apiUrl}/admin`;

    constructor(private http: HttpClient) { }

    getCeos(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/ceos`);
    }

    createCeo(ceoData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/ceos`, ceoData);
    }

    getAllUsers(role?: string): Observable<any[]> {
        const url = role ? `${this.apiUrl}/users?role=${role}` : `${this.apiUrl}/users`;
        return this.http.get<any[]>(url);
    }

    createUser(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/users`, userData);
    }

    getDashboardStats(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/stats`);
    }

    updateUser(id: number, userData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/users/${id}`, userData);
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
    }
}
