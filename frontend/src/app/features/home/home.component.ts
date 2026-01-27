import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="container mx-auto px-4 py-8">
      <div class="text-center">
        <h1 class="text-4xl font-bold text-gray-800 mb-4">Bienvenido a Restaurant-tec</h1>
        <p class="text-xl text-gray-600">Encuentra y reserva en los mejores restaurantes.</p>
        
        <div class="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- Placeholder for restaurant cards -->
          <div class="bg-white p-6 rounded-lg shadow-md border-t-4 border-secondary-500">
            <h3 class="font-bold text-lg mb-2">Restaurante Ejemplo</h3>
            <p class="text-gray-500 mb-4">Comida exquisita en un ambiente relajado.</p>
            <button class="bg-primary-500 text-white px-4 py-2 rounded hover:bg-primary-600 transition-colors w-full">
              Ver Detalles
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: []
})
export class HomeComponent { }
