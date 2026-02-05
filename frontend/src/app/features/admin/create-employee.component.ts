import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LocalService } from '../../core/services/local.service';
import { Local } from '../../core/models/local.model';

@Component({
    selector: 'app-create-employee',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
    template: `
    <div class="min-h-[90vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 bg-gray-50">
      <div class="max-w-xl w-full space-y-8 bg-white p-10 rounded-xl shadow-2xl border-t-4 border-primary-500">
        
        <!-- SUCCESS STATE -->
        <div *ngIf="successMessage" class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative text-center">
            <strong class="font-bold">¡Empleado creado!</strong>
            <span class="block sm:inline">{{ successMessage }}</span>
            <button (click)="resetForm()" class="mt-3 block w-full text-center text-primary-600 hover:text-primary-500 font-medium">
                Crear otro empleado
            </button>
        </div>

        <!-- ERROR STATE -->
        <div *ngIf="errorMessage" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative text-center">
            <strong class="font-bold">Error:</strong>
            <span class="block sm:inline">{{ errorMessage }}</span>
        </div>

        <div class="text-center" *ngIf="!successMessage">
          <div class="text-5xl mb-4">👨‍🍳</div>
          <h2 class="text-3xl font-extrabold text-gray-900">
            Crear Empleado
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            Registra un nuevo empleado y asígnalo a un restaurante
          </p>
        </div>
        
        <form *ngIf="!successMessage" class="mt-8 space-y-6" [formGroup]="employeeForm" (ngSubmit)="onSubmit()">
          
          <div class="space-y-4">
            <!-- Nombre -->
            <div>
              <label for="nombre" class="block text-sm font-medium text-gray-700 mb-1">Nombre Completo</label>
              <input id="nombre" type="text" formControlName="nombre" 
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="Juan García">
              <p *ngIf="employeeForm.get('nombre')?.touched && employeeForm.get('nombre')?.hasError('required')" class="mt-1 text-xs text-red-600">Este campo es obligatorio</p>
            </div>

            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
              <input id="email" type="email" formControlName="email"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="juan.empleado@restaurant.com">
              <p *ngIf="employeeForm.get('email')?.touched && employeeForm.get('email')?.hasError('email')" class="mt-1 text-xs text-red-600">Introduce un correo válido</p>
            </div>

            <!-- Teléfono -->
            <div>
              <label for="telefono" class="block text-sm font-medium text-gray-700 mb-1">Teléfono (opcional)</label>
              <input id="telefono" type="tel" formControlName="telefono"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="+34 600 000 000">
            </div>

            <!-- Restaurante -->
            <div>
              <label for="localId" class="block text-sm font-medium text-gray-700 mb-1">Asignar a Restaurante</label>
              <select id="localId" formControlName="localId"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md bg-white focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm">
                <option value="" disabled>Selecciona un restaurante</option>
                <option *ngFor="let local of locales" [value]="local.id">
                  {{ local.nombre }} - {{ local.direccion }}
                </option>
              </select>
              <p *ngIf="employeeForm.get('localId')?.touched && employeeForm.get('localId')?.hasError('required')" class="mt-1 text-xs text-red-600">Debes seleccionar un restaurante</p>
              <p *ngIf="loadingLocales" class="mt-1 text-xs text-gray-500">Cargando restaurantes...</p>
            </div>

            <!-- Contraseña -->
            <div>
              <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
              <input id="password" type="password" formControlName="password"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="••••••••">
              <p *ngIf="employeeForm.get('password')?.touched && employeeForm.get('password')?.hasError('minlength')" class="mt-1 text-xs text-red-600">La contraseña debe tener al menos 6 caracteres</p>
            </div>

            <!-- Confirmar Contraseña -->
            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">Confirmar Contraseña</label>
              <input id="confirmPassword" type="password" formControlName="confirmPassword"
                class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                placeholder="••••••••">
              <p *ngIf="employeeForm.errors?.['mismatch'] && employeeForm.get('confirmPassword')?.touched" class="mt-1 text-xs text-red-600">Las contraseñas no coinciden</p>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="pt-4">
            <button type="submit" 
              [disabled]="employeeForm.invalid || isLoading"
              class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-primary-500 hover:bg-primary-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed">
              <span *ngIf="isLoading" class="flex items-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Creando...
              </span>
              <span *ngIf="!isLoading">Crear Empleado</span>
            </button>
          </div>

          <!-- Back link -->
          <div class="text-center">
            <a routerLink="/admin" class="text-sm font-medium text-primary-600 hover:text-primary-500">
              ← Volver al panel de administración
            </a>
          </div>
        </form>
      </div>
    </div>
  `,
    styles: []
})
export class CreateEmployeeComponent implements OnInit {
    employeeForm: FormGroup;
    locales: Local[] = [];
    loadingLocales = true;
    isLoading = false;
    successMessage: string | null = null;
    errorMessage: string | null = null;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private localService: LocalService
    ) {
        this.employeeForm = this.fb.group({
            nombre: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            telefono: [''],
            localId: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', Validators.required]
        }, { validators: this.passwordMatchValidator });
    }

    ngOnInit(): void {
        this.loadLocales();
    }

    loadLocales(): void {
        this.loadingLocales = true;
        this.localService.getLocales().subscribe({
            next: (locales) => {
                this.locales = locales;
                this.loadingLocales = false;
            },
            error: (err) => {
                console.error('Error loading locales:', err);
                this.errorMessage = 'Error al cargar los restaurantes';
                this.loadingLocales = false;
            }
        });
    }

    passwordMatchValidator(form: FormGroup) {
        const password = form.get('password');
        const confirmPassword = form.get('confirmPassword');
        if (password && confirmPassword && password.value !== confirmPassword.value) {
            return { mismatch: true };
        }
        return null;
    }

    onSubmit(): void {
        if (this.employeeForm.invalid) return;

        this.isLoading = true;
        this.errorMessage = null;

        const employeeData = {
            nombre: this.employeeForm.get('nombre')?.value,
            email: this.employeeForm.get('email')?.value,
            telefono: this.employeeForm.get('telefono')?.value || null,
            localId: parseInt(this.employeeForm.get('localId')?.value),
            password: this.employeeForm.get('password')?.value
        };

        this.authService.createEmployee(employeeData).subscribe({
            next: (response) => {
                this.successMessage = typeof response === 'string' ? response : 'Empleado creado correctamente';
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error creating employee:', err);
                this.errorMessage = err.error || 'Error al crear el empleado';
                this.isLoading = false;
            }
        });
    }

    resetForm(): void {
        this.successMessage = null;
        this.errorMessage = null;
        this.employeeForm.reset();
    }
}
