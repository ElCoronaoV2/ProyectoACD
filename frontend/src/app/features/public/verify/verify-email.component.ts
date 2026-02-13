import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 bg-gray-800/50 backdrop-blur-sm p-10 rounded-xl shadow-2xl border-t-4 transition-all" 
           [ngClass]="{'border-green-500': status === 'success', 'border-red-500': status === 'error', 'border-amber-500': status === 'loading'}">
        
        <div class="text-center">
            
          <!-- Loading State -->
          <div *ngIf="status === 'loading'" class="animate-fadeIn">
             <div class="mx-auto flex items-center justify-center h-16 w-16 mb-6">
               <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-amber-500"></div>
             </div>
             <h2 class="mt-2 text-3xl font-extrabold text-white">Verificando...</h2>
             <p class="mt-2 text-gray-400">Por favor espera un momento.</p>
          </div>

          <!-- Success State -->
          <div *ngIf="status === 'success'" class="animate-fadeIn">
            <div class="mx-auto flex items-center justify-center h-20 w-20 rounded-full bg-green-900/50 border-2 border-green-500 mb-6 animate-scaleIn">
              <svg class="h-12 w-12 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
              </svg>
            </div>
            <h2 class="mt-2 text-3xl font-extrabold text-white">¡Cuenta Verificada! 🎉</h2>
            <p class="mt-4 text-gray-300">Tu correo ha sido confirmado correctamente.</p>
            <p class="mt-2 text-gray-400 text-sm">Ya puedes iniciar sesión con tu cuenta.</p>
            <div class="mt-8">
              <a routerLink="/login" 
                 class="w-full inline-flex justify-center py-3 px-6 border border-transparent rounded-lg shadow-sm text-sm font-semibold text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 focus:ring-offset-gray-800 transition-all transform hover:-translate-y-0.5 hover:shadow-lg">
                Iniciar Sesión →
              </a>
            </div>
          </div>

          <!-- Error State -->
          <div *ngIf="status === 'error'" class="animate-fadeIn">
            <div class="mx-auto flex items-center justify-center h-20 w-20 rounded-full bg-red-900/50 border-2 border-red-500 mb-6 animate-scaleIn">
              <svg class="h-12 w-12 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </div>
            <h2 class="mt-2 text-3xl font-extrabold text-white">Error de Verificación</h2>
            <p class="mt-4 text-red-400 font-medium">{{ errorMessage }}</p>
            <p class="mt-4 text-gray-400 text-sm">Si crees que esto es un error, contacta con soporte.</p>
            <div class="mt-8 space-y-3">
              <a routerLink="/register" 
                 class="w-full inline-flex justify-center py-3 px-6 border border-transparent rounded-lg shadow-sm text-sm font-semibold text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 focus:ring-offset-gray-800 transition-all">
                Registrarse de Nuevo
              </a>
              <a routerLink="/" 
                 class="w-full inline-flex justify-center py-2 px-4 text-sm font-medium text-gray-400 hover:text-gray-200 transition-colors">
                Volver al Inicio
              </a>
            </div>
          </div>

        </div>
      </div>
    </div>
  `,
  styles: [`
      @keyframes fadeIn {
        from {
          opacity: 0;
          transform: translateY(10px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }

      @keyframes scaleIn {
        from {
          transform: scale(0.8);
          opacity: 0;
        }
        to {
          transform: scale(1);
          opacity: 1;
        }
      }

      .animate-fadeIn {
        animation: fadeIn 0.5s ease-out;
      }

      .animate-scaleIn {
        animation: scaleIn 0.4s ease-out;
      }
    `]
})
export class VerifyEmailComponent implements OnInit {
  status: 'loading' | 'success' | 'error' = 'loading';
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService
  ) { }

  private extractErrorMessage(err: any): string {
    if (err?.error) {
      if (typeof err.error === 'string') {
        return err.error;
      }
      if (err.error.message) {
        return err.error.message;
      }
      if (err.error.text) {
        return err.error.text;
      }
      if (err.error.error) {
        return typeof err.error.error === 'string'
          ? err.error.error
          : 'Token inválido o expirado.';
      }
    }

    if (err?.message) {
      return err.message;
    }

    return 'Token inválido o expirado.';
  }

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.authService.verifyEmail(token).subscribe({
        next: () => {
          this.status = 'success';
        },
        error: (err) => {
          console.error('Verification error:', err);

          const errorMessage = this.extractErrorMessage(err);
          const normalizedMessage = errorMessage.toLowerCase();

          if (normalizedMessage.includes('cuenta verificada')) {
            this.status = 'success';
            return;
          }

          this.status = 'error';
          this.errorMessage = errorMessage;
        }
      });
    } else {
      this.status = 'error';
      this.errorMessage = 'No se proporcionó ningún token de verificación.';
    }
  }
}
