import { Injectable, signal } from '@angular/core';

export interface Toast {
    id: number;
    message: string;
    type: 'success' | 'error' | 'info' | 'warning';
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    toasts = signal<Toast[]>([]);
    private counter = 0;

    show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info') {
        const id = this.counter++;
        this.toasts.update(current => [...current, { id, message, type }]);

        // Auto dismiss after 4 seconds
        setTimeout(() => {
            this.remove(id);
        }, 4000);
    }

    showSuccess(message: string) {
        this.show(message, 'success');
    }

    showError(message: string) {
        this.show(message, 'error');
    }

    remove(id: number) {
        this.toasts.update(current => current.filter(t => t.id !== id));
    }

    showWarning(message: string) {
        this.show(message, 'warning');
    }

    showInfo(message: string) {
        this.show(message, 'info');
    }
}
