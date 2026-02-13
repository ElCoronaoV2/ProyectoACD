import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ReservationService {
    private readonly apiUrl = `${environment.apiUrl}/reservas`;

    constructor(private http: HttpClient) { }

    createReservation(data: any): Observable<any> {
        return this.http.post(this.apiUrl, data);
    }

    createGuestReservation(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/guest`, data);
    }

    getMyReservations(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/mis-reservas`);
    }

    getLocalReservations(localId: number, start?: string, end?: string): Observable<any[]> {
        let url = `${this.apiUrl}/local/${localId}`;
        if (start && end) {
            url += `?start=${start}&end=${end}`;
        }
        return this.http.get<any[]>(url);
    }

    updateStatus(id: number, estado: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}/estado`, { estado });
    }

    checkAvailability(localId: number, fechaHora: string): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/availability?localId=${localId}&fechaHora=${fechaHora}`);
    }
}
