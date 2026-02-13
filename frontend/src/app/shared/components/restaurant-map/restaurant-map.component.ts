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

    // Custom marker icon - Red pin for better visibility
    private restaurantIcon = L.divIcon({
        html: `<div class="custom-marker">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="36" height="36">
                <path fill="#DC2626" stroke="#991B1B" stroke-width="1" d="M12 0C7.58 0 4 3.58 4 8c0 5.5 8 14 8 14s8-8.5 8-14c0-4.42-3.58-8-8-8z"/>
                <circle fill="#FEE2E2" cx="12" cy="8" r="3"/>
            </svg>
        </div>`,
        className: 'restaurant-marker',
        iconSize: [36, 36],
        iconAnchor: [18, 36],
        popupAnchor: [0, -36]
    });

    userLocation: L.LatLng | null = null;
    userMarker: L.Marker | null = null;

    // Custom Blue User Icon
    private userIcon = L.divIcon({
        html: `<div class="custom-marker user-marker">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="36" height="36">
                <circle cx="12" cy="12" r="10" fill="#2563EB" stroke="#1E40AF" stroke-width="2"/>
                <circle cx="12" cy="12" r="4" fill="#FFFFFF"/>
            </svg>
        </div>`,
        className: 'user-position-marker',
        iconSize: [36, 36],
        iconAnchor: [18, 36],
        popupAnchor: [0, -36]
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
                    const menuBtn = document.getElementById(`menu-btn-${local.id}`);
                    if (menuBtn) {
                        menuBtn.addEventListener('click', () => this.openMenu(local));
                    }
                    const bookBtn = document.getElementById(`book-btn-${local.id}`);
                    if (bookBtn) {
                        bookBtn.addEventListener('click', () => this.openBooking(local));
                    }
                });

                marker.addTo(this.map);
                this.markers.push(marker);
            }
        });

        // Fit bounds if we have markers
        if (this.markers.length > 0 && !this.userLocation) {
            const group = L.featureGroup(this.markers);
            this.map.fitBounds(group.getBounds().pad(0.1));
        }
    }

    private createPopupContent(local: Local): string {
        const rating = local.valoracion ? local.valoracion.toFixed(1) : 'N/A';
        const stars = this.getStarsHTML(local.valoracion);

        let distanceHtml = '';
        if (local.distance !== undefined) {
            distanceHtml = `<p class="popup-distance">📏 A <strong>${local.distance.toFixed(1)} km</strong> de ti</p>`;
        }

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
                    ${distanceHtml}
                    ${local.horario ? `<p class="popup-hours">🕐 ${local.horario}</p>` : ''}
                    <div class="popup-actions">
                        <button id="menu-btn-${local.id}" class="popup-btn menu-btn">Ver Menú</button>
                        <button id="book-btn-${local.id}" class="popup-btn book-btn">Reservar</button>
                    </div>
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
        // Access modal directly via ViewChild would be better, but we pass data via props
        // Reset tab to menu
        if (this.modalComponent) this.modalComponent.activeTab = 'menu';
        this.showModal = true;
        this.loadMenus(local.id);
        this.map.closePopup();
    }

    openBooking(local: Local): void {
        this.selectedLocal = local;
        this.showModal = true;
        this.loadMenus(local.id);

        // Use timeout to ensure modal is rendered before accessing ViewChild logic if needed
        setTimeout(() => {
            if (this.modalComponent) this.modalComponent.activeTab = 'booking';
        }, 100);

        this.map.closePopup();
    }

    @ViewChild(RestaurantMenuModalComponent) modalComponent!: RestaurantMenuModalComponent;

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

    centerOnAlicante(): void {
        this.map.setView(this.alicanteCenter, this.defaultZoom);
    }

    // Geolocation Features
    locateUser(): void {
        if (!navigator.geolocation) {
            alert('Tu navegador no soporta geolocalización');
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                this.userLocation = new L.LatLng(lat, lng);

                // Add or update user marker
                if (this.userMarker) {
                    this.userMarker.setLatLng(this.userLocation);
                } else {
                    this.userMarker = L.marker(this.userLocation, {
                        icon: this.userIcon,
                        title: 'Tu ubicación'
                    }).addTo(this.map);

                    this.userMarker.bindPopup('<b>¡Estás aquí!</b>').openPopup();
                }

                // Center map
                this.map.setView(this.userLocation, 14);

                // Calculate distances immediately
                this.calculateDistances();
            },
            (error) => {
                console.error('Geolocation error:', error);
                alert('No pudimos obtener tu ubicación. Por favor, revisa tus permisos.');
            }
        );
    }

    calculateDistances(): void {
        if (!this.userLocation) {
            this.locateUser(); // Try to locate first
            return;
        }

        this.locales.forEach(local => {
            if (local.latitud && local.longitud) {
                const localLatLng = new L.LatLng(local.latitud, local.longitud);
                // Distance in meters / 1000 = km
                local.distance = this.userLocation!.distanceTo(localLatLng) / 1000;
            }
        });

        // Update markers to show new distance in popups
        this.addMarkersToMap();

        // Show nearest restaurants filter feedback (optional, maybe a toast)
    }

    sortByDistance(): void {
        if (!this.userLocation) {
            this.locateUser();
            return;
        }

        // First ensure distances are calculated
        this.calculateDistances();

        // Sort locales by distance
        this.locales.sort((a, b) => (a.distance ?? 9999) - (b.distance ?? 9999));

        // Re-add markers (to update logic if needed, though order on map doesn't change visually)
        // But we could auto-open the nearest one?
        if (this.locales.length > 0) {
            const nearest = this.locales[0];
            if (nearest.latitud && nearest.longitud) {
                this.map.flyTo([nearest.latitud, nearest.longitud], 16);

                // Find marker for nearest
                const nearestMarker = this.markers.find(m => {
                    const latLng = m.getLatLng();
                    return latLng.lat === nearest.latitud && latLng.lng === nearest.longitud;
                });
                if (nearestMarker) nearestMarker.openPopup();
            }
        }
    }
}
