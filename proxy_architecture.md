# Arquitectura Multi-Dominio con una sola IP Pública

El problema es que un router solo puede enviar el tráfico del puerto 80 (Web) a **una sola IP interna**. No puede "adivinar" a qué máquina enviarlo basándose en el nombre; eso lo tiene que hacer un servidor web (Nginx).

Para tener dos dominios (`restaurant-tec.es` y `tu-otro-dominio.com`) en dos máquinas virtuales diferentes, necesitas configurar una de ellas como **Proxy Inverso Principal**.

## Diagrama de Solución

```mermaid
graph TD
    User((Usuario Internet))
    Router[Router / IP Pública]
    
    subgraph "Servidor ESXi (Tu Red Interna)"
        Proxy[VM Nginx "Portero"]
        VM1[VM 1: Web Antigua]
        VM2[VM 2: Restaurant-tec]
    end

    User -->|http/https| Router
    Router -->|Puerto 80/443| Proxy
    
    Proxy -->|Si piden 'vieja-web.com'| VM1
    Proxy -->|Si piden 'restaurant-tec.es'| VM2
```

## Opciones

Tienes dos opciones para implementar esto:

### Opción A: Usar ESTA máquina (VM Restaurant) como Principal
Configuramos el Nginx que ya tenemos aquí para que maneje **todo**.
1. En el router: Apuntas puertos 80/443 a esta máquina (`192.168.1.142`).
2. En Nginx (esta máquina):
   - Configuramos `restaurant-tec.es` para servirse localmente (¡Ya hecho!).
   - Configuramos `tu-otro-dominio.com` para reenviarse a la otra VM.

### Opción B: Usar la OTRA máquina (VM Antigua) como Principal
Si no quieres tocar el router, tienes que configurar el Nginx (o Apache) de tu otra máquina.
1. Router: Se queda igual (apuntando a la otra VM).
2. En la otra máquina:
   - Añades una configuración para que cuando pidan `restaurant-tec.es`, reenvíe el tráfico a `192.168.1.142` (esta máquina).

## ¿Qué necesito para configurarlo?

Si eliges la **Opción A** (recomendada si esta máquina es más nueva/potente), necesito que me digas:
1. El **nombre de dominio** de la otra web.
2. La **IP interna** de la otra máquina virtual.
