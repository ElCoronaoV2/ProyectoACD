import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ReservationService {
    private apiUrl = 'https://www.restaurant-tec.es/api/reservas';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        return new HttpHeaders({
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        });
    }

    createReservation(data: any): Observable<any> {
        return this.http.post(this.apiUrl, data, { headers: this.getHeaders() });
    }

    getMyReservations(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/mis-reservas`, { headers: this.getHeaders() });
    }

    getLocalReservations(localId: number, start?: string, end?: string): Observable<any[]> {
        let url = `${this.apiUrl}/local/${localId}`;
        if (start && end) {
            url += `?start=${start}&end=${end}`;
        }
        return this.http.get<any[]>(url, { headers: this.getHeaders() });
    }

    updateStatus(id: number, estado: string): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}/estado`, { estado }, { headers: this.getHeaders() });
    }

    checkAvailability(localId: number, fechaHora: string): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/availability?localId=${localId}&fechaHora=${fechaHora}`);
    }
}
