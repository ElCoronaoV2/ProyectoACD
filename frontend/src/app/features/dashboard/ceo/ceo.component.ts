import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { ManagementService } from '../../../core/services/management.service';
import { MenuFormComponent } from '../../admin/menu/menu-form/menu-form.component';
import { ReviewsModalComponent } from '../../../shared/components/reviews-modal/reviews-modal.component';
import { NotificationService } from '../../../core/services/notification.service';
import { ReservationService } from '../../../core/services/reservation.service';

@Component({
  selector: 'app-ceo',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuFormComponent, ReviewsModalComponent],
  templateUrl: './ceo.component.html',
  styleUrls: ['./ceo.component.css']
})
export class CeoComponent implements OnInit, AfterViewInit {
  @ViewChild('mapContainer') mapElement!: ElementRef;

  private map: L.Map | undefined;
  private marker: L.Marker | undefined;

  activeTab: string = 'restaurantes';
  restaurantes: any[] = [];
  selectedLocal: any = null;
  isEditMode: boolean = false;
  editData: any = {};

  // Review Modal State
  showReviewsModal = false;
  selectedLocalIdForReviews: number | null = null;

  newLocal: any = {
    nombre: '',
    direccion: '',
    capacidad: 50,
    horario: '',
    imagenUrl: '',
    latitud: null,
    longitud: null
  };

  // Gestión Sub-state
  activeManagementTab: 'info' | 'employees' | 'menus' | 'reservas' = 'info';
  employees: any[] = [];
  menus: any[] = [];
  reservations: any[] = [];

  newEmployee: any = {
    nombre: '',
    email: '',
    telefono: '',
    password: '',
    alergenos: ''
  };

  showMenuWizard = false;

  newMenu: any = {
    nombre: '',
    tipo: 'COMIDA', // COMIDA, CENA, DESAYUNO
    precio: 0,
    descripcion: ''
  };

  // Menu Scheduling (Menú del Día)
  scheduleDate: string = '';
  scheduleMenuId: string = '';
  menuSchedule: any[] = [];
  today: string = new Date().toISOString().split('T')[0];

  // General Menus
  generalMenus: any[] = [];
  targetLocals: { [key: number]: number } = {};

  constructor(
    private managementService: ManagementService,
    private notificationService: NotificationService,
    private reservationService: ReservationService
  ) { }

  ngOnInit() {
    this.loadRestaurants();
  }

  ngAfterViewInit() {
    // El mapa solo se inicializa si el tab es 'nuevo', pero como empieza en 'restaurantes',
    // necesitamos observar el cambio de tab.
  }

  // Hook para detectar cambio de tab y cargar mapa
  get isNewTab() { return this.activeTab === 'nuevo'; }

  loadRestaurants() {
    this.managementService.getMyRestaurants().subscribe({
      next: (data: any) => this.restaurantes = data,
      error: (err: any) => console.error(err)
    });
  }



