import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Local, Menu } from '../../../core/models/local.model';
import { ReservationService } from '../../../core/services/reservation.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-restaurant-menu-modal',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './restaurant-menu-modal.component.html',
    styleUrls: ['./restaurant-menu-modal.component.css']
})
export class RestaurantMenuModalComponent implements OnInit {
    @Input() local!: Local;
    @Input() menus: Menu[] = [];
    @Output() close = new EventEmitter<void>();

    activeTab: 'menu' | 'booking' = 'menu';
    bookingData = {
        fecha: '',
        hora: '',
        personas: 2,
        observaciones: ''
    };

    constructor(
        public authService: AuthService,
        private reservationService: ReservationService
    ) { }

    ngOnInit() {
        // Set default date to tomorrow
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        this.bookingData.fecha = tomorrow.toISOString().split('T')[0];
        this.bookingData.hora = '20:00';
    }

    onClose(): void {
        this.close.emit();
    }

    onBackdropClick(event: MouseEvent): void {
        if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
            this.onClose();
        }
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

    formatAlergenos(alergenos: string | undefined): string[] {
        if (!alergenos) return [];
        return alergenos.split(',').map(a => a.trim());
    }

    makeReservation() {
        if (!this.authService.isLoggedIn()) {
            alert('Debes iniciar sesión para reservar.');
            return;
        }

        const dateTime = `${this.bookingData.fecha}T${this.bookingData.hora}:00`;
        const payload = {
            localId: this.local.id,
            fechaHora: dateTime,
            numeroPersonas: this.bookingData.personas,
            observaciones: this.bookingData.observaciones
        };

        this.reservationService.createReservation(payload).subscribe({
            next: () => {
                alert('¡Reserva realizada con éxito!');
                this.onClose();
            },
            error: (err) => {
                console.error(err);
                alert('Error al realizar la reserva.');
            }
        });
    }
}
