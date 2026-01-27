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
            { path: 'register', redirectTo: '', pathMatch: 'full' }
        ]
    },
    { path: '**', redirectTo: '' }
];
