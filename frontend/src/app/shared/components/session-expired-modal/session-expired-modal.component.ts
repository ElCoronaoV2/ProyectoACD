import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SessionExpiredService } from '../../../core/services/session-expired.service';

@Component({
    selector: 'app-session-expired-modal',
    standalone: true,
    imports: [CommonModule],
    template: `
      <div *ngIf="sessionExpiredService.isVisible()"
           class="fixed inset-0 z-[1000] flex items-center justify-center bg-black/60 backdrop-blur-sm px-4"
           (click)="onBackdropClick($event)">
        <div class="w-full max-w-md rounded-2xl border border-amber-400/40 bg-gray-900/95 p-6 shadow-2xl">
          <div class="flex items-start gap-3">
            <div class="flex h-12 w-12 items-center justify-center rounded-full bg-amber-500/15 text-amber-400">
              <span class="text-2xl">⏳</span>
            </div>
            <div class="flex-1">
              <h3 class="text-lg font-semibold text-amber-100">Sesión expirada</h3>
              <p class="mt-1 text-sm text-amber-200/80">{{ sessionExpiredService.getMessage() }}</p>
            </div>
          </div>

          <div class="mt-6 flex flex-col gap-2 sm:flex-row sm:justify-end">
            <button
              class="rounded-lg border border-gray-700 bg-gray-800/60 px-4 py-2 text-sm font-medium text-gray-200 hover:bg-gray-800"
              (click)="close()">
              Cerrar
            </button>
            <button
              class="rounded-lg bg-amber-500 px-4 py-2 text-sm font-semibold text-gray-900 hover:bg-amber-400"
              (click)="goToLogin()">
              Ir a iniciar sesión
            </button>
          </div>
        </div>
      </div>
    `
})
export class SessionExpiredModalComponent {
    sessionExpiredService = inject(SessionExpiredService);
    private router = inject(Router);

    onBackdropClick(event: MouseEvent) {
        if ((event.target as HTMLElement).classList.contains('fixed')) {
            this.close();
        }
    }

    close() {
        this.sessionExpiredService.hide();
    }

    goToLogin() {
        this.sessionExpiredService.hide();
        this.router.navigate(['/login']);
    }
}
