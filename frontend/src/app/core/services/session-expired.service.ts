import { Injectable, signal } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class SessionExpiredService {
    private visible = signal(false);
    private message = signal('Tu sesión ha expirado. Por favor, inicia sesión de nuevo.');

    isVisible() {
        return this.visible();
    }

    getMessage() {
        return this.message();
    }

    show(message?: string) {
        if (this.visible()) return;
        if (message) {
            this.message.set(message);
        }
        this.visible.set(true);
    }

    hide() {
        this.visible.set(false);
    }
}
