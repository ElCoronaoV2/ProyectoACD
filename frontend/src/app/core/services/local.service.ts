import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Local, CreateLocalRequest, Menu } from '../models/local.model';

@Injectable({
    providedIn: 'root'
})
export class LocalService {
    private baseUrl = '/api';

    constructor(private http: HttpClient) { }

    // Obtener todos los locales
    getLocales(): Observable<Local[]> {
        return this.http.get<Local[]>(`${this.baseUrl}/locales`);
    }

    // Obtener un local por ID
    getLocal(id: number): Observable<Local> {
        return this.http.get<Local>(`${this.baseUrl}/locales/${id}`);
    }

    // Crear un nuevo local (admin)
    createLocal(request: CreateLocalRequest): Observable<Local> {
        return this.http.post<Local>(`${this.baseUrl}/admin/locales`, request);
    }

    // Actualizar un local (admin)
    updateLocal(id: number, request: CreateLocalRequest): Observable<Local> {
        return this.http.put<Local>(`${this.baseUrl}/admin/locales/${id}`, request);
    }

    // Eliminar un local (admin)
    deleteLocal(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/admin/locales/${id}`);
    }

    // Obtener menús de un local
    getMenusByLocal(localId: number): Observable<Menu[]> {
        return this.http.get<Menu[]>(`${this.baseUrl}/locales/${localId}/menus`);
    }
}
