import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestaurantMapComponent } from '../../shared/components/restaurant-map/restaurant-map.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RestaurantMapComponent],
  template: `
    <div class="container mx-auto px-4 py-8">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-800 mb-4">Bienvenido a Restaurant-tec</h1>
        <p class="text-xl text-gray-600">Explora el mapa y descubre los mejores restaurantes de Alicante.</p>
      </div>
      
      <app-restaurant-map></app-restaurant-map>
    </div>
  `,
  styles: []
})
export class HomeComponent { }

