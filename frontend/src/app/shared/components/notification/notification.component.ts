import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  animations: [
    trigger('toastAnimation', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ],
  template: `
    <div class="fixed top-4 right-4 z-[9999] flex flex-col gap-3 pointer-events-none">
      <div *ngFor="let toast of notificationService.toasts()" 
           [@toastAnimation]
           class="glass-toast pointer-events-auto min-w-[450px] p-6 rounded-xl border-l-8 shadow-2xl flex items-center justify-between group cursor-pointer"
           [ngClass]="{
             'border-green-500 bg-white text-green-600': toast.type === 'success',
             'border-red-500 bg-white text-red-600': toast.type === 'error',
             'border-yellow-500 bg-white text-yellow-600': toast.type === 'warning',
             'border-blue-500 bg-white text-blue-600': toast.type === 'info'
           }"
           (click)="notificationService.remove(toast.id)">
        
        <div class="flex items-center gap-3">
          <!-- Icons -->
          <span class="text-xl" [ngSwitch]="toast.type">
            <span *ngSwitchCase="'success'">✅</span>
            <span *ngSwitchCase="'error'">⚠️</span>
            <span *ngSwitchCase="'warning'">⚡</span>
            <span *ngSwitchCase="'info'">ℹ️</span>
          </span>
          
          <div class="flex flex-col">
            <span class="font-bold text-xs tracking-widest uppercase opacity-60 mb-1">
              SYSTEM NOTIFICATION
            </span>
            <span class="font-semibold text-sm">{{ toast.message }}</span>
          </div>
        </div>

        <button class="opacity-0 group-hover:opacity-100 transition-opacity opacity-60 hover:opacity-100">
          ✕
        </button>
      </div>
    </div>
  `,
  styles: [`
    .glass-toast {
      box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      border-right: 1px solid rgba(255, 255, 255, 0.1);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      position: relative;
      overflow: hidden;
    }
    
    /* Neon Glow effect */
    .glass-toast::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(45deg, transparent 0%, rgba(255,255,255,0.03) 50%, transparent 100%);
      pointer-events: none;
    }
  `]
})
export class NotificationComponent {
  notificationService = inject(NotificationService);
}
