import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ManagementService {
    private apiUrl = 'https://www.restaurant-tec.es/api/management';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        return new HttpHeaders({
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        });
    }

    // --- CEO ---
    getMyRestaurants(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes`, { headers: this.getHeaders() });
    }

    createRestaurant(restaurantData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes`, restaurantData, { headers: this.getHeaders() });
    }

    createEmployee(localId: number, employeeData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes/${localId}/empleados`, employeeData, { headers: this.getHeaders() });
    }

    getEmployees(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes/${localId}/empleados`, { headers: this.getHeaders() });
    }

    deleteEmployee(employeeId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/empleados/${employeeId}`, { headers: this.getHeaders() });
    }

    updateEmployee(employeeId: number, employeeData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/empleados/${employeeId}`, employeeData, { headers: this.getHeaders() });
    }

    updateRestaurant(restaurantId: number, restaurantData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/restaurantes/${restaurantId}`, restaurantData, { headers: this.getHeaders() });
    }

    deleteRestaurant(restaurantId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/restaurantes/${restaurantId}`, { headers: this.getHeaders() });
    }

    getRestaurant(restaurantId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/restaurantes/${restaurantId}`, { headers: this.getHeaders() });
    }



    // --- MENUS ---
    createMenu(localId: number, menuData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes/${localId}/menus`, menuData, { headers: this.getHeaders() });
    }

    deleteMenu(menuId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/menus/${menuId}`, { headers: this.getHeaders() });
    }

    updateMenu(menuId: number, menuData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/menus/${menuId}`, menuData, { headers: this.getHeaders() });
    }

    getMenus(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes/${localId}/menus`, { headers: this.getHeaders() });
    }


    // --- STAFF ---
    getMyWorkRestaurant(): Observable<any> {
        return this.http.get<any>(`https://www.restaurant-tec.es/api/staff/my-restaurant`, { headers: this.getHeaders() });
    }
}
