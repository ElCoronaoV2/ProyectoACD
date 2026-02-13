import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Menu, CreateMenuRequest } from '../models/menu.model';
import { environment } from '../../../environments/environment';
import jsPDF from 'jspdf';

// Mapa de alérgenos con emojis y colores
const ALLERGEN_DATA: { [key: string]: { emoji: string; color: string; label: string } } = {
    'gluten': { emoji: '🌾', color: '#D4A574', label: 'Gluten' },
    'crustaceos': { emoji: '🦐', color: '#FF6B6B', label: 'Crustáceos' },
    'huevos': { emoji: '🥚', color: '#FFE66D', label: 'Huevos' },
    'pescado': { emoji: '🐟', color: '#4ECDC4', label: 'Pescado' },
    'cacahuetes': { emoji: '🥜', color: '#C9B896', label: 'Cacahuetes' },
    'soja': { emoji: '🫘', color: '#90BE6D', label: 'Soja' },
    'lacteos': { emoji: '🥛', color: '#F8F9FA', label: 'Lácteos' },
    'frutos de cascara': { emoji: '🌰', color: '#8B7355', label: 'Frutos cáscara' },
    'apio': { emoji: '🥬', color: '#52B788', label: 'Apio' },
    'mostaza': { emoji: '🟡', color: '#FFD93D', label: 'Mostaza' },
    'sesamo': { emoji: '⚪', color: '#E5E5E5', label: 'Sésamo' },
    'sulfitos': { emoji: '🍷', color: '#722F37', label: 'Sulfitos' },
    'altramuces': { emoji: '🌸', color: '#9B59B6', label: 'Altramuces' },
    'moluscos': { emoji: '🦪', color: '#95A5A6', label: 'Moluscos' }
};

@Injectable({
    providedIn: 'root'
})
export class MenuService {
    private readonly apiUrl = `${environment.apiUrl}/admin/menus`;
    private readonly publicApiUrl = `${environment.apiUrl}/menus`;

    constructor(private http: HttpClient) { }

    createMenu(menu: CreateMenuRequest): Observable<Menu> {
        return this.http.post<Menu>(this.apiUrl, menu);
    }

    updateMenu(id: number, menu: CreateMenuRequest): Observable<Menu> {
        return this.http.put<Menu>(`${this.apiUrl}/${id}`, menu);
    }

