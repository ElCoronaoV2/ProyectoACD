import { Component, Input, Output, EventEmitter, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Local, Menu } from '../../../core/models/local.model';
import { ReservationService } from '../../../core/services/reservation.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { MenuService } from '../../../core/services/menu.service';
import { StripeService, StripePaymentElementComponent } from 'ngx-stripe';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
    selector: 'app-restaurant-menu-modal',
    standalone: true,
    imports: [CommonModule, FormsModule, StripePaymentElementComponent],
    templateUrl: './restaurant-menu-modal.component.html',
    styleUrls: ['./restaurant-menu-modal.component.css']
})
export class RestaurantMenuModalComponent implements OnInit {
    @Input() local!: Local;
    @Input() menus: Menu[] = [];
    @Output() close = new EventEmitter<void>();

    activeTab: 'menu' | 'booking' = 'menu';

    // Reservation data
    bookingData = {
        fecha: '',
        hora: '',
        personas: 2,
        observaciones: '',
        nombreInvitado: '',
        emailInvitado: '',
        telefonoInvitado: ''
    };

    // Review data
    reviewData: { [menuId: number]: { rating: number; comment: string } } = {};
    showReviewForm: { [menuId: number]: boolean } = {};

    // Selected menu for reservation
    selectedMenu: Menu | null = null;

    // Stripe
    @ViewChild(StripePaymentElementComponent) paymentElement!: StripePaymentElementComponent;
    stripe = inject(StripeService);
    http = inject(HttpClient); // Inject HttpClient directly for payment intent

    elementsOptions: any = {
        locale: 'es',
        appearance: {
            theme: 'stripe'
        }
    };
    paymentElementOptions: any = {
        layout: {
            type: 'tabs',
            defaultCollapsed: false,
        }
    };

    isProcessingPayment = false;
    showPaymentStep = false;
    currentClientSecret: string | null = null;

    // Lightbox para imagen
    showImageLightbox = false;

    // Restaurant review
    showRestaurantReviewForm = false;
    restaurantReviewData = { rating: 0, comment: '' };
    restaurantHoverRating = 0;
    menuHoverRating: { [menuId: number]: number } = {};

    constructor(
        public authService: AuthService,
        private reservationService: ReservationService,
        private notificationService: NotificationService,
        private menuService: MenuService
    ) { }

    availableSeats: number | null = null;
    selectedShift: 'lunch' | 'dinner' | null = null;
    availableTimeSlots: string[] = [];

    ngOnInit() {
        // Set default date to tomorrow
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        this.bookingData.fecha = tomorrow.toISOString().split('T')[0];

        // Initialize review data structures
        if (this.menus) {
            this.menus.forEach(menu => {
                this.reviewData[menu.id] = { rating: 5, comment: '' };
                this.showReviewForm[menu.id] = false;
            });
        }
    }

    onClose(): void {
        this.close.emit();
    }

    openImageLightbox(event: MouseEvent): void {
        event.stopPropagation();
        event.preventDefault();
        this.showImageLightbox = true;
    }

    closeImageLightbox(): void {
        this.showImageLightbox = false;
    }

    onLightboxBackdropClick(event: MouseEvent): void {
        if ((event.target as HTMLElement).classList.contains('lightbox-backdrop')) {
            this.closeImageLightbox();
        }
    }

