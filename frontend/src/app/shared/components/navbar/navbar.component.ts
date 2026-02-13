import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="bg-gray-900 shadow-lg border-b border-gray-800">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <a routerLink="/" class="flex items-center space-x-2 text-white hover:text-amber-400 transition-colors">
            <span class="text-2xl">🍽️</span>
            <span class="font-bold text-xl tracking-wide">Restaurant-Tec</span>
          </a>

          <!-- Navigation Links -->
          <div class="hidden md:flex items-center space-x-6">
            <!-- Menú Público -->
            <a routerLink="/restaurants" class="text-gray-300 hover:text-amber-400 font-medium transition-colors">
              Restaurantes
            </a>
            
            <!-- Botones Auth (Si NO está logueado) -->
            <div *ngIf="!authService.isLoggedIn()" class="flex items-center space-x-4 ml-4">
              <a routerLink="/login" class="text-gray-300 hover:text-amber-400 font-medium transition-colors">
                Iniciar Sesión
              </a>
              <a routerLink="/register" class="bg-amber-600 hover:bg-amber-700 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all hover:shadow-lg transform hover:-translate-y-0.5">
                Registrarse
              </a>
            </div>

            <!-- Menú Usuario (Si ESTÁ logueado) -->
            <div *ngIf="authService.isLoggedIn()" class="flex items-center space-x-4 ml-4">
              
              <!-- Dashboard Link (Dynamic) - Solo para empleados, directores y CEO -->
              <a *ngIf="!authService.isClient()" [routerLink]="getDashboardRoute()" class="text-gray-300 hover:text-amber-400 font-medium transition-colors">
                Panel de Control
              </a>

              <a routerLink="/profile" class="text-gray-300 hover:text-amber-400 font-medium transition-colors">
                Mi Perfil
              </a>

              <button (click)="logout()" class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all">
                Cerrar Sesión
              </button>
            </div>
          </div>

          <!-- Mobile Menu Button -->
          <div class="md:hidden flex items-center">
            <button (click)="toggleMobileMenu()" class="text-white text-2xl p-2 hover:text-amber-400 transition-colors">
              <span *ngIf="!mobileMenuOpen">☰</span>
              <span *ngIf="mobileMenuOpen">✕</span>
            </button>
          </div>
        </div>

        <!-- Mobile Menu Dropdown -->
        <div *ngIf="mobileMenuOpen" class="md:hidden pb-4 border-t border-gray-800 mt-2 pt-4">
          <div class="flex flex-col space-y-3">
            <a routerLink="/restaurants" (click)="closeMobileMenu()" class="text-gray-300 hover:text-amber-400 font-medium transition-colors py-2">
              Restaurantes
            </a>
            
            <!-- Botones Auth (Si NO está logueado) -->
            <ng-container *ngIf="!authService.isLoggedIn()">
              <a routerLink="/login" (click)="closeMobileMenu()" class="text-gray-300 hover:text-amber-400 font-medium transition-colors py-2">
                Iniciar Sesión
              </a>
              <a routerLink="/register" (click)="closeMobileMenu()" class="bg-amber-600 hover:bg-amber-700 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all text-center">
                Registrarse
              </a>
            </ng-container>

            <!-- Menú Usuario (Si ESTÁ logueado) -->
            <ng-container *ngIf="authService.isLoggedIn()">
              <a *ngIf="!authService.isClient()" [routerLink]="getDashboardRoute()" (click)="closeMobileMenu()" class="text-gray-300 hover:text-amber-400 font-medium transition-colors py-2">
                Panel de Control
              </a>
              <a routerLink="/profile" (click)="closeMobileMenu()" class="text-gray-300 hover:text-amber-400 font-medium transition-colors py-2">
                Mi Perfil
              </a>
              <button (click)="logout(); closeMobileMenu()" class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all text-center">
                Cerrar Sesión
              </button>
            </ng-container>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent {
  mobileMenuOpen = false;

  constructor(public authService: AuthService, private router: Router) { }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

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
