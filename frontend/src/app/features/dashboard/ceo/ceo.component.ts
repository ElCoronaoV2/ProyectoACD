import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { ManagementService } from '../../../core/services/management.service';

@Component({
  selector: 'app-ceo',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  newMenu: any = {
    nombre: '',
    tipo: 'COMIDA', // COMIDA, CENA, DESAYUNO
    precio: 0,
    descripcion: ''
  };

  constructor(private managementService: ManagementService) { }

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
        alert('Restaurante creado con éxito');
        this.activeTab = 'restaurantes';
        this.loadRestaurants();
        this.newLocal = { nombre: '', direccion: '', capacidad: 50, latitud: null, longitud: null };
        if (this.marker) this.marker.remove();
        this.marker = undefined;
      },
      error: (err: any) => alert('Error al crear restaurante')
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
      error: (err) => alert('Error al eliminar restaurante')
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
      alert("Email y Contraseña son obligatorios");
      return;
    }

    this.managementService.createEmployee(this.selectedLocal.id, this.newEmployee).subscribe({
      next: (res) => {
        alert('Empleado creado correctamente');
        this.loadEmployees(); // Reload list
        this.newEmployee = { nombre: '', email: '', telefono: '', password: '', alergenos: '' }; // Reset form
      },
      error: (err) => {
        console.error(err);
        alert(err.error || 'Error al crear empleado');
      }
    });
  }

  deleteEmployee(id: number) {
    if (!confirm('¿Eliminar empleado?')) return;
    this.managementService.deleteEmployee(id).subscribe({
      next: () => this.loadEmployees(),
      error: (err) => alert('Error al eliminar empleado')
    });
  }

  loadMenus() {
    if (!this.selectedLocal) return;
    this.managementService.getMenus(this.selectedLocal.id).subscribe({
      next: (res) => this.menus = res,
      error: (err) => console.error(err)
    });
  }

  createMenu() {
    if (!this.selectedLocal) return;
    if (!this.newMenu.nombre) {
      alert("Nombre del menú obligatorio");
      return;
    }
    this.managementService.createMenu(this.selectedLocal.id, this.newMenu).subscribe({
      next: (res) => {
        alert('Menú creado correctamente');
        this.loadMenus();
        this.newMenu = { nombre: '', tipo: 'COMIDA', precio: 0, descripcion: '' };
      },
      error: (err) => alert('Error al crear menú')
    });
  }

  deleteMenu(id: number) {
    if (!confirm('¿Eliminar menú?')) return;
    this.managementService.deleteMenu(id).subscribe({
      next: () => this.loadMenus(),
      error: (err) => alert('Error al eliminar menú')
    });
  }
}
