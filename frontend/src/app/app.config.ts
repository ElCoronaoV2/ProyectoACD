import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideNgxStripe } from 'ngx-stripe';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideNgxStripe('pk_test_51SpGLHLP1o0EDopZYjwkq8bA36EBeJBhqcSKb2xj7ADT1uRPDEJk3YMHi40W1RRN6JhbjuBTS6QKFyZJHASXDUKz004dAR6G7l')
  ]
};
