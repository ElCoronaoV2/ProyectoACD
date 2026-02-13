import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ManagementService {
    private readonly apiUrl = `${environment.apiUrl}/management`;
    private readonly adminUrl = `${environment.apiUrl}/admin`;
    private readonly staffUrl = `${environment.apiUrl}/staff`;

    constructor(private http: HttpClient) { }

    // --- Restaurants ---
    getMyRestaurants(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes`);
    }

    createRestaurant(restaurantData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes`, restaurantData);
    }

    getRestaurant(restaurantId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/restaurantes/${restaurantId}`);
    }

    updateRestaurant(restaurantId: number, restaurantData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/restaurantes/${restaurantId}`, restaurantData);
    }

    deleteRestaurant(restaurantId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/restaurantes/${restaurantId}`);
    }



    // --- Employees ---
    createEmployee(localId: number, employeeData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes/${localId}/empleados`, employeeData);
    }

    getEmployees(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes/${localId}/empleados`);
    }

    updateEmployee(employeeId: number, employeeData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/empleados/${employeeId}`, employeeData);
    }

    deleteEmployee(employeeId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/empleados/${employeeId}`);
    }

    // --- Menus ---
    createMenu(localId: number, menuData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/restaurantes/${localId}/menus`, menuData);
    }

    getMenus(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/restaurantes/${localId}/menus`);
    }

    updateMenu(menuId: number, menuData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/menus/${menuId}`, menuData);
    }

    deleteMenu(menuId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/menus/${menuId}`);
    }

    getGeneralMenus(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/menus/general`);
    }

    assignMenuToLocal(menuId: number, localId: number): Observable<any> {
        return this.http.put(`${this.apiUrl}/menus/${menuId}/assign/${localId}`, {});
    }

    // --- Menu Scheduling ---
    scheduleMenu(localId: number, menuId: number, fecha: string): Observable<any> {
        return this.http.post(`${this.adminUrl}/locales/${localId}/schedule`, { menuId, fecha });
    }

    getMenuSchedule(localId: number, year?: number, month?: number): Observable<any[]> {
        let url = `${this.adminUrl}/locales/${localId}/schedule`;
        if (year && month) {
            url += `?year=${year}&month=${month}`;
        }
        return this.http.get<any[]>(url);
    }

    deleteMenuSchedule(localId: number, fecha: string): Observable<any> {
        return this.http.delete(`${this.adminUrl}/locales/${localId}/schedule/${fecha}`);
    }

    // --- Staff ---
    getMyWorkRestaurant(): Observable<any> {
        return this.http.get<any>(`${this.staffUrl}/my-restaurant`);
    }

    getPublicMenus(localId: number): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/locales/${localId}/menus`);
    }
}

