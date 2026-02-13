export interface Local {
    id: number;
    nombre: string;
    direccion: string;
    capacidad: number;
    horario?: string;
    latitud?: number;
    longitud?: number;
    posX?: number;
    posY?: number;
    imagenUrl?: string;
    valoracion?: number;
    distance?: number; // Distance in km from user

    // Shift Hours
    aperturaComida?: string;
    cierreComida?: string;
    aperturaCena?: string;
    cierreCena?: string;
}

export interface CreateLocalRequest {
    nombre: string;
    direccion: string;
    capacidad: number;
    horario?: string;
    latitud?: number;
    longitud?: number;
    posX?: number;
    posY?: number;
    imagenUrl?: string;
    valoracion?: number;
}

export interface Menu {
    id: number;
    nombre: string;
    descripcion?: string;
    precio: number;
    ingredientes?: string;
    alergenos?: string;
    disponible: boolean;
    localId: number;
    valoracionMedia?: number;

    // Estructura de platos
    primerPlato?: string;
    primerPlatoDesc?: string;
    primerPlatoIngredientes?: string;
    segundoPlato?: string;
    segundoPlatoDesc?: string;
    segundoPlatoIngredientes?: string;
    postre?: string;
    postreDesc?: string;
    postreIngredientes?: string;
}
