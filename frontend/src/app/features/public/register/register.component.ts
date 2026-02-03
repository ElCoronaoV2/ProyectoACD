import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  template: `
    <div class="min-h-[90vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 bg-gray-50">
      <div class="max-w-2xl w-full space-y-8 bg-white p-10 rounded-xl shadow-2xl border-t-4 border-secondary-500">
        
        <!-- SUCCESS STATE -->
        <div *ngIf="successMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative text-center">
            <strong class="font-bold">¡Registro exitoso!</strong>
            <span class="block sm:inline">{{ successMessage }}</span>
            <p class="mt-2 text-sm">Ya puedes cerrar esta pestaña y revisar tu correo.</p>
        </div>

        <!-- ERROR STATE -->
        <div *ngIf="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative text-center">
            <strong class="font-bold">Error:</strong>
            <span class="block sm:inline">{{ errorMessage }}</span>
        </div>

        <div class="text-center" *ngIf="!successMessage">
          <h2 class="mt-2 text-3xl font-extrabold text-gray-900">
            Crear Cuenta
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            ¿Ya tienes cuenta?
            <a routerLink="/login" class="font-medium text-primary-600 hover:text-primary-500 transition-colors">
              Inicia sesión aquí
            </a>
          </p>
        </div>
        
        <form *ngIf="!successMessage" class="mt-8 space-y-6" [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Username -->
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700 mb-1">Nombre de Usuario</label>
              <input id="username" type="text" formControlName="username" 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="JuanGourmet">
              <p *ngIf="registerForm.get('username')?.touched && registerForm.get('username')?.hasError('required')" class="mt-1 text-xs text-red-600">Este campo es obligatorio</p>
            </div>

            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
              <input id="email" type="email" formControlName="email"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="juan@ejemplo.com">
                 <p *ngIf="registerForm.get('email')?.touched && registerForm.get('email')?.hasError('email')" class="mt-1 text-xs text-red-600">Introduce un correo válido</p>
            </div>

             <!-- Phone -->
             <div class="md:col-span-2">
              <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
              <input id="phone" type="tel" formControlName="phone"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="+34 600 000 000">
            </div>

            <!-- Allergens Dropdown/Select -->
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">Alergias e Intolerancias (Selecciona las tuyas)</label>
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div *ngFor="let allergen of allergens" 
                     (click)="toggleAllergen(allergen.id)"
                     [class.bg-primary-50]="isSelected(allergen.id)"
                     [class.border-primary-500]="isSelected(allergen.id)"
                     [class.text-primary-700]="isSelected(allergen.id)"
                     class="cursor-pointer border rounded-lg p-2 flex flex-col items-center justify-center text-center transition-all hover:shadow-md border-gray-200">
                  <span class="text-2xl mb-1">{{ allergen.icon }}</span>
                  <span class="text-xs font-medium">{{ allergen.name }}</span>
                </div>
              </div>
            </div>

            <!-- Passwords -->
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
              <input id="password" type="password" formControlName="password"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="••••••••">
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">Repetir Contraseña</label>
              <input id="confirmPassword" type="password" formControlName="confirmPassword"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="••••••••">
               <p *ngIf="registerForm.errors?.['mismatch'] && registerForm.get('confirmPassword')?.touched" class="mt-1 text-xs text-red-600">Las contraseñas no coinciden</p>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="pt-4">
            <button type="submit" 
              [disabled]="registerForm.invalid || isLoading"
              class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-secondary-500 hover:bg-secondary-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-secondary-500 transition-all hover:shadow-lg hover:animate-pulsing disabled:opacity-50 disabled:cursor-not-allowed">
              <span *ngIf="isLoading">Procesando...</span>
              <span *ngIf="!isLoading">Crear Cuenta</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: []
})
export class RegisterComponent {
  registerForm: FormGroup;
  selectedAllergens: string[] = [];
  isLoading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

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
          this.successMessage = "Se ha enviado un correo de verificación a " + userData.email;
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Registration error:', err);
          if (err.error && typeof err.error === 'object') {
            this.errorMessage = JSON.stringify(err.error);
          } else {
            this.errorMessage = err.error || "Error al registrar usuario";
          }
        }
      });
    }
  }
}
