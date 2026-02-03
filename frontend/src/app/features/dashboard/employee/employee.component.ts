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

  activeTab: 'menus' | 'reservas' = 'reservas';
  myLocal: any = null;
  menus: any[] = [];
  reservations: any[] = [];

  newMenu: any = {
    nombre: '',
    tipo: 'COMIDA',
    precio: 0,
    descripcion: ''
  };

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
    this.managementService.getMenus(this.myLocal.id).subscribe({
      next: (res) => this.menus = res
    });
  }

  loadReservations() {
    if (!this.myLocal) return;
    this.reservationService.getLocalReservations(this.myLocal.id).subscribe({
      next: (res) => this.reservations = res
    });
  }

  // Menus CRUD
  createMenu() {
    if (!this.myLocal) return;
    this.managementService.createMenu(this.myLocal.id, this.newMenu).subscribe({
      next: () => {
        alert('Menú creado');
        this.loadMenus();
        this.newMenu = { nombre: '', tipo: 'COMIDA', precio: 0, descripcion: '' };
      },
      error: () => alert('Error al crear menú')
    });
  }

  deleteMenu(id: number) {
    if (confirm('¿Eliminar menú?')) {
      this.managementService.deleteMenu(id).subscribe({
        next: () => this.loadMenus()
      });
    }
  }

  // Reservations Logic
  confirmReservation(id: number) {
    this.reservationService.updateStatus(id, 'CONFIRMADA').subscribe({
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
}
