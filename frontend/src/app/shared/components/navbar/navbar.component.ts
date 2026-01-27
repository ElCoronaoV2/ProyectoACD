import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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
            <span class="font-bold text-xl tracking-wide">Restaurant-tec</span>
          </a>

          <!-- Navigation Links -->
          <div class="hidden md:flex items-center space-x-6">
            <a routerLink="/restaurants" class="text-white hover:text-secondary-200 font-medium transition-colors">
              Restaurantes
            </a>
            
            <!-- Auth Buttons -->
            <div class="flex items-center space-x-4 ml-4">
              <a routerLink="/login" class="text-white hover:text-secondary-100 font-medium transition-colors">
                Iniciar Sesión
              </a>
              <a routerLink="/register" class="bg-secondary-500 hover:bg-secondary-600 text-white px-4 py-2 rounded-full font-bold shadow-md transition-all hover:shadow-lg transform hover:-translate-y-0.5 hover:animate-jelly">
                Registrarse
              </a>
            </div>
          </div>

          <!-- Mobile Menu Button (Placeholder) -->
          <div class="md:hidden flex items-center">
            <button class="text-white hover:text-secondary-200">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent { }
