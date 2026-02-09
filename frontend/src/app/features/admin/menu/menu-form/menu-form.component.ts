import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { CreateMenuRequest, Menu } from '../../../../core/models/menu.model';
import { MenuService } from '../../../../core/services/menu.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
    selector: 'app-menu-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './menu-form.component.html',
    styleUrls: ['./menu-form.component.css']
})
export class MenuFormComponent {
    @Input() restaurantId!: number;
    @Output() onClose = new EventEmitter<void>();
    @Output() onSaved = new EventEmitter<void>();

    currentStep = 1;
    menuForm: FormGroup;
    loading = false;

    availableAllergens = [
        { name: 'Gluten', icon: '🌾' },
        { name: 'Crustáceos', icon: '🦐' },
        { name: 'Huevos', icon: '🥚' },
        { name: 'Pescado', icon: '🐟' },
        { name: 'Cacahuetes', icon: '🥜' },
        { name: 'Soja', icon: '🌱' },
        { name: 'Lácteos', icon: '🥛' },
        { name: 'Frutos de cáscara', icon: '🌰' },
        { name: 'Apio', icon: '🌿' },
        { name: 'Mostaza', icon: '🌭' },
        { name: 'Granos de sésamo', icon: '🥯' },
        { name: 'Sulfitos', icon: '🍷' },
        { name: 'Altramuces', icon: '🌼' },
        { name: 'Moluscos', icon: '🐙' }
    ];

    constructor(
        private fb: FormBuilder,
        private menuService: MenuService,
        private notificationService: NotificationService
    ) {
        this.menuForm = this.fb.group({
            nombre: ['', Validators.required],
            descripcion: [''],
            precio: [0, [Validators.required, Validators.min(0)]],
            disponible: [true],

            // Platos
            primerPlato: ['', Validators.required],
            primerPlatoDesc: [''],
            primerPlatoIngredientes: [''],

            segundoPlato: ['', Validators.required],
            segundoPlatoDesc: [''],
            segundoPlatoIngredientes: [''],

            postre: ['', Validators.required],
            postreDesc: [''],
            postreIngredientes: [''],

            // Alérgenos (array de strings seleccionados)
            alergenos: [[]]
        });
    }

    nextStep() {
        if (this.currentStep < 4) {
            this.currentStep++;
        }
    }

    prevStep() {
        if (this.currentStep > 1) {
            this.currentStep--;
        }
    }

    toggleAllergen(allergen: string) {
        const currentAllergens = this.menuForm.get('alergenos')?.value as string[];
        const index = currentAllergens.indexOf(allergen);

        if (index === -1) {
            currentAllergens.push(allergen);
        } else {
            currentAllergens.splice(index, 1);
        }
        this.menuForm.patchValue({ alergenos: currentAllergens });
    }

    isAllergenSelected(allergen: string): boolean {
        return (this.menuForm.get('alergenos')?.value as string[]).includes(allergen);
    }

    // AI Analysis
    analyzing = false;
    aiAnalyzed = false;
    progress = 0;
    progressMessage = '';
    private progressInterval: any;

    triggerAiAnalysis() {
        this.analyzing = true;
        this.aiAnalyzed = false;
        this.progress = 0;
        this.simulateProgress();

        // Gather all text from dishes
        const parts = [];
        const f = this.menuForm.value;
        parts.push(`MENU: ${f.nombre}`);
        if (f.descripcion) parts.push(`DESCRIPCION: ${f.descripcion}`);
        if (f.primerPlato) parts.push(`PRIMER PLATO: ${f.primerPlato}. INGREDIENTES: ${f.primerPlatoIngredientes}`);
        if (f.segundoPlato) parts.push(`SEGUNDO PLATO: ${f.segundoPlato}. INGREDIENTES: ${f.segundoPlatoIngredientes}`);
        if (f.postre) parts.push(`POSTRE: ${f.postre}. INGREDIENTES: ${f.postreIngredientes}`);

        const textToAnalyze = parts.join('\n');

        this.menuService.analyzeAllergens(textToAnalyze).subscribe({
            next: (detected) => {
                this.completeProgressAndShowResults(detected);
            },
            error: () => {
                this.stopProgress();
                this.analyzing = false;
                this.progressMessage = 'Error de conexión';
                this.notificationService.showError('Error al conectar con el servicio de IA');
            }
        });
    }

    simulateProgress() {
        this.progress = 0;
        this.progressMessage = 'Conectando con IA...';

        this.progressInterval = setInterval(() => {
            if (this.progress < 20) {
                this.progress += 2;
                this.progressMessage = 'Analizando ingredientes...';
            } else if (this.progress < 60) {
                this.progress += 1;
                this.progressMessage = 'Detectando alérgenos con IA...';
            } else if (this.progress < 85) {
                this.progress += 0.5;
                this.progressMessage = 'Procesando en servidor...';
            }
            // Cap at 85% to wait for real response
        }, 100);
    }

    completeProgressAndShowResults(detected: string[]) {
        clearInterval(this.progressInterval);
        this.progressMessage = 'Finalizando análisis...';

        // Animate from current progress to 100%
        const completionInterval = setInterval(() => {
            if (this.progress < 100) {
                this.progress += 2; // Fast completion
            } else {
                clearInterval(completionInterval);
                this.progress = 100;
                this.progressMessage = '¡Análisis completado!';

                // Wait for user to see 100% before showing results
                setTimeout(() => {
                    this.analyzing = false; // Hide progress bar
                    this.aiAnalyzed = true; // Show success message

                    // Mark allergens
                    // We only enable the ones detected, keeping existing ones if needed? 
                    // Usually AI replaces or adds. Let's replace for clarity as it's a "detection"
                    this.menuForm.patchValue({ alergenos: detected });

                    this.notificationService.showSuccess(`Se han detectado ${detected.length} alérgenos.`);
                }, 800);
            }
        }, 30);
    }

    stopProgress() {
        clearInterval(this.progressInterval);
        this.progress = 0;
    }

    submit() {
        if (this.menuForm.invalid) return;

        this.loading = true;
        const formVal = this.menuForm.value;

        if (!this.restaurantId) {
            console.error('❌ MenuForm: No restaurantId provided!');
            this.notificationService.showError('Error interno: Falta ID del restaurante');
            return;
        }

        const request: CreateMenuRequest = {
            ...formVal,
            localId: this.restaurantId,
            alergenos: formVal.alergenos.join(',') // Convert array to string
        };

        console.log('📤 Submitting Menu Request:', request);

        this.menuService.createMenu(request).subscribe({
            next: (menu) => {
                this.loading = false;
                // Optionally generate PDF automatically
                // this.menuService.generateMenuPdf(menu); 
                this.onSaved.emit();
                this.onClose.emit();
            },
            error: (err) => {
                console.error(err);
                this.loading = false;
                this.notificationService.showError('Error al guardar el menú');
            }
        });
    }
}