    onBackdropClick(event: MouseEvent): void {
        if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
            this.onClose();
        }
    }

    selectShift(shift: 'lunch' | 'dinner') {
        this.selectedShift = shift;
        this.bookingData.hora = '';
        this.availableSeats = null;
        this.generateTimeSlots(shift);
    }

    generateTimeSlots(shift: 'lunch' | 'dinner') {
        this.availableTimeSlots = [];
        let start = shift === 'lunch' ? (this.local.aperturaComida || '13:00') : (this.local.aperturaCena || '20:00');
        let end = shift === 'lunch' ? (this.local.cierreComida || '16:00') : (this.local.cierreCena || '23:30');

        // Parse HH:mm
        const [startH, startM] = start.split(':').map(Number);
        const [endH, endM] = end.split(':').map(Number);

        let currentH = startH;
        let currentM = startM;

        while (currentH < endH || (currentH === endH && currentM < endM)) {
            const timeStr = `${currentH.toString().padStart(2, '0')}:${currentM.toString().padStart(2, '0')}`;
            this.availableTimeSlots.push(timeStr);

            // Increment 30 mins
            currentM += 30;
            if (currentM >= 60) {
                currentM -= 60;
                currentH++;
            }
        }
    }

    selectTime(time: string) {
        this.bookingData.hora = time;
        this.checkAvailability();
    }

    checkAvailability() {
        if (!this.bookingData.fecha || !this.bookingData.hora) return;

        const dateTime = `${this.bookingData.fecha}T${this.bookingData.hora}:00`;
        this.reservationService.checkAvailability(this.local.id, dateTime).subscribe({
            next: (seats) => {
                this.availableSeats = seats;
            },
            error: (err) => console.error('Error checking availability', err)
        });
    }

    getStars(rating: number | undefined): string[] {
        const r = rating || 0;
        const full = Math.floor(r);
        const half = r % 1 >= 0.5 ? 1 : 0;
        const empty = 5 - full - half;
        return [
            ...Array(full).fill('★'),
            ...Array(half).fill('☆'),
            ...Array(empty).fill('☆')
        ];
    }

    toggleReviewForm(menuId: number) {
        this.showReviewForm[menuId] = !this.showReviewForm[menuId];
        if (this.showReviewForm[menuId] && !this.reviewData[menuId]) {
            this.reviewData[menuId] = { rating: 0, comment: '' };
        }
    }

    setRating(menuId: number, rating: number) {
        if (!this.reviewData[menuId]) this.reviewData[menuId] = { rating: 0, comment: '' };
        this.reviewData[menuId].rating = rating;
    }

    getReviewRating(menuId: number): number {
        return this.reviewData[menuId]?.rating || 0;
    }

    submitReview(menuId: number) {
        if (!this.authService.isLoggedIn()) {
            this.notificationService.showWarning('Debes iniciar sesión para valorar.');
            return;
        }

        const data = this.reviewData[menuId];
        this.menuService.addReview(menuId, data.rating, data.comment).subscribe({
            next: () => {
                this.notificationService.showSuccess('¡Valoración enviada! Gracias por tu opinión.');
                this.showReviewForm[menuId] = false;
                // Opcional: Recargar menú o actualizar media localmente (simple aproximación)
            },
            error: (err) => {
                console.error(err);
                if (err.error?.message) {
                    this.notificationService.showError('Error: ' + err.error.message);
                } else {
                    this.notificationService.showError('Error al enviar la valoración.');
                }
            }
        });
    }

    // Restaurant review methods
    toggleRestaurantReviewForm(): void {
        this.showRestaurantReviewForm = !this.showRestaurantReviewForm;
        if (this.showRestaurantReviewForm) {
            this.restaurantReviewData = { rating: 0, comment: '' };
        }
    }

    setRestaurantRating(rating: number): void {
        this.restaurantReviewData.rating = rating;
    }

    submitRestaurantReview(): void {
        if (!this.authService.isLoggedIn()) {
            this.notificationService.showWarning('Debes iniciar sesión para valorar.');
            return;
        }

        if (this.restaurantReviewData.rating === 0) {
            this.notificationService.showWarning('Por favor, selecciona una puntuación.');
            return;
        }

        // Enviar valoración al backend
        this.http.post(`https://www.restaurant-tec.es/api/locales/${this.local.id}/reviews`, {
            rating: this.restaurantReviewData.rating,
            comment: this.restaurantReviewData.comment
        }).subscribe({
            next: () => {
                this.notificationService.showSuccess('¡Gracias por valorar el restaurante!');
                this.showRestaurantReviewForm = false;
                this.restaurantReviewData = { rating: 0, comment: '' };
            },
            error: (err) => {
                console.error(err);
                if (err.error?.message) {
                    this.notificationService.showError('Error: ' + err.error.message);
                } else {
                    this.notificationService.showError('Error al enviar la valoración.');
                }
            }
        });
    }

    // Hover methods for Restaurant
    setRestaurantHover(rating: number) {
        this.restaurantHoverRating = rating;
    }

    clearRestaurantHover() {
        this.restaurantHoverRating = 0;
    }

    // Hover methods for Menu
    setMenuHover(menuId: number, rating: number) {
        this.menuHoverRating[menuId] = rating;
    }

    clearMenuHover(menuId: number) {
        if (this.menuHoverRating[menuId]) {
            delete this.menuHoverRating[menuId];
        }
    }


    formatAlergenos(alergenos: string | undefined): string[] {
        if (!alergenos) return [];
        return alergenos.split(',').map(a => a.trim());
    }

    // Mapa de alérgenos con emojis
    private allergenMap: { [key: string]: { emoji: string; class: string } } = {
        'gluten': { emoji: '🌾', class: 'allergen-gluten' },
        'crustaceos': { emoji: '🦐', class: 'allergen-crustaceos' },
        'huevos': { emoji: '🥚', class: 'allergen-huevos' },
        'pescado': { emoji: '🐟', class: 'allergen-pescado' },
        'cacahuetes': { emoji: '🥜', class: 'allergen-cacahuetes' },
        'soja': { emoji: '🫘', class: 'allergen-soja' },
        'lacteos': { emoji: '🥛', class: 'allergen-lacteos' },
        'frutos de cascara': { emoji: '🌰', class: 'allergen-frutos' },
        'apio': { emoji: '🥬', class: 'allergen-apio' },
        'mostaza': { emoji: '🟡', class: 'allergen-mostaza' },
        'sesamo': { emoji: '⚪', class: 'allergen-sesamo' },
        'sulfitos': { emoji: '🍷', class: 'allergen-sulfitos' },
        'altramuces': { emoji: '🌸', class: 'allergen-altramuces' },
        'moluscos': { emoji: '🦪', class: 'allergen-moluscos' }
    };

    getAllergenEmoji(allergen: string): string {
        const normalized = allergen.toLowerCase().trim();
        for (const [key, value] of Object.entries(this.allergenMap)) {
            if (normalized.includes(key) || key.includes(normalized)) {
                return value.emoji;
            }
        }
        return '⚠️';
    }

    getAllergenClass(allergen: string): string {
        const normalized = allergen.toLowerCase().trim();
        for (const [key, value] of Object.entries(this.allergenMap)) {
            if (normalized.includes(key) || key.includes(normalized)) {
                return value.class;
            }
        }
        return 'allergen-default';
    }

    // Comprueba si el usuario logueado es alérgico a este alérgeno
    isUserAllergic(allergen: string): boolean {
        const user = this.authService.getCurrentUser();
        if (!user || !user.alergenos) return false;

        const userAllergens = user.alergenos.toLowerCase().split(',').map((a: string) => a.trim());
        const normalizedAllergen = allergen.toLowerCase().trim();

        return userAllergens.some((ua: string) =>
            ua.includes(normalizedAllergen) || normalizedAllergen.includes(ua)
        );
    }

    // Comprueba si hay coincidencia entre alérgenos del menú y los del usuario
    hasUserAllergenMatch(menuAlergenos: string): boolean {
        if (!menuAlergenos) return false;
        const allergens = this.formatAlergenos(menuAlergenos);
        return allergens.some(a => this.isUserAllergic(a));
    }

    selectMenu(menu: Menu): void {
        if (this.selectedMenu?.id === menu.id) {
            this.selectedMenu = null; // Deselect if clicking same menu
        } else {
            this.selectedMenu = menu;
            this.notificationService.showSuccess(`Menú "${menu.nombre}" seleccionado`);
        }
    }

    goToBookingWithMenu(): void {
        this.activeTab = 'booking';
    }

    get isValidReservation(): boolean {
        return !!(this.bookingData.fecha &&
            this.bookingData.hora &&
            this.bookingData.personas > 0);
    }

    // Validación de formato de email
    private isValidEmail(email: string): boolean {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email.trim());
    }

    // Validación de formato de teléfono (mínimo 9 dígitos, solo números)
    private isValidPhone(phone: string): boolean {
        const cleanPhone = phone.replace(/[\s\-\(\)]/g, ''); // Elimina espacios, guiones, paréntesis
        const phoneRegex = /^\+?[0-9]{9,15}$/;
        return phoneRegex.test(cleanPhone);
    }

    // Validación de nombre (mínimo 2 caracteres, solo letras y espacios)
    private isValidName(name: string): boolean {
        const trimmedName = name.trim();
        return trimmedName.length >= 2 && /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/.test(trimmedName);
    }

    // Método principal de validación de datos del invitado
    validateGuestData(): { valid: boolean; errors: string[] } {
        const errors: string[] = [];

        if (!this.authService.isLoggedIn()) {
            // Validar nombre
            if (!this.bookingData.nombreInvitado || !this.bookingData.nombreInvitado.trim()) {
                errors.push('El nombre es obligatorio');
            } else if (!this.isValidName(this.bookingData.nombreInvitado)) {
                errors.push('El nombre debe tener al menos 2 caracteres y solo letras');
            }

            // Validar email
            if (!this.bookingData.emailInvitado || !this.bookingData.emailInvitado.trim()) {
                errors.push('El email es obligatorio');
            } else if (!this.isValidEmail(this.bookingData.emailInvitado)) {
                errors.push('El formato del email no es válido (ej: correo@ejemplo.com)');
            }

            // Validar teléfono
            if (!this.bookingData.telefonoInvitado || !this.bookingData.telefonoInvitado.trim()) {
                errors.push('El teléfono es obligatorio');
            } else if (!this.isValidPhone(this.bookingData.telefonoInvitado)) {
                errors.push('El teléfono debe tener entre 9 y 15 dígitos');
            }
        }

        return { valid: errors.length === 0, errors };
    }

    initiateReservationProcess() {
        if (this.availableSeats !== null && this.bookingData.personas > this.availableSeats) {
            this.notificationService.showWarning(`Solo quedan ${this.availableSeats} plazas disponibles.`);
            return;
        }

        // Validate basic fields
        if (!this.isValidReservation) {
            let missing = [];
            if (!this.bookingData.fecha) missing.push('Fecha');
            if (!this.bookingData.hora) missing.push('Hora');
            if (!this.bookingData.personas || this.bookingData.personas < 1) missing.push('Personas');

            this.notificationService.showWarning(`Por favor completa: ${missing.join(', ')}`);
            return;
        }

        // Validate guest fields with format validation if not logged in
        const guestValidation = this.validateGuestData();
        if (!guestValidation.valid) {
            // Mostrar cada error en una notificación separada o todos juntos
            guestValidation.errors.forEach(error => {
                this.notificationService.showWarning(error);
            });
            return;
        }

        // 1. Create Payment Intent
        this.isProcessingPayment = true;
        this.http.post<{ clientSecret: string }>(`${environment.apiUrl}/reservas/create-payment-intent`, {})
            .subscribe({
                next: (res) => {
                    this.currentClientSecret = res.clientSecret;
                    this.elementsOptions.clientSecret = res.clientSecret;
                    this.showPaymentStep = true;
                    this.isProcessingPayment = false;
                },
                error: (err) => {
                    console.error('Error creating payment intent', err);
                    this.notificationService.showError('Error al iniciar el pago.');
                    this.isProcessingPayment = false;
                }
            });
    }

    confirmPaymentAndReserve() {
        if (!this.currentClientSecret || this.isProcessingPayment) return;

        this.isProcessingPayment = true;

        this.stripe.confirmPayment({
            elements: this.paymentElement.elements,
            confirmParams: {
                payment_method_data: {
                    billing_details: {
                        name: this.authService.isLoggedIn() ? this.authService.getCurrentUser()?.nombre ?? '' : this.bookingData.nombreInvitado,
                        email: this.authService.isLoggedIn() ? this.authService.getCurrentUser()?.email ?? '' : this.bookingData.emailInvitado
                    }
                }
            },
            redirect: 'if_required'
        }).subscribe({
            next: (result) => {
                if (result.error) {
                    this.notificationService.showError(result.error.message || 'Error en el pago');
                    this.isProcessingPayment = false;
                } else if (result.paymentIntent && result.paymentIntent.status === 'succeeded') {
                    // Payment successful, create reservation
                    this.finalizeReservation(result.paymentIntent.id);
                }
            },
            error: (err) => {
                console.error(err);
                this.notificationService.showError('Error al procesar el pago');
                this.isProcessingPayment = false;
            }
        });
    }

    finalizeReservation(paymentIntentId: string) {
        const dateTime = `${this.bookingData.fecha}T${this.bookingData.hora}:00`;

        if (this.authService.isLoggedIn()) {
            const payload: any = {
                localId: this.local.id,
                fechaHora: dateTime,
                numeroPersonas: this.bookingData.personas,
                observaciones: this.bookingData.observaciones,
                paymentIntentId: paymentIntentId
            };

            // Include selected menu if any
            if (this.selectedMenu) {
                payload.menuId = this.selectedMenu.id;
            }

            this.reservationService.createReservation(payload).subscribe({
                next: () => this.handleReservationSuccess(),
                error: (err) => {
                    this.handleReservationError(err);
                    this.isProcessingPayment = false;
                }
            });
        } else {
            const guestPayload: any = {
                localId: this.local.id,
                fechaHora: dateTime,
                numeroPersonas: this.bookingData.personas,
                observaciones: this.bookingData.observaciones,
                nombre: this.bookingData.nombreInvitado,
                email: this.bookingData.emailInvitado,
                telefono: this.bookingData.telefonoInvitado,
                paymentIntentId: paymentIntentId
            };

            // Include selected menu if any
            if (this.selectedMenu) {
                guestPayload.menuId = this.selectedMenu.id;
            }

            this.reservationService.createGuestReservation(guestPayload).subscribe({
                next: () => this.handleReservationSuccess(),
                error: (err) => {
                    this.handleReservationError(err);
                    this.isProcessingPayment = false;
                }
            });
        }
    }

    private handleReservationSuccess() {
        this.notificationService.showSuccess('¡Reserva realizada con éxito!');
        this.isProcessingPayment = false;
        this.showPaymentStep = false;
        this.onClose();
        // Reset form
        this.bookingData.nombreInvitado = '';
        this.bookingData.emailInvitado = '';
        this.bookingData.telefonoInvitado = '';
    }

    private handleReservationError(err: any) {
        console.error(err);
        if (err.error?.message) {
            this.notificationService.showError('Error: ' + err.error.message);
        } else {
            this.notificationService.showError('Error al realizar la reserva.');
        }
    }
}
