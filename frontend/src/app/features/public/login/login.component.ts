import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HttpClientModule],
  template: `
    <div class="min-h-[80vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-2xl border-t-4 border-secondary-500">
        <div class="text-center">
          <h2 class="mt-2 text-3xl font-extrabold text-gray-900">
            Iniciar Sesión
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            ¿No tienes cuenta?
            <a routerLink="/register" class="font-medium text-primary-600 hover:text-primary-500 transition-colors">
              Regístrate aquí
            </a>
          </p>
        </div>
        
        <!-- SUCCESS MESSAGE -->
        <div *ngIf="successMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4 flex items-center">
          <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
          </svg>
          <span class="block sm:inline">{{ successMessage }}</span>
        </div>
        
        <!-- ERROR MESSAGE -->
        <div *ngIf="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4 flex items-center">
          <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
          </svg>
          <span class="block sm:inline">{{ errorMessage }}</span>
        </div>

        <form class="mt-8 space-y-6" [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="rounded-md shadow-sm -space-y-px">
            <div class="mb-4">
              <label for="email-address" class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
              <input id="email-address" name="email" type="email" autocomplete="email" required formControlName="email"
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="ejemplo@correo.com">
            </div>
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
              <input id="password" name="password" type="password" autocomplete="current-password" required formControlName="password"
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="••••••••">
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input id="remember-me" name="remember-me" type="checkbox"
                class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded">
              <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                Recordarme
              </label>
            </div>

            <div class="text-sm">
              <a routerLink="/forgot-password" class="font-medium text-primary-600 hover:text-primary-500 transition-colors">
                ¿Olvidaste tu contraseña?
              </a>
            </div>
          </div>

          <div>
            <button type="submit" 
              [disabled]="loginForm.invalid || isLoading"
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all hover:animate-pulsing disabled:opacity-50 disabled:cursor-not-allowed">
              <span *ngIf="isLoading">Cargando...</span>
              <span *ngIf="!isLoading">Entrar</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = null;
      this.successMessage = null;

      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          this.isLoading = false;
          this.successMessage = "¡Inicio de sesión correcto! Redirigiendo...";
          // Esperar 1.5 segundos para que el usuario vea el mensaje antes de redirigir
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 1500);
        },
        error: (err) => {
          this.isLoading = false;
          // Manejo de mensajes de error específicos del backend
          if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
          } else if (err.error && err.error.error) {
            // Mensajes personalizados según el código de error
            switch (err.error.error) {
              case 'INVALID_CREDENTIALS':
                this.errorMessage = "La contraseña es incorrecta. Por favor, verifica e intenta de nuevo.";
                break;
              case 'USER_NOT_FOUND':
                this.errorMessage = "No existe una cuenta con ese correo electrónico.";
                break;
              case 'ACCOUNT_DISABLED':
                this.errorMessage = "Tu cuenta no está verificada. Por favor, revisa tu correo electrónico para activarla.";
                break;
              case 'ACCOUNT_LOCKED':
                this.errorMessage = "Tu cuenta ha sido bloqueada. Contacta al administrador.";
                break;
              default:
                this.errorMessage = "Error al iniciar sesión. Inténtalo de nuevo.";
            }
          } else if (err.status === 401) {
            this.errorMessage = "Credenciales incorrectas. Verifica tu correo y contraseña.";
          } else if (err.status === 403) {
            this.errorMessage = "Tu cuenta no está activa. Por favor, verifica tu correo electrónico.";
          } else if (err.status === 0) {
            this.errorMessage = "No se puede conectar con el servidor. Verifica tu conexión a internet.";
          } else {
            this.errorMessage = "Error al iniciar sesión. Inténtalo de nuevo.";
          }
        }
      });
    }
  }
}

