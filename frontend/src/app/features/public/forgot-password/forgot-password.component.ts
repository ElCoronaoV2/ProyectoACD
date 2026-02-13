import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';

@Component({
    selector: 'app-forgot-password',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
    template: `
    <div class="min-h-[70vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-2xl border-t-4 border-secondary-500">
        <div class="text-center">
          <h2 class="mt-2 text-3xl font-extrabold text-gray-900">
            Recuperar Contraseña
          </h2>
          <p class="mt-4 text-sm text-gray-600">
            Introduce la dirección de correo electrónico con la que te registraste y te enviaremos las instrucciones para restablecer tu contraseña.
          </p>
        </div>
        
        <form class="mt-8 space-y-6" [formGroup]="forgotForm" (ngSubmit)="onSubmit()">
          <div class="rounded-md shadow-sm -space-y-px">
            <div>
              <label for="email-address" class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
              <input id="email-address" name="email" type="email" autocomplete="email" required formControlName="email"
                class="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500 focus:z-10 sm:text-sm"
                placeholder="ejemplo@correo.com">
            </div>
          </div>

          <div *ngIf="notification" class="p-3 bg-green-50 text-green-700 rounded-md text-sm border border-green-200">
            {{ notification }}
          </div>

          <div>
            <button type="submit" 
              [disabled]="forgotForm.invalid || loading"
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all hover:animate-pulsing disabled:opacity-50 disabled:cursor-not-allowed">
              <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                <svg *ngIf="!loading" class="h-5 w-5 text-primary-500 group-hover:text-primary-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <svg *ngIf="loading" class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </span>
              {{ loading ? 'Enviando...' : 'Enviar Correo' }}
            </button>
          </div>
          
          <div class="text-center">
            <a routerLink="/login" class="font-medium text-gray-500 hover:text-gray-900 transition-colors text-sm">
              Volver al inicio de sesión
            </a>
          </div>
        </form>
      </div>
    </div>
  `,
    styles: []
})
export class ForgotPasswordComponent {
    forgotForm: FormGroup;
    loading = false;
    notification = '';

    constructor(private fb: FormBuilder) {
        this.forgotForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]]
        });
    }

    onSubmit() {
        if (this.forgotForm.valid) {
            this.loading = true;
            this.notification = '';

            // Simulating API call
            setTimeout(() => {
                this.loading = false;
                this.notification = 'Si el correo existe en nuestra base de datos, recibirás las instrucciones en breve.';
                this.forgotForm.reset();
            }, 2000);
        }
    }
}
