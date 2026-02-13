import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
      <div class="max-w-2xl w-full space-y-8 bg-gray-800/50 backdrop-blur-sm p-10 rounded-xl shadow-2xl border border-gray-700">
        
        <!-- ERROR STATE -->
        <div *ngIf="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative text-center">
            <strong class="font-bold">Error:</strong>
            <span class="block sm:inline">{{ errorMessage }}</span>
        </div>

        <div class="text-center" *ngIf="!showSuccessModal">
          <h2 class="mt-2 text-3xl font-extrabold text-white">
            Crear Cuenta
          </h2>
          <p class="mt-2 text-sm text-gray-400">
            ¿Ya tienes cuenta?
            <a routerLink="/login" class="font-medium text-amber-500 hover:text-amber-400 transition-colors">
              Inicia sesión aquí
            </a>
          </p>
        </div>
        
        <form *ngIf="!showSuccessModal" class="mt-8 space-y-6" [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Username -->
            <div>
              <label for="username" class="block text-sm font-medium text-gray-300 mb-1">Nombre de Usuario</label>
              <input id="username" type="text" formControlName="username" 
                class="appearance-none block w-full px-3 py-2 border border-gray-600 bg-gray-700/50 rounded-md placeholder-gray-400 text-white focus:outline-none focus:ring-amber-500 focus:border-amber-500 sm:text-sm"
                placeholder="JuanGourmet">
              <p *ngIf="registerForm.get('username')?.touched && registerForm.get('username')?.hasError('required')" class="mt-1 text-xs text-red-400">Este campo es obligatorio</p>
            </div>

            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-gray-300 mb-1">Correo Electrónico</label>
              <input id="email" type="email" formControlName="email"
                class="appearance-none block w-full px-3 py-2 border border-gray-600 bg-gray-700/50 rounded-md placeholder-gray-400 text-white focus:outline-none focus:ring-amber-500 focus:border-amber-500 sm:text-sm"
                placeholder="juan@ejemplo.com">
                 <p *ngIf="registerForm.get('email')?.touched && registerForm.get('email')?.hasError('email')" class="mt-1 text-xs text-red-400">Introduce un correo válido</p>
            </div>

             <!-- Phone -->
             <div class="md:col-span-2">
              <label for="phone" class="block text-sm font-medium text-gray-300 mb-1">Teléfono</label>
              <input id="phone" type="tel" formControlName="phone"
                class="appearance-none block w-full px-3 py-2 border border-gray-600 bg-gray-700/50 rounded-md placeholder-gray-400 text-white focus:outline-none focus:ring-amber-500 focus:border-amber-500 sm:text-sm"
                placeholder="+34 600 000 000">
            </div>

            <!-- Allergens Dropdown/Select -->
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-300 mb-2">Alergias e Intolerancias (Selecciona las tuyas)</label>
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div *ngFor="let allergen of allergens" 
                     (click)="toggleAllergen(allergen.id)"
                     [ngClass]="{
                       'bg-amber-900 border-amber-500 text-amber-400': isSelected(allergen.id),
                       'bg-gray-700/30 border-gray-600 text-gray-300': !isSelected(allergen.id)
                     }"
                     class="cursor-pointer border rounded-lg p-2 flex flex-col items-center justify-center text-center transition-all hover:shadow-md hover:border-amber-400">
                  <span class="text-2xl mb-1">{{ allergen.icon }}</span>
                  <span class="text-xs font-medium">{{ allergen.name }}</span>
                </div>
              </div>
              <p *ngIf="selectedAllergens.length > 0" class="mt-2 text-xs text-amber-400">
                ✓ Seleccionados: {{ selectedAllergens.length }}
              </p>
            </div>

            <!-- Passwords -->
            <div>
              <label for="password" class="block text-sm font-medium text-gray-300 mb-1">Contraseña</label>
              <input id="password" type="password" formControlName="password"
                class="appearance-none block w-full px-3 py-2 border border-gray-600 bg-gray-700/50 rounded-md placeholder-gray-400 text-white focus:outline-none focus:ring-amber-500 focus:border-amber-500 sm:text-sm"
                placeholder="••••••••">
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-300 mb-1">Repetir Contraseña</label>
              <input id="confirmPassword" type="password" formControlName="confirmPassword"
                class="appearance-none block w-full px-3 py-2 border border-gray-600 bg-gray-700/50 rounded-md placeholder-gray-400 text-white focus:outline-none focus:ring-amber-500 focus:border-amber-500 sm:text-sm"
                placeholder="••••••••">
               <p *ngIf="registerForm.errors?.['mismatch'] && registerForm.get('confirmPassword')?.touched" class="mt-1 text-xs text-red-400">Las contraseñas no coinciden</p>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="pt-4">
            <button type="submit" 
              [disabled]="registerForm.invalid || isLoading"
              class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 transition-all hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed">
              <span *ngIf="isLoading">Procesando...</span>
              <span *ngIf="!isLoading">Crear Cuenta</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Beautiful Success Modal -->
    <div *ngIf="showSuccessModal" 
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 px-4 animate-fadeIn"
         (click)="closeModal()">
      <div class="bg-white rounded-2xl shadow-2xl max-w-md w-full p-8 relative animate-slideUp"
           (click)="$event.stopPropagation()">
        
        <!-- Close button -->
        <button (click)="closeModal()" 
                class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>

        <!-- Email icon with animation -->
        <div class="flex justify-center mb-6">
          <div class="relative">
            <div class="text-7xl animate-bounce">✉️</div>
            <div class="absolute -top-2 -right-2 bg-green-500 text-white rounded-full w-8 h-8 flex items-center justify-center animate-ping-slow">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- Title and message -->
        <h3 class="text-3xl font-bold text-center text-gray-900 mb-4">
          ¡Cuenta Creada! 🎉
        </h3>
        <p class="text-center text-gray-600 mb-2">
          Hemos enviado un correo de verificación a:
        </p>
        <p class="text-center text-primary-600 font-semibold text-lg mb-6">
          {{ registeredEmail }}
        </p>
        <p class="text-center text-gray-500 text-sm mb-8">
          Por favor, revisa tu bandeja de entrada (y la carpeta de spam) para activar tu cuenta.
        </p>

        <!-- Action buttons -->
        <div class="space-y-3">
          <button (click)="openEmailClient()"
                  class="w-full bg-gradient-to-r from-blue-500 to-blue-600 text-white font-semibold py-3 px-6 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all shadow-lg hover:shadow-xl transform hover:-translate-y-0.5">
            📧 Abrir Mi Correo
          </button>
          <button (click)="closeModal()"
                  class="w-full bg-gray-100 text-gray-700 font-medium py-3 px-6 rounded-lg hover:bg-gray-200 transition-all">
            Entendido
          </button>
        </div>

        <!-- Optional: Countdown or additional info -->
        <p class="text-center text-xs text-gray-400 mt-6">
          El enlace de verificación expira en 24 horas
        </p>
      </div>
    </div>
  `,
  styles: [`
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
    
    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    @keyframes ping-slow {
      0% {
        transform: scale(1);
        opacity: 1;
      }
      50% {
        transform: scale(1.1);
        opacity: 0.8;
      }
      100% {
        transform: scale(1);
        opacity: 1;
      }
    }
    
    .animate-fadeIn {
      animation: fadeIn 0.3s ease-out;
    }
    
    .animate-slideUp {
      animation: slideUp 0.4s ease-out;
    }
    
    .animate-ping-slow {
      animation: ping-slow 2s ease-in-out infinite;
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  selectedAllergens: string[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  showSuccessModal = false;
  registeredEmail = '';

  allergens = [
    { id: 'gluten', name: 'Gluten', icon: '🌾' },
    { id: 'crustaceans', name: 'Crustáceos', icon: '🦐' },
    { id: 'eggs', name: 'Huevos', icon: '🥚' },
    { id: 'fish', name: 'Pescado', icon: '🐟' },
    { id: 'peanuts', name: 'Cacahuetes', icon: '🥜' },
    { id: 'soy', name: 'Soja', icon: '🫘' },
    { id: 'dairy', name: 'Lácteos', icon: '🥛' },
    { id: 'nuts', name: 'Frutos Cáscara', icon: '🌰' },
    { id: 'celery', name: 'Apio', icon: '🌿' },
    { id: 'mustard', name: 'Mostaza', icon: '🌭' },
    { id: 'sesame', name: 'Sésamo', icon: '🥯' },
    { id: 'sulphites', name: 'Sulfitos', icon: '🍷' },
    { id: 'lupin', name: 'Altramuces', icon: '🥨' },
    { id: 'molluscs', name: 'Moluscos', icon: '🐙' }
  ];

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      allergens: [[]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { mismatch: true };
    }
    return null;
  }

  toggleAllergen(id: string) {
    if (this.selectedAllergens.includes(id)) {
      this.selectedAllergens = this.selectedAllergens.filter(a => a !== id);
    } else {
      this.selectedAllergens.push(id);
    }
    this.registerForm.patchValue({ allergens: this.selectedAllergens });
  }

  isSelected(id: string): boolean {
    return this.selectedAllergens.includes(id);
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = null;

      const userData = {
        nombre: this.registerForm.get('username')?.value,
        email: this.registerForm.get('email')?.value,
        telefono: this.registerForm.get('phone')?.value,
        password: this.registerForm.get('password')?.value,
        alergenos: this.selectedAllergens.join(',')
      };

      this.authService.register(userData).subscribe({
        next: (res) => {
          this.isLoading = false;
          // Success - show modal
          this.registeredEmail = userData.email;
          this.showSuccessModal = true;
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Registration error:', err);

          // Check if error actually contains success message (backend quirk)
          if (err.error && err.error.text && err.error.text.includes('verificar')) {
            this.registeredEmail = userData.email;
            this.showSuccessModal = true;
            return;
          }

          // Handle actual errors
          if (err.error && typeof err.error === 'string') {
            this.errorMessage = err.error;
          } else if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
          } else {
            this.errorMessage = "Error al registrar usuario. Por favor, inténtalo de nuevo.";
          }
        }
      });
    }
  }

  closeModal() {
    this.showSuccessModal = false;
  }

  openEmailClient() {
    // This will open the user's default email client
    window.location.href = 'mailto:';
  }
}