    deleteMenu(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    addReview(menuId: number, puntuacion: number, comentario: string): Observable<any> {
        return this.http.post(`${this.publicApiUrl}/${menuId}/resenas`, { puntuacion, comentario });
    }

    getMenuById(id: number): Observable<Menu> {
        return this.http.get<Menu>(`${this.apiUrl}/${id}`);
    }

    generateMenuPdf(menu: Menu, restaurantName?: string) {
        const doc = new jsPDF();
        const pageWidth = doc.internal.pageSize.getWidth();
        const pageHeight = doc.internal.pageSize.getHeight();
        const margin = 20;

        // ========== HEADER DECORATIVO ==========
        // Fondo degradado header (simulado con rectángulos)
        doc.setFillColor(139, 69, 19); // Marrón oscuro
        doc.rect(0, 0, pageWidth, 50, 'F');
        doc.setFillColor(160, 82, 45); // Marrón más claro
        doc.rect(0, 0, pageWidth, 45, 'F');
        doc.setFillColor(205, 133, 63); // Marrón dorado
        doc.rect(0, 0, pageWidth, 40, 'F');

        // Línea decorativa dorada
        doc.setDrawColor(218, 165, 32);
        doc.setLineWidth(2);
        doc.line(20, 48, pageWidth - 20, 48);

        // Nombre del restaurante
        if (restaurantName) {
            doc.setTextColor(255, 255, 255);
            doc.setFontSize(12);
            doc.setFont('helvetica', 'italic');
            doc.text(restaurantName, pageWidth / 2, 15, { align: 'center' });
        }

        // Título del menú
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(28);
        doc.setFont('helvetica', 'bold');
        doc.text(menu.nombre, pageWidth / 2, 32, { align: 'center' });

        // Precio destacado
        doc.setFontSize(18);
        doc.setFont('helvetica', 'normal');
        const priceText = `${menu.precio.toFixed(2)}€`;
        doc.text(priceText, pageWidth / 2, 44, { align: 'center' });

        // ========== DESCRIPCIÓN ==========
        let yPos = 60;

        if (menu.descripcion) {
            doc.setTextColor(100, 100, 100);
            doc.setFontSize(11);
            doc.setFont('helvetica', 'italic');
            const descLines = doc.splitTextToSize(menu.descripcion, pageWidth - 40);
            doc.text(descLines, pageWidth / 2, yPos, { align: 'center' });
            yPos += (descLines.length * 6) + 10;
        }

        // Línea separadora
        doc.setDrawColor(200, 200, 200);
        doc.setLineWidth(0.5);
        doc.line(margin, yPos, pageWidth - margin, yPos);
        yPos += 15;

        // ========== PLATOS ==========
        const addCourse = (icon: string, title: string, name?: string, desc?: string, ingredients?: string) => {
            if (!name) return;

            // Icono y título del plato
            doc.setFontSize(14);
            doc.setFont('helvetica', 'bold');
            doc.setTextColor(139, 69, 19); // Marrón
            doc.text(`${icon}  ${title}`, margin, yPos);
            yPos += 8;

            // Nombre del plato
            doc.setFontSize(13);
            doc.setFont('helvetica', 'normal');
            doc.setTextColor(40, 40, 40);
            doc.text(name, margin + 5, yPos);
            yPos += 6;

            // Descripción
            if (desc) {
                doc.setFontSize(10);
                doc.setTextColor(100, 100, 100);
                doc.setFont('helvetica', 'italic');
                const descLines = doc.splitTextToSize(desc, pageWidth - 50);
                doc.text(descLines, margin + 5, yPos);
                yPos += (descLines.length * 5) + 2;
            }

            // Ingredientes
            if (ingredients) {
                doc.setFontSize(9);
                doc.setTextColor(130, 130, 130);
                doc.setFont('helvetica', 'normal');
                const ingLines = doc.splitTextToSize(`Ingredientes: ${ingredients}`, pageWidth - 50);
                doc.text(ingLines, margin + 5, yPos);
                yPos += (ingLines.length * 4) + 5;
            }

            yPos += 8;
        };

        addCourse('🍽️', 'PRIMER PLATO', menu.primerPlato, menu.primerPlatoDesc, menu.primerPlatoIngredientes);
        addCourse('🥘', 'SEGUNDO PLATO', menu.segundoPlato, menu.segundoPlatoDesc, menu.segundoPlatoIngredientes);
        addCourse('🍰', 'POSTRE', menu.postre, menu.postreDesc, menu.postreIngredientes);

        // ========== SECCIÓN ALÉRGENOS ==========
        if (menu.alergenos) {
            // Verificar si necesitamos nueva página
            if (yPos > pageHeight - 60) {
                doc.addPage();
                yPos = 20;
            }

            yPos += 5;

            // Línea separadora
            doc.setDrawColor(200, 200, 200);
            doc.setLineWidth(0.5);
            doc.line(margin, yPos, pageWidth - margin, yPos);
            yPos += 12;

            // Título alérgenos con fondo
            doc.setFillColor(255, 243, 224); // Fondo naranja suave
            doc.roundedRect(margin, yPos - 6, pageWidth - (margin * 2), 10, 2, 2, 'F');

            doc.setFontSize(11);
            doc.setFont('helvetica', 'bold');
            doc.setTextColor(200, 80, 0);
            doc.text('⚠️  INFORMACIÓN DE ALÉRGENOS', pageWidth / 2, yPos, { align: 'center' });
            yPos += 12;

            // Parsear y mostrar alérgenos con iconos
            const allergenList = menu.alergenos.split(',').map(a => a.trim().toLowerCase());
            let xPos = margin;
            const allergenBoxWidth = 50;
            const allergenBoxHeight = 18;
            const maxPerRow = Math.floor((pageWidth - margin * 2) / (allergenBoxWidth + 5));
            let currentRow = 0;

            allergenList.forEach((allergen, index) => {
                if (index > 0 && index % maxPerRow === 0) {
                    currentRow++;
                    xPos = margin;
                }

                const allergenInfo = this.getAllergenInfo(allergen);
                const boxY = yPos + (currentRow * (allergenBoxHeight + 5));

                // Caja del alérgeno con color
                doc.setFillColor(...this.hexToRgb(allergenInfo.color));
                doc.roundedRect(xPos, boxY, allergenBoxWidth, allergenBoxHeight, 3, 3, 'F');

                // Borde
                doc.setDrawColor(180, 180, 180);
                doc.setLineWidth(0.3);
                doc.roundedRect(xPos, boxY, allergenBoxWidth, allergenBoxHeight, 3, 3, 'S');

                // Emoji
                doc.setFontSize(12);
                doc.text(allergenInfo.emoji, xPos + 5, boxY + 12);

                // Nombre del alérgeno
                doc.setFontSize(8);
                doc.setFont('helvetica', 'bold');
                doc.setTextColor(60, 60, 60);
                doc.text(allergenInfo.label, xPos + 18, boxY + 12);

                xPos += allergenBoxWidth + 5;
            });

            yPos += (currentRow + 1) * (allergenBoxHeight + 5) + 10;
        }

        // ========== FOOTER ==========
        const footerY = pageHeight - 15;

        // Línea decorativa
        doc.setDrawColor(218, 165, 32);
        doc.setLineWidth(1);
        doc.line(margin, footerY - 5, pageWidth - margin, footerY - 5);

        // Texto footer
        doc.setFontSize(8);
        doc.setTextColor(150, 150, 150);
        doc.setFont('helvetica', 'italic');
        doc.text('Consulte con nuestro personal para más información sobre alérgenos e ingredientes.', pageWidth / 2, footerY, { align: 'center' });

        // Fecha de generación
        const today = new Date().toLocaleDateString('es-ES');
        doc.setFontSize(7);
        doc.text(`Generado: ${today}`, pageWidth - margin, footerY + 5, { align: 'right' });

        // ========== GUARDAR ==========
        doc.save(`Menu_${menu.nombre.replace(/\s+/g, '_')}.pdf`);
    }

    private getAllergenInfo(allergen: string): { emoji: string; color: string; label: string } {
        // Buscar coincidencia parcial
        const normalized = allergen.toLowerCase().trim();

        for (const [key, value] of Object.entries(ALLERGEN_DATA)) {
            if (normalized.includes(key) || key.includes(normalized)) {
                return value;
            }
        }

        // Default para alérgenos no reconocidos
        return { emoji: '⚠️', color: '#FFE4B5', label: allergen.charAt(0).toUpperCase() + allergen.slice(1) };
    }

    private hexToRgb(hex: string): [number, number, number] {
        const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        if (result) {
            return [parseInt(result[1], 16), parseInt(result[2], 16), parseInt(result[3], 16)];
        }
        return [255, 228, 181]; // Fallback: color melocotón
    }

    // IA para detectar alérgenos (Backend -> Remote Ollama)
    analyzeAllergens(text: string): Observable<string[]> {
        return this.http.post<string[]>('https://www.restaurant-tec.es/api/admin/ai/analyze', { text });
    }
}
