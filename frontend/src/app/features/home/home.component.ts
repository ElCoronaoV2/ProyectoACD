import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestaurantMapComponent } from '../../shared/components/restaurant-map/restaurant-map.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RestaurantMapComponent],
  template: `
    <div class="home-container">
      <div class="content-container container mx-auto px-4 py-8">
        <div class="text-center mb-8 bg-gray-800/80 p-6 rounded-lg shadow-lg backdrop-blur-sm max-w-3xl mx-auto border border-gray-700">
          <h1 class="text-4xl font-bold text-white mb-4">Bienvenido a Restaurant-tec</h1>
          <p class="text-xl text-gray-300">Explora el mapa y descubre los mejores restaurantes de Alicante.</p>
        </div>
        
        <app-restaurant-map></app-restaurant-map>
      </div>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: calc(100vh - 64px);
      background: linear-gradient(135deg, #111827 0%, #1f2937 50%, #111827 100%);
      user-select: none;
      -webkit-user-select: none;
      -moz-user-select: none;
      -ms-user-select: none;
    }
  `]
})
export class HomeComponent { }

