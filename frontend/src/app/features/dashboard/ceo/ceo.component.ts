import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { ManagementService } from '../../../core/services/management.service';
import { MenuFormComponent } from '../../admin/menu/menu-form/menu-form.component';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-ceo',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuFormComponent],
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
  activeManagementTab: 'info' | 'employees' | 'menus' = 'info';
  employees: any[] = [];
  menus: any[] = [];

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
  selectedGeneralMenuId: number | null = null;
  targetLocalId: number | null = null;

  constructor(
    private managementService: ManagementService,
    private notificationService: NotificationService
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
    if (!confirm('¿Seguro que quieres eliminar este restaurante?')) return;
    this.managementService.deleteRestaurant(id).subscribe({
      next: () => {
        this.loadRestaurants();
        if (this.selectedLocal?.id === id) {
          this.selectedLocal = null;
          this.activeTab = 'restaurantes';
        }
      },
      error: (err) => this.notificationService.showError('Error al eliminar restaurante')
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

  assignMenu() {
    if (!this.selectedGeneralMenuId || !this.targetLocalId) return;

    this.managementService.assignMenuToLocal(this.selectedGeneralMenuId, this.targetLocalId).subscribe({
      next: () => {
        this.notificationService.showSuccess('Menú asignado correctamente');
        this.loadGeneralMenus();
        this.selectedGeneralMenuId = null;
        this.targetLocalId = null;
      },
      error: (err) => this.notificationService.showError('Error al asignar menú')
    });
  }

  // --- SUB TABS MANAGEMENT ---
  setManagementTab(tab: 'info' | 'employees' | 'menus') {
    this.activeManagementTab = tab;
    if (tab === 'employees') this.loadEmployees();
    if (tab === 'menus') this.loadMenus();
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
      error: (err) => this.notificationService.showError(err.error?.error || 'Error al programar menú')
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
}

