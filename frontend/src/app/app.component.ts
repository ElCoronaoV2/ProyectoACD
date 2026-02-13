import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NotificationComponent } from './shared/components/notification/notification.component';
import { SessionExpiredModalComponent } from './shared/components/session-expired-modal/session-expired-modal.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NotificationComponent, SessionExpiredModalComponent],
  template: `
    <app-notification></app-notification>
    <app-session-expired-modal></app-session-expired-modal>
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AppComponent {
  title = 'Restaurant-tec';
}
