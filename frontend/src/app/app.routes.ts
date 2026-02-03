import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/components/main-layout/main-layout.component';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            {
                path: '',
                loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
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
            },
            {
                path: 'admin',
                loadComponent: () => import('./features/admin/admin-map.component').then(m => m.AdminMapComponent)
            }
        ]
    },
    { path: '**', redirectTo: '' }
];
