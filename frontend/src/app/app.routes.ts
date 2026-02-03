import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/components/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            {
                path: '',
                loadComponent: () => import('./features/landing-page/landing-page.component').then(m => m.LandingPageComponent)
            },
            {
                path: 'home',
                loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent),
                canActivate: [authGuard],
                // Cliente access (and typically everyone can see home map, but we protect it as requested)
            },
            {
                path: 'restaurants', // Public access to map/list? User said "Once logged in...". Keep it public for now? Or redirect?
                // Providing public access route for guests
                loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
            },
            {
                path: 'dashboard/director',
                loadComponent: () => import('./features/dashboard/director/director.component').then(m => m.DirectorComponent),
                canActivate: [authGuard],
                data: { roles: ['DIRECTOR'] }
            },
            {
                path: 'dashboard/ceo',
                loadComponent: () => import('./features/dashboard/ceo/ceo.component').then(m => m.CeoComponent),
                canActivate: [authGuard],
                data: { roles: ['CEO', 'DIRECTOR'] }
            },
            {
                path: 'dashboard/employee',
                loadComponent: () => import('./features/dashboard/employee/employee.component').then(m => m.EmployeeComponent),
                canActivate: [authGuard],
                data: { roles: ['EMPLEADO', 'CEO', 'DIRECTOR'] }
            },
            {
                path: 'profile',
                loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
                canActivate: [authGuard]
            },
            {
                path: 'login',
                loadComponent: () => import('./features/public/login/login.component').then(m => m.LoginComponent)
            },
            {
                path: 'forgot-password',
                loadComponent: () => import('./features/public/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
            },
            {
                path: 'register',
                loadComponent: () => import('./features/public/register/register.component').then(m => m.RegisterComponent)
            },
            {
                path: 'verify',
                loadComponent: () => import('./features/public/verify/verify-email.component').then(m => m.VerifyEmailComponent)
            }
        ]
    },
    { path: '**', redirectTo: '' }
];
