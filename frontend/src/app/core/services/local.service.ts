import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Local, CreateLocalRequest, Menu } from '../models/local.model';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class LocalService {
    private readonly apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) { }

    getLocales(): Observable<Local[]> {
        return this.http.get<Local[]>(`${this.apiUrl}/locales`);
    }

    getLocal(id: number): Observable<Local> {
        return this.http.get<Local>(`${this.apiUrl}/locales/${id}`);
    }

    createLocal(request: CreateLocalRequest): Observable<Local> {
        return this.http.post<Local>(`${this.apiUrl}/admin/locales`, request);
    }

    updateLocal(id: number, request: CreateLocalRequest): Observable<Local> {
        return this.http.put<Local>(`${this.apiUrl}/admin/locales/${id}`, request);
    }

    deleteLocal(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/admin/locales/${id}`);
    }

    getMenusByLocal(localId: number): Observable<Menu[]> {
        return this.http.get<Menu[]>(`${this.apiUrl}/locales/${localId}/menus`);
    }

    getReviews(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/locales/${localId}/reviews`);
    }
}
