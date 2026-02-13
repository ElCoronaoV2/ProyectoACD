import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagementService } from '../../../core/services/management.service';
import { ReservationService } from '../../../core/services/reservation.service';

@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee.component.html',
  styleUrl: './employee.component.css'
})
export class EmployeeComponent implements OnInit {

  activeTab: 'reservas' | 'menus' = 'reservas';
  activeReservationTab: 'hoy' | 'proximas' | 'historial' = 'hoy';

  myLocal: any = null;
  menus: any[] = []; // Will store only today's menu(s)

  // Reservation lists
  allReservations: any[] = [];
  todayReservations: any[] = [];
  upcomingReservations: any[] = [];
  historyReservations: any[] = [];

  constructor(
    private managementService: ManagementService,
    private reservationService: ReservationService
  ) { }

  ngOnInit() {
    this.loadMyLocal();
  }

  loadMyLocal() {
    this.managementService.getMyWorkRestaurant().subscribe({
      next: (local) => {
        this.myLocal = local;
        this.loadMenus();
        this.loadReservations();
      },
      error: (err) => console.error("No tienes restaurante asignado", err)
    });
  }

  loadMenus() {
    if (!this.myLocal) return;
    // Fetch ONLY public menus (which returns scheduled menu for today)
    this.managementService.getPublicMenus(this.myLocal.id).subscribe({
      next: (res) => this.menus = res
    });
  }

  loadReservations() {
    if (!this.myLocal) return;
    this.reservationService.getLocalReservations(this.myLocal.id).subscribe({
      next: (res) => {
        this.allReservations = res;
        this.filterReservations();
      }
    });
  }

  filterReservations() {
    const now = new Date();
    const todayStr = now.toISOString().split('T')[0];

    this.todayReservations = [];
    this.upcomingReservations = [];
    this.historyReservations = [];

    this.allReservations.forEach(res => {
      const resDate = new Date(res.fechaHora);
      const resDateStr = res.fechaHora.split('T')[0];

      // Logic:
      // History: 
      //  - Status is COMPLETED, CANCELLED, ASISTIDO, NO_ASISTIDO
      //  - OR Date is in the past (yesterday or before) - regardless of status? 
      //    User said: "al pasar de dia, se van a esa subclase...". 
      //    So if date < today, it is history.

      if (resDateStr < todayStr) {
        this.historyReservations.push(res);
      } else if (resDateStr === todayStr) {
        // Today
        // If status is final (ASISTIDO/NO_ASISTIDO/CANCELADA), maybe move to history? 
        // User said: "se marca... y pasa a historial".
        // So if status is ASISTIDO or NO_ASISTIDO, it goes to history?
        // "si ha venido... se pasa a la subclase de historial" -> YES.

        if (['ASISTIDO', 'NO_ASISTIDO', 'CANCELADA', 'COMPLETADA'].includes(res.estado)) {
          this.historyReservations.push(res);
        } else {
          this.todayReservations.push(res);
        }
      } else {
        // Future (Tomorrow onwards)
        this.upcomingReservations.push(res);
      }
    });

    // Sort
    this.todayReservations.sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
    this.upcomingReservations.sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
    this.historyReservations.sort((a, b) => new Date(b.fechaHora).getTime() - new Date(a.fechaHora).getTime()); // Newest first
  }

  markAttendance(resId: number, asisitio: boolean) {
    const status = asisitio ? 'ASISTIDO' : 'NO_ASISTIDO';
    this.reservationService.updateStatus(resId, status).subscribe({
      next: () => this.loadReservations()
    });
  }

  cancelReservation(id: number) {
    if (confirm('¿Cancelar esta reserva?')) {
      this.reservationService.updateStatus(id, 'CANCELADA').subscribe({
        next: () => this.loadReservations()
      });
    }
  }

  getShiftLabel(fechaHora: string): string {
    const hour = new Date(fechaHora).getHours();
    return hour < 17 ? '☀️ Comida' : '🌙 Cena';
  }

  getReservationName(res: any): string {
    if (res.nombreUsuario) return res.nombreUsuario;
    if (res.nombreInvitado) return res.nombreInvitado + ' (Invitado)';
    return 'Sin nombre';
  }
}
