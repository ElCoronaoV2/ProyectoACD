import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent {

  constructor(public auth: AuthService) { }

  get isLoggedIn(): boolean {
    return this.auth.isLoggedIn();
  }

  get dashboardRoute(): string {
    if (this.auth.isCEO()) return '/dashboard/ceo';
    if (this.auth.isDirector()) return '/dashboard/director';
    if (this.auth.isEmployee()) return '/dashboard/employee';
    return '/profile';
  }
}
