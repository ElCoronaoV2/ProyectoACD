import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { LocalService } from '../../../core/services/local.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit, OnDestroy {
  stats: any = {
    totalUsers: 0,
    usersOnline: 0,
    guestsOnline: 0,
    unverifiedUsers: 0,
    usersByRole: {}
  };

  users: any[] = [];
  filteredUsers: any[] = [];
  locales: any[] = []; // Stores list of restaurants
  selectedRole: string = '';
  loading = false;
  private usersSubscription: Subscription | null = null;

  // Modal State
  showCreateModal = false;
  newUser: any = {
    nombre: '',
    email: '',
    password: '',
    telefono: '',
    rol: 'USER',
    restauranteId: null,      // For EMPLEADO
    restauranteIds: []        // For CEO
  };

  private statsSubscription: Subscription | null = null;
  private statsInterval: any;
  loadingStats = false;

  constructor(
    private adminService: AdminService,
    private localService: LocalService
  ) { }

  ngOnInit() {
    this.loadStats();
    this.loadUsers();
    this.loadLocales();

    // Actualizar estadísticas cada 3 segundos (Optimizado)
    // Se guarda en una variable para limpiar al salir
    this.statsInterval = setInterval(() => {
      if (!this.loadingStats) {
        this.loadStats();
      }
    }, 3000);
  }

  ngOnDestroy() {
    if (this.usersSubscription) {
      this.usersSubscription.unsubscribe();
    }
    if (this.statsSubscription) {
      this.statsSubscription.unsubscribe();
    }
    if (this.statsInterval) {
      clearInterval(this.statsInterval);
    }
  }

  loadStats() {
    this.loadingStats = true;
    this.statsSubscription = this.adminService.getDashboardStats().subscribe({
      next: (data) => {
        // Solo actualizamos si los datos han cambiado para evitar reflows innecesarios (Opcional, pero Angular lo maneja bien)
        this.stats = data;
        this.loadingStats = false;
      },
      error: () => {
        this.loadingStats = false;
      }
    });
  }

  loadUsers() {
    if (this.usersSubscription) {
      this.usersSubscription.unsubscribe();
    }

    this.loading = true;
    console.log('Loading users with role:', this.selectedRole || 'ALL');

    this.usersSubscription = this.adminService.getAllUsers(this.selectedRole || undefined).subscribe({
      next: (data) => {
        console.log('Users received:', data.length);
        this.users = data;
        this.filteredUsers = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.loading = false;
      }
    });
  }

  loadLocales() {
    this.localService.getLocales().subscribe({
      next: (data) => {
        this.locales = data;
        console.log('Locales loaded:', this.locales.length);
      },
      error: (err) => console.error('Error loading locales:', err)
    });
  }

  filterByRole(role: string) {
    this.selectedRole = role;
    this.loadUsers();
  }

  openCreateModal() {
    this.showCreateModal = true;
    this.newUser = { nombre: '', email: '', password: '', telefono: '', rol: 'USER' };
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  createUser() {
    this.adminService.createUser(this.newUser).subscribe({
      next: () => {
        alert('Usuario creado correctamente');
        this.closeCreateModal();
        this.loadUsers(); // Refresh list
        this.loadStats(); // Refresh stats
      },
      error: () => alert('Error al crear usuario. El email puede estar duplicado.')
    });
  }

  deleteUser(id: number) {
    if (confirm('¿Estás seguro de eliminar este usuario?')) {
      this.adminService.deleteUser(id).subscribe(() => {
        this.loadUsers();
        this.loadStats();
      });
    }
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'DIRECTOR': return 'badge-director';
      case 'CEO': return 'badge-ceo';
      case 'EMPLEADO': return 'badge-employee';
      default: return 'badge-user';
    }
  }
}
