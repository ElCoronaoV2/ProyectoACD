import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { LocalService } from '../../../core/services/local.service';
import { Local, Menu } from '../../../core/models/local.model';
import { RestaurantMenuModalComponent } from '../restaurant-menu-modal/restaurant-menu-modal.component';

// Fix for default marker icons in Leaflet with Angular
const iconRetinaUrl = 'assets/marker-icon-2x.png';
const iconUrl = 'assets/marker-icon.png';
const shadowUrl = 'assets/marker-shadow.png';

@Component({
    selector: 'app-restaurant-map',
    standalone: true,
    imports: [CommonModule, RestaurantMenuModalComponent],
    templateUrl: './restaurant-map.component.html',
    styleUrls: ['./restaurant-map.component.css']
})
export class RestaurantMapComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('map') mapElement!: ElementRef;

    private map!: L.Map;
    private markers: L.Marker[] = [];

    locales: Local[] = [];
    selectedLocal: Local | null = null;
    menuItems: Menu[] = [];
    showModal = false;
    loading = true;
    error: string | null = null;

    // Alicante center coordinates
    private alicanteCenter: L.LatLngExpression = [38.3452, -0.4810];
    private defaultZoom = 13;

    // Custom marker icon
    private restaurantIcon = L.divIcon({
        html: '<div class="custom-marker">🍽️</div>',
        className: 'restaurant-marker',
        iconSize: [40, 40],
        iconAnchor: [20, 40],
        popupAnchor: [0, -40]
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
            zoom: this.defaultZoom,
            zoomControl: true
        });

        // Add OpenStreetMap tiles
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 19
        }).addTo(this.map);

        // Add zoom control to top-right
        this.map.zoomControl.setPosition('topright');
    }

    loadLocales(): void {
        this.loading = true;
        this.localService.getLocales().subscribe({
            next: (locales) => {
                this.locales = locales.filter(l => l.latitud != null && l.longitud != null);
                this.loading = false;
                this.addMarkersToMap();
            },
            error: (err) => {
                console.error('Error loading locales:', err);
                this.error = 'Error al cargar los restaurantes';
                this.loading = false;
            }
        });
    }

    private addMarkersToMap(): void {
        // Clear existing markers
        this.markers.forEach(marker => marker.remove());
        this.markers = [];

        // Add markers for each restaurant
        this.locales.forEach(local => {
            if (local.latitud && local.longitud) {
                const marker = L.marker([local.latitud, local.longitud], {
                    icon: this.restaurantIcon,
                    title: local.nombre
                });

                // Create popup content
                const popupContent = this.createPopupContent(local);
                marker.bindPopup(popupContent, {
                    maxWidth: 300,
                    className: 'restaurant-popup'
                });

                // Handle popup open to attach click event
                marker.on('popupopen', () => {
                    const btn = document.getElementById(`menu-btn-${local.id}`);
                    if (btn) {
                        btn.addEventListener('click', () => this.openMenu(local));
                    }
                });

                marker.addTo(this.map);
                this.markers.push(marker);
            }
        });

        // Fit bounds if we have markers
        if (this.markers.length > 0) {
            const group = L.featureGroup(this.markers);
            this.map.fitBounds(group.getBounds().pad(0.1));
        }
    }

    private createPopupContent(local: Local): string {
        const rating = local.valoracion ? local.valoracion.toFixed(1) : 'N/A';
        const stars = this.getStarsHTML(local.valoracion);

        return `
            <div class="popup-content">
                ${local.imagenUrl ?
                `<img src="${local.imagenUrl}" alt="${local.nombre}" class="popup-image" />` :
                `<div class="popup-placeholder">🍽️</div>`
            }
                <div class="popup-info">
                    <h3>${local.nombre}</h3>
                    <div class="popup-rating">${stars} <span>(${rating})</span></div>
                    <p class="popup-address">📍 ${local.direccion}</p>
                    ${local.horario ? `<p class="popup-hours">🕐 ${local.horario}</p>` : ''}
                    <button id="menu-btn-${local.id}" class="popup-menu-btn">Ver Menú</button>
                </div>
            </div>
        `;
    }

    private getStarsHTML(rating: number | undefined): string {
        const r = rating || 0;
        const full = Math.floor(r);
        const half = r % 1 >= 0.5 ? 1 : 0;
        const empty = Math.max(0, 5 - full - half);

        return '★'.repeat(full) + (half ? '☆' : '') + '☆'.repeat(empty);
    }

    openMenu(local: Local): void {
        this.selectedLocal = local;
        this.showModal = true;
        this.loadMenus(local.id);
        this.map.closePopup();
    }

    loadMenus(localId: number): void {
        this.localService.getMenusByLocal(localId).subscribe({
            next: (menus) => {
                this.menuItems = menus;
            },
            error: (err) => {
                console.error('Error loading menus:', err);
                this.menuItems = [];
            }
        });
    }

    closeModal(): void {
        this.showModal = false;
        this.selectedLocal = null;
        this.menuItems = [];
    }

    // Center map on Alicante
    centerOnAlicante(): void {
        this.map.setView(this.alicanteCenter, this.defaultZoom);
    }
}
