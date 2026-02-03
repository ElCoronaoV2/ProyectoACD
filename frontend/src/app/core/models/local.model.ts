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
}
