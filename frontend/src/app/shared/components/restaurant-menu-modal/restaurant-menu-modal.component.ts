import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Local, Menu } from '../../../core/models/local.model';

@Component({
    selector: 'app-restaurant-menu-modal',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './restaurant-menu-modal.component.html',
    styleUrls: ['./restaurant-menu-modal.component.css']
})
export class RestaurantMenuModalComponent {
    @Input() local!: Local;
    @Input() menus: Menu[] = [];
    @Output() close = new EventEmitter<void>();

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
}
