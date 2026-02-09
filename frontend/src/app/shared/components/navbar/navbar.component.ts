import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="bg-gradient-to-r from-primary-600 to-primary-500 shadow-lg">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <a routerLink="/" class="flex items-center space-x-2 text-white hover:text-secondary-100 transition-colors">
            <span class="text-2xl">🍽️</span>
            <span class="font-bold text-xl tracking-wide">Restaurant-Tec</span>
          </a>

          <!-- Navigation Links -->
          <div class="hidden md:flex items-center space-x-6">
            <!-- Menú Público -->
            <a routerLink="/restaurants" class="text-white hover:text-secondary-200 font-medium transition-colors">
              Restaurantes
            </a>
            
            <!-- Botones Auth (Si NO está logueado) -->
            <div *ngIf="!authService.isLoggedIn()" class="flex items-center space-x-4 ml-4">
              <a routerLink="/login" class="text-white hover:text-secondary-100 font-medium transition-colors">
                Iniciar Sesión
              </a>
              <a routerLink="/register" class="bg-secondary-500 hover:bg-secondary-600 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all hover:shadow-lg transform hover:-translate-y-0.5">
                Registrarse
              </a>
            </div>

            <!-- Menú Usuario (Si ESTÁ logueado) -->
            <div *ngIf="authService.isLoggedIn()" class="flex items-center space-x-4 ml-4">
              
              <!-- Dashboard Link (Dynamic) - Solo para empleados, directores y CEO -->
              <a *ngIf="!authService.isClient()" [routerLink]="getDashboardRoute()" class="text-white hover:text-secondary-100 font-medium transition-colors">
                Panel de Control
              </a>

              <a routerLink="/profile" class="text-white hover:text-secondary-100 font-medium transition-colors">
                Mi Perfil
              </a>

              <button (click)="logout()" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all">
                Cerrar Sesión
              </button>
            </div>
          </div>

          <!-- Mobile Menu (Simplificado) -->
          <div class="md:hidden flex items-center">
            <button class="text-white">☰</button>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) { }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  getDashboardRoute(): string {
    if (this.authService.isDirector()) return '/dashboard/director';
    if (this.authService.isCEO()) return '/dashboard/ceo';
    if (this.authService.isEmployee()) return '/dashboard/employee';
    return '/home'; // Cliente
  }
}
