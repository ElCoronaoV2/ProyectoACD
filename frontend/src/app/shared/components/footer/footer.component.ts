import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="bg-gray-950 text-white py-8 mt-auto border-t border-gray-800">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <div class="mb-4 md:mb-0 text-center md:text-left">
            <h3 class="text-lg font-bold text-amber-500">Restaurant-tec</h3>
            <p class="text-gray-400 text-sm mt-1">Tu mesa favorita, a un click de distancia.</p>
          </div>
          
          <div class="text-sm text-gray-400">
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
