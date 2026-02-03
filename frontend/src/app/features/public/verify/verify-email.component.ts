import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-verify-email',
    standalone: true,
    imports: [CommonModule, RouterModule],
    template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-lg border-t-4" 
           [ngClass]="{'border-green-500': status === 'success', 'border-red-500': status === 'error', 'border-blue-500': status === 'loading'}">
        
        <div class="text-center">
            
          <div *ngIf="status === 'loading'">
             <h2 class="mt-2 text-3xl font-extrabold text-gray-900">Verificando...</h2>
             <p class="mt-2 text-gray-600">Por favor espera un momento.</p>
          </div>

          <div *ngIf="status === 'success'">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100">
              <svg class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
              </svg>
            </div>
            <h2 class="mt-2 text-3xl font-extrabold text-gray-900">¡Cuenta Verificada!</h2>
            <p class="mt-2 text-gray-600">Tu correo ha sido confirmado correctamente.</p>
            <div class="mt-6">
              <a routerLink="/login" class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700">
                Iniciar Sesión
              </a>
            </div>
          </div>

          <div *ngIf="status === 'error'">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100">
              <svg class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </div>
            <h2 class="mt-2 text-3xl font-extrabold text-gray-900">Error</h2>
            <p class="mt-2 text-red-600">{{ errorMessage }}</p>
            <div class="mt-6">
              <a routerLink="/" class="text-indigo-600 hover:text-indigo-500">
                Volver al inicio
              </a>
            </div>
          </div>

        </div>
      </div>
    </div>
  `
})
export class VerifyEmailComponent implements OnInit {
    status: 'loading' | 'success' | 'error' = 'loading';
    errorMessage: string = '';

    constructor(
        private route: ActivatedRoute,
        private authService: AuthService
    ) { }

    ngOnInit() {
        const token = this.route.snapshot.queryParamMap.get('token');

        if (token) {
            this.authService.verifyEmail(token).subscribe({
                next: () => {
                    this.status = 'success';
                },
                error: (err) => {
                    this.status = 'error';
                    this.errorMessage = err.error || 'Token inválido o expirado.';
                }
            });
        } else {
            this.status = 'error';
            this.errorMessage = 'Token no proporcionado.';
        }
    }
}
