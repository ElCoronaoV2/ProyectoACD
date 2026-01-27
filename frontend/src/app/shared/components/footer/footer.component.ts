import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="bg-primary-900 text-white py-8 mt-auto">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <div class="mb-4 md:mb-0 text-center md:text-left">
            <h3 class="text-lg font-bold text-secondary-400">Restaurant-tec</h3>
            <p class="text-primary-100 text-sm mt-1">Tu mesa favorita, a un click de distancia.</p>
          </div>
          
          <div class="text-sm text-primary-200">
            &copy; {{ currentYear }} Restaurant-tec. Todos los derechos reservados.
          </div>
        </div>
      </div>
    </footer>
  `,
  styles: []
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
