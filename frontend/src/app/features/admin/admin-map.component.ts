import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { LocalService } from '../../core/services/local.service';
import { Local, CreateLocalRequest } from '../../core/models/local.model';

@Component({
    selector: 'app-admin-map',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-map.component.html',
    styleUrls: ['./admin-map.component.css']
})
export class AdminMapComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('map') mapElement!: ElementRef;

    private map!: L.Map;
    private markers: Map<number, L.Marker> = new Map();
    private tempMarker: L.Marker | null = null;

    locales: Local[] = [];
    showForm = false;
    editMode = false;
    selectedLocal: Local | null = null;
    loading = false;
    message: { type: 'success' | 'error', text: string } | null = null;

    // Alicante center
    private alicanteCenter: L.LatLngExpression = [38.3452, -0.4810];
    private defaultZoom = 13;

    formData: CreateLocalRequest = this.getEmptyForm();

    // Custom icons
    private restaurantIcon = L.divIcon({
        html: '<div class="custom-marker">🍽️</div>',
        className: 'restaurant-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40]
    });

    private newPinIcon = L.divIcon({
        html: '<div class="custom-marker new">📍</div>',
        className: 'new-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40]
    });

    constructor(private localService: LocalService) { }

    ngOnInit(): void {
        this.loadLocales();
    }

    ngAfterViewInit(): void {
        this.initMap();
    }

    ngOnDestroy(): void {
        if (this.map) {
            this.map.remove();
        }
    }

    private initMap(): void {
        this.map = L.map(this.mapElement.nativeElement, {
            center: this.alicanteCenter,
            zoom: this.defaultZoom
        });

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors',
            maxZoom: 19
        }).addTo(this.map);

        // Click on map to add new restaurant
        this.map.on('click', (e: L.LeafletMouseEvent) => {
            if (!this.showForm) {
                this.onMapClick(e.latlng);
            }
        });
    }

    private getEmptyForm(): CreateLocalRequest {
        return {
            nombre: '',
            direccion: '',
            capacidad: 50,
            horario: '',
            latitud: 0,
            longitud: 0,
            imagenUrl: '',
            valoracion: 4.0
        };
    }

    loadLocales(): void {
        this.localService.getLocales().subscribe({
            next: (locales) => {
                this.locales = locales;
                this.addMarkersToMap();
            },
            error: (err) => {
                console.error('Error loading locales:', err);
                this.showMessage('error', 'Error al cargar los restaurantes');
            }
        });
    }

    private addMarkersToMap(): void {
        // Clear existing markers
        this.markers.forEach(marker => marker.remove());
        this.markers.clear();

        // Add markers for restaurants with coordinates
        this.locales.forEach(local => {
            if (local.latitud && local.longitud) {
                const marker = L.marker([local.latitud, local.longitud], {
                    icon: this.restaurantIcon,
                    title: local.nombre
                });

                marker.bindTooltip(local.nombre, {
                    permanent: false,
                    direction: 'top',
                    offset: [0, -35]
                });

                marker.on('click', () => this.onMarkerClick(local));
                marker.addTo(this.map);
                this.markers.set(local.id, marker);
            }
        });
    }

    onMapClick(latlng: L.LatLng): void {
        // Remove temp marker if exists
        if (this.tempMarker) {
            this.tempMarker.remove();
        }

        // Add temporary marker
        this.tempMarker = L.marker(latlng, {
            icon: this.newPinIcon
        }).addTo(this.map);

        // Open form with coordinates
        this.formData = this.getEmptyForm();
        this.formData.latitud = latlng.lat;
        this.formData.longitud = latlng.lng;
        this.editMode = false;
        this.selectedLocal = null;
        this.showForm = true;
    }

    onMarkerClick(local: Local): void {
        this.selectedLocal = local;
        this.editMode = true;
        this.formData = {
            nombre: local.nombre,
            direccion: local.direccion,
            capacidad: local.capacidad,
            horario: local.horario || '',
            latitud: local.latitud || 0,
            longitud: local.longitud || 0,
            imagenUrl: local.imagenUrl || '',
            valoracion: local.valoracion || 4.0
        };
        this.showForm = true;
    }

    closeForm(): void {
        this.showForm = false;
        this.selectedLocal = null;
        this.formData = this.getEmptyForm();

        // Remove temp marker
        if (this.tempMarker) {
            this.tempMarker.remove();
            this.tempMarker = null;
        }
    }

    onSubmit(): void {
        this.loading = true;

        if (this.editMode && this.selectedLocal) {
            this.localService.updateLocal(this.selectedLocal.id, this.formData).subscribe({
                next: () => {
                    this.showMessage('success', 'Restaurante actualizado correctamente');
                    this.loadLocales();
                    this.closeForm();
                    this.loading = false;
                },
                error: (err) => {
                    console.error('Error updating local:', err);
                    this.showMessage('error', 'Error al actualizar el restaurante');
                    this.loading = false;
                }
            });
        } else {
            this.localService.createLocal(this.formData).subscribe({
                next: () => {
                    this.showMessage('success', 'Restaurante creado correctamente');
                    this.loadLocales();
                    this.closeForm();
                    this.loading = false;
                },
                error: (err) => {
                    console.error('Error creating local:', err);
                    this.showMessage('error', 'Error al crear el restaurante');
                    this.loading = false;
                }
            });
        }
    }

    deleteLocal(): void {
        if (!this.selectedLocal) return;

        if (confirm(`¿Estás seguro de eliminar "${this.selectedLocal.nombre}"?`)) {
            this.loading = true;
            this.localService.deleteLocal(this.selectedLocal.id).subscribe({
                next: () => {
                    this.showMessage('success', 'Restaurante eliminado correctamente');
                    this.loadLocales();
                    this.closeForm();
                    this.loading = false;
                },
                error: (err) => {
                    console.error('Error deleting local:', err);
                    this.showMessage('error', 'Error al eliminar el restaurante');
                    this.loading = false;
                }
            });
        }
    }

    showMessage(type: 'success' | 'error', text: string): void {
        this.message = { type, text };
        setTimeout(() => {
            this.message = null;
        }, 3000);
    }

    centerOnAlicante(): void {
        this.map.setView(this.alicanteCenter, this.defaultZoom);
    }
}
