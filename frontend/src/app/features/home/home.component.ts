import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestaurantMapComponent } from '../../shared/components/restaurant-map/restaurant-map.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RestaurantMapComponent],
  template: `
    <div class="home-background" style="background-image: url('assets/images/landing/landing_3.png');">
      <div class="background-overlay"></div>
      <div class="content-container container mx-auto px-4 py-8 relative z-10">
        <div class="text-center mb-8 bg-white/80 p-6 rounded-lg shadow-lg backdrop-blur-sm max-w-3xl mx-auto">
          <h1 class="text-4xl font-bold text-gray-800 mb-4">Bienvenido a Restaurant-tec</h1>
          <p class="text-xl text-gray-600">Explora el mapa y descubre los mejores restaurantes de Alicante.</p>
        </div>
        
        <app-restaurant-map></app-restaurant-map>
      </div>
    </div>
  `,
  styles: [`
    .home-background {
      position: relative;
      min-height: calc(100vh - 64px); /* Adjust for navbar height */
      background-size: cover;
      background-position: center;
      background-attachment: fixed;
    }
    .background-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.3); /* Dark overlay for better contrast */
    }
  `]
})
export class HomeComponent { }

