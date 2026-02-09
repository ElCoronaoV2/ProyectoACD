export interface Menu {
    id: number;
    nombre: string;
    descripcion?: string;
    precio: number;
    ingredientes?: string;
    alergenos?: string;
    disponible: boolean;
    localId: number;

    // Campos extendidos para el Wizard
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

export interface CreateMenuRequest {
    nombre: string;
    descripcion?: string;
    precio: number;
    disponible: boolean;
    localId: number;
    alergenos?: string;

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
