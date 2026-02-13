// Load .env file
const fs = require('fs');
const path = require('path');

function loadEnv() {
    const envPath = path.join(__dirname, '.env');
    if (fs.existsSync(envPath)) {
        const envContent = fs.readFileSync(envPath, 'utf-8');
        envContent.split('\n').forEach(line => {
            const trimmed = line.trim();
            if (trimmed && !trimmed.startsWith('#')) {
                const [key, ...valueParts] = trimmed.split('=');
                let value = valueParts.join('=');
                // Remove quotes if present
                if ((value.startsWith('"') && value.endsWith('"')) || 
                    (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.slice(1, -1);
                }
                process.env[key.trim()] = value.trim();
            }
        });
    }
}

loadEnv();

module.exports = {
    apps: [
        {
            name: 'restaurant-db',
            script: 'docker',
            args: 'compose up db',
            cwd: '/home/proyectoacd/ProyectoACD',
            autorestart: false,
        },
        {
            name: 'restaurant-backend',
            script: 'java',
            args: '-jar /home/proyectoacd/ProyectoACD/tec/target/tec-0.0.1-SNAPSHOT.jar',
            cwd: '/home/proyectoacd/ProyectoACD',
            env: {
                SPRING_DATASOURCE_URL: process.env.SPRING_DATASOURCE_URL || 'jdbc:postgresql://localhost:5432/restaurant_tec',
                SPRING_DATASOURCE_USERNAME: process.env.POSTGRES_USER || 'admin',
                SPRING_DATASOURCE_PASSWORD: process.env.DB_PASSWORD || 'curso',
                POSTGRES_PASSWORD: process.env.DB_PASSWORD || 'curso',
                SERVER_PORT: '8080',
                OLLAMA_URL: process.env.OLLAMA_URL || 'http://192.168.1.83:11434/api/generate',
                OLLAMA_MODEL: process.env.OLLAMA_MODEL || 'llama3.1:8b',
                STRIPE_SECRET_KEY: process.env.STRIPE_SECRET_KEY || 'sk_test_placeholder',
                JWT_SECRET: process.env.JWT_SECRET || 'cambiar-en-produccion',
                JWT_EXPIRATION_MILLISECONDS: process.env.JWT_EXPIRATION_MILLISECONDS || '86400000',
                MAIL_USERNAME: process.env.MAIL_USERNAME || 'cambiar-email@gmail.com',
                MAIL_PASSWORD: process.env.MAIL_PASSWORD || 'cambiar-password'
            }
        },
        {
            name: 'restaurant-frontend',
            script: 'npx',
            args: 'serve -s ./frontend/dist/frontend/browser -p 3000',
            cwd: '/home/proyectoacd/ProyectoACD'
        }
    ]
};