  // Se llama cuando user cambia a tab 'nuevo'
  initMap() {
    if (this.map) return; // Ya existe

    // Esperar a que el elemento exista en DOM (ngIf)
    setTimeout(() => {
      if (!this.mapElement) return;

      this.map = L.map(this.mapElement.nativeElement).setView([38.3452, -0.4810], 13); // Alicante

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: 'OpenStreetMap'
      }).addTo(this.map);

      // Click event
      this.map.on('click', (e: L.LeafletMouseEvent) => {
        const { lat, lng } = e.latlng;
        this.setMarker(lat, lng);
      });
    }, 100);
  }

  setMarker(lat: number, lng: number) {
    this.newLocal.latitud = lat;
    this.newLocal.longitud = lng;

    if (this.marker) {
      this.marker.setLatLng([lat, lng]);
    } else {
      this.marker = L.marker([lat, lng]).addTo(this.map!);
    }
  }

  createRestaurant() {
    if (!this.newLocal.nombre || !this.newLocal.direccion) {
      this.notificationService.showWarning('Nombre y dirección son obligatorios');
      return;
    }
    if (!this.newLocal.latitud || !this.newLocal.longitud) {
      this.notificationService.showWarning('Debes seleccionar una ubicación en el mapa');
      return;
    }
    if (this.newLocal.capacidad <= 0) {
      this.notificationService.showWarning('La capacidad debe ser mayor a 0');
      return;
    }

    this.managementService.createRestaurant(this.newLocal).subscribe({
      next: (res: any) => {
        this.notificationService.showSuccess('Restaurante creado con éxito');
        this.activeTab = 'restaurantes';
        this.loadRestaurants();
        this.newLocal = { nombre: '', direccion: '', capacidad: 50, latitud: null, longitud: null };
        if (this.marker) this.marker.remove();
        this.marker = undefined;
      },
      error: (err: any) => this.notificationService.showError('Error al crear restaurante')
    });
  }

  deleteRestaurant(id: number) {
    const restaurantName = this.selectedLocal?.nombre || 'este restaurante';
    const confirmMessage = `¿Estás seguro de que quieres eliminar "${restaurantName}"?\n\nEsta acción no se puede deshacer y eliminará:\n- Todos los menús asociados\n- Todas las reservas\n- Todos los empleados asignados\n\nEscribe el nombre del restaurante para confirmar.`;

    const userInput = prompt(confirmMessage);

    if (userInput !== restaurantName) {
      if (userInput !== null) {
        this.notificationService.showWarning('El nombre no coincide. Eliminación cancelada.');
      }
      return;
    }

    this.managementService.deleteRestaurant(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Restaurante eliminado correctamente');
        this.loadRestaurants();
        if (this.selectedLocal?.id === id) {
          this.selectedLocal = null;
          this.activeTab = 'restaurantes';
        }
      },
      error: (err) => this.notificationService.showError('Error al eliminar restaurante')
    });
  }

  enableEditMode() {
    this.isEditMode = true;
    // Copy current data to editData
    this.editData = {
      nombre: this.selectedLocal.nombre,
      direccion: this.selectedLocal.direccion,
      capacidad: this.selectedLocal.capacidad,
      horario: this.selectedLocal.horario || '',
      imagenUrl: this.selectedLocal.imagenUrl || '',
      latitud: this.selectedLocal.latitud,
      longitud: this.selectedLocal.longitud,
      aperturaComida: this.selectedLocal.aperturaComida,
      cierreComida: this.selectedLocal.cierreComida,
      aperturaCena: this.selectedLocal.aperturaCena,
      cierreCena: this.selectedLocal.cierreCena
    };
  }

  cancelEdit() {
    this.isEditMode = false;
    this.editData = {};
  }

  updateRestaurant() {
    if (!this.selectedLocal || !this.editData.nombre || !this.editData.direccion) {
      this.notificationService.showWarning('Nombre y dirección son obligatorios');
      return;
    }
    if (this.editData.capacidad !== undefined && this.editData.capacidad <= 0) {
      this.notificationService.showWarning('La capacidad debe ser mayor a 0');
      return;
    }
    if (this.editData.latitud === undefined || this.editData.longitud === undefined) {
      this.notificationService.showWarning('La ubicación es obligatoria');
      return;
    }

    this.managementService.updateRestaurant(this.selectedLocal.id, this.editData).subscribe({
      next: (res) => {
        this.notificationService.showSuccess('Restaurante actualizado correctamente');
        this.isEditMode = false;
        this.loadRestaurants();
        // Update selectedLocal with new data
        this.selectedLocal = { ...this.selectedLocal, ...this.editData };
      },
      error: (err) => {
        console.error(err);
        this.notificationService.showError('Error al actualizar restaurante');
      }
    });
  }


  manageLocal(local: any) {
    this.selectedLocal = local;
    this.activeTab = 'gestion';
    this.activeManagementTab = 'info'; // Reset subtab
    // Load dependants? No, load on tab switch or default
  }

  setTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'nuevo') {
      this.initMap();
    }
    if (tab === 'general') {
      this.loadGeneralMenus();
    }
  }

  loadGeneralMenus() {
    this.managementService.getGeneralMenus().subscribe({
      next: (res) => this.generalMenus = res,
      error: (err) => console.error(err)
    });
  }

  assignMenu(menuId: number) {
    const targetId = this.targetLocals[menuId];
    if (!targetId) {
      this.notificationService.showWarning('Selecciona un restaurante destino');
      return;
    }

    console.log('🔄 Importando menú:', { menuId, targetId });

    this.managementService.assignMenuToLocal(menuId, targetId).subscribe({
      next: (response: any) => {
        console.log('✅ Menú importado:', response);
        const mensaje = response?.message || 'Menú importado correctamente';
        this.notificationService.showSuccess(mensaje);
        // Limpiar selección solo para este menú
        delete this.targetLocals[menuId];
      },
      error: (err) => {
        console.error('❌ Error al importar menú:', err);
        const errorMsg = err.error?.error || err.error?.message || err.error || 'Error al importar menú';
        this.notificationService.showError(errorMsg);
      }
    });
  }

  // --- SUB TABS MANAGEMENT ---
  setManagementTab(tab: 'info' | 'employees' | 'menus' | 'reservas') {
    this.activeManagementTab = tab;
    if (tab === 'employees') this.loadEmployees();
    if (tab === 'menus') this.loadMenus();
    if (tab === 'reservas') this.loadReservations();
  }

  // Reservations Logic
  activeReservationTab: 'hoy' | 'proximas' | 'historial' = 'hoy';
  allReservations: any[] = [];
  todayReservations: any[] = [];
  upcomingReservations: any[] = [];
  historyReservations: any[] = [];

  loadReservations() {
    if (!this.selectedLocal) return;
    this.reservationService.getLocalReservations(this.selectedLocal.id).subscribe({
      next: (res) => {
        this.allReservations = res;
        this.filterReservations();
      },
      error: (err) => console.error('Error loading reservations:', err)
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

      if (resDateStr < todayStr) {
        this.historyReservations.push(res);
      } else if (resDateStr === todayStr) {
        if (['ASISTIDO', 'NO_ASISTIDO', 'CANCELADA', 'COMPLETADA'].includes(res.estado)) {
          this.historyReservations.push(res);
        } else {
          this.todayReservations.push(res);
        }
      } else {
        this.upcomingReservations.push(res);
      }
    });

    // Sort
    this.todayReservations.sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
    this.upcomingReservations.sort((a, b) => new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime());
    this.historyReservations.sort((a, b) => new Date(b.fechaHora).getTime() - new Date(a.fechaHora).getTime());
  }

  markAttendance(resId: number, asisitio: boolean) {
    const status = asisitio ? 'ASISTIDO' : 'NO_ASISTIDO';
    this.reservationService.updateStatus(resId, status).subscribe({
      next: () => {
        this.notificationService.showSuccess('Asistencia confirmada. Reserva movida al historial.');
        this.loadReservations();
      },
      error: () => this.notificationService.showError('Error al actualizar reserva')
    });
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

  loadEmployees() {
    if (!this.selectedLocal) return;
    this.managementService.getEmployees(this.selectedLocal.id).subscribe({
      next: (res) => this.employees = res,
      error: (err) => console.error(err)
    });
  }

  createEmployee() {
    if (!this.selectedLocal) return;
    // Validar datos básicos
    if (!this.newEmployee.email || !this.newEmployee.password) {
      this.notificationService.showWarning("Email y Contraseña son obligatorios");
      return;
    }

    this.managementService.createEmployee(this.selectedLocal.id, this.newEmployee).subscribe({
      next: (res) => {
        this.notificationService.showSuccess('Empleado creado correctamente');
        this.loadEmployees(); // Reload list
        this.newEmployee = { nombre: '', email: '', telefono: '', password: '', alergenos: '' }; // Reset form
      },
      error: (err) => {
        console.error(err);
        this.notificationService.showError(err.error || 'Error al crear empleado');
      }
    });
  }

  deleteEmployee(id: number) {
    if (!confirm('¿Eliminar empleado?')) return;
    this.managementService.deleteEmployee(id).subscribe({
      next: () => this.loadEmployees(),
      error: (err) => this.notificationService.showError('Error al eliminar empleado')
    });
  }

  loadMenus() {
    if (!this.selectedLocal) return;
    this.managementService.getMenus(this.selectedLocal.id).subscribe({
      next: (res) => {
        this.menus = res;
        this.loadMenuSchedule(); // Also load schedule when menus are loaded
      },
      error: (err) => console.error(err)
    });
  }

  createMenu() {
    if (!this.selectedLocal) return;
    if (!this.newMenu.nombre) {
      this.notificationService.showWarning("Nombre del menú obligatorio");
      return;
    }
    if (this.newMenu.precio < 0) {
      this.notificationService.showWarning("El precio no puede ser negativo");
      return;
    }

    this.managementService.createMenu(this.selectedLocal.id, this.newMenu).subscribe({
      next: (res) => {
        this.notificationService.showSuccess('Menú creado correctamente');
        this.loadMenus();
        this.newMenu = { nombre: '', tipo: 'COMIDA', precio: 0, descripcion: '' };
      },
      error: (err) => this.notificationService.showError('Error al crear menú')
    });
  }

  deleteMenu(id: number) {
    if (!confirm('¿Eliminar menú?')) return;
    this.managementService.deleteMenu(id).subscribe({
      next: () => this.loadMenus(),
      error: (err) => this.notificationService.showError('Error al eliminar menú')
    });
  }

  // --- MENU SCHEDULING (Menú del Día) ---
  loadMenuSchedule() {
    if (!this.selectedLocal) return;
    this.managementService.getMenuSchedule(this.selectedLocal.id).subscribe({
      next: (res) => this.menuSchedule = res,
      error: (err) => console.error('Error loading schedule:', err)
    });
  }

  scheduleMenuForDate() {
    if (!this.selectedLocal || !this.scheduleDate || !this.scheduleMenuId) return;

    this.managementService.scheduleMenu(
      this.selectedLocal.id,
      parseInt(this.scheduleMenuId),
      this.scheduleDate
    ).subscribe({
      next: (res) => {
        this.notificationService.showSuccess('Menú programado correctamente');
        this.loadMenuSchedule();
        this.scheduleDate = '';
        this.scheduleMenuId = '';
      },
      error: (err) => {
        if (err.status === 400) {
          this.notificationService.showError('Ya existe un menú programado para esta fecha. Elimínalo primero si deseas cambiarlo.');
        } else {
          this.notificationService.showError(err.error?.error || 'Error al programar menú');
        }
      }
    });
  }

  deleteSchedule(fecha: string) {
    if (!this.selectedLocal) return;
    if (!confirm('¿Eliminar programación?')) return;

    this.managementService.deleteMenuSchedule(this.selectedLocal.id, fecha).subscribe({
      next: () => {
        this.notificationService.showSuccess('Programación eliminada');
        this.loadMenuSchedule();
      },
      error: (err) => this.notificationService.showError('Error al eliminar programación')
    });
  }

  openReviewsModal(localId: number, event?: Event) {
    if (event) event.stopPropagation();
    this.selectedLocalIdForReviews = localId;
    this.showReviewsModal = true;
  }
}

