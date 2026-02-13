
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocalService } from '../../../core/services/local.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-reviews-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <!-- Header -->
        <div class="p-4 border-b flex justify-between items-center bg-gray-50 rounded-t-lg">
          <h3 class="text-xl font-bold text-gray-800">Reseñas del Restaurante</h3>
          <button (click)="close()" class="text-gray-500 hover:text-gray-700 font-bold text-xl">&times;</button>
        </div>

        <!-- Content -->
        <div class="p-6 overflow-y-auto flex-1">
          <div *ngIf="loading" class="text-center py-8 text-gray-500">
            Cargando reseñas...
          </div>

          <div *ngIf="!loading && reviews.length === 0" class="text-center py-8 text-gray-500 bg-gray-50 rounded">
            No hay reseñas disponibles para este restaurante.
          </div>

          <div *ngIf="!loading && reviews.length > 0" class="space-y-4">
            <div *ngFor="let review of reviews" class="border rounded-lg p-4 hover:bg-gray-50 transition">
              <div class="flex justify-between items-start mb-2">
                <div>
                  <span class="font-bold text-gray-800">{{ review.usuarioNombre }}</span>
                  <div class="text-yellow-500 text-sm">
                    <span *ngFor="let s of [1,2,3,4,5]" [class.text-gray-300]="s > review.puntuacion">★</span>
                  </div>
                </div>
                <span class="text-xs text-gray-500">{{ review.fecha | date:'short' }}</span>
              </div>
              <p class="text-gray-600 text-sm mt-2 whitespace-pre-wrap">{{ review.comentario || 'Sin comentario' }}</p>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-4 border-t bg-gray-50 rounded-b-lg text-right">
          <button (click)="close()" class="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition">
            Cerrar
          </button>
        </div>
      </div>
    </div>
  `
})
export class ReviewsModalComponent implements OnInit {
  @Input() localId!: number;
  @Output() onClose = new EventEmitter<void>();

  reviews: any[] = [];
  loading = true;

  constructor(
    private localService: LocalService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
    if (this.localId) {
      this.loadReviews();
    }
  }

  loadReviews() {
    this.loading = true;
    this.localService.getReviews(this.localId).subscribe({
      next: (res) => {
        this.reviews = res;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError('Error al cargar reseñas');
      }
    });
  }

  close() {
    this.onClose.emit();
  }
}
