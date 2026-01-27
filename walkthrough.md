# Estado del Despliegue - Restaurant-tec

Tu servidor está configurado y funcionando. Aquí tienes el resumen y los pasos finales.

## 🚀 Accesos

| Sitio | URL | Estado | Notas |
| :--- | :--- | :--- | :--- |
| **Frontend (WWW)** | [https://www.restaurant-tec.es](https://www.restaurant-tec.es) | ✅ **ONLINE (Seguro)** | Carga correctamente con HTTPS. |
| **Frontend (Raíz)** | `http://restaurant-tec.es` | ⏳ **Pendiente** | DNS apunta a IP antigua. |
| **API Backend** | `https://www.restaurant-tec.es/api/` | ✅ **ONLINE** | Accesible a través del proxy. |
| **n8n** | `http://n8n.n8nservidor.es` | ❌ **Error DNS** | DNS apunta a IP incorrecta (`95...`). |

## 🛠️ Configuración Realizada

1.  **Nginx como Proxy Central (Gateway)**:
    *   Gestiona `restaurant-tec.es` localmente.
    *   Reenvía `n8n.n8nservidor.es` a la MV interna `192.168.1.110`.
2.  **Seguridad**:
    *   Certificado SSL instalado para `www.restaurant-tec.es`.
    *   Clave SSH configurada para GitHub.
3.  **Código**:
    *   Repositorio subido a GitHub: [ElCoronaoV2/ProyectoACD](https://github.com/ElCoronaoV2/ProyectoACD).

## ⚠️ Pasos Pendientes (IMPORTANTES)

Para que todo funcione al 100%, necesitas corregir tus registros DNS en tu proveedor de dominio:

1.  **restaurant-tec.es (Raíz)**:
    *   Cambiar IP de `217.76.156.252` -> **`37.14.218.204`**
2.  **n8n.n8nservidor.es**:
    *   Cambiar IP de `95.17.229.81` -> **`37.14.218.204`**

Cuando hagas estos cambios y pasen unas horas, avísame para activar el SSL (candadito) en estos dominios también.
