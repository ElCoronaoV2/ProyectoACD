export const environment = {
    production: true,
    apiUrl: 'https://www.restaurant-tec.es/api',
    // Stripe Public Key - Debe ser seteada en build time desde variable de entorno (pk_live_xxxxx)
    stripePublicKey: 'pk_live_' // Será reemplazada en build time
};
