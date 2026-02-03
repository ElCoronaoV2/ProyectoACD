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
            args: '-jar tec/target/tec-0.0.1-SNAPSHOT.jar',
            cwd: '/home/proyectoacd/ProyectoACD',
            env: {
                SPRING_DATASOURCE_URL: 'jdbc:postgresql://localhost:5432/restaurant_tec',
                SPRING_DATASOURCE_USERNAME: 'admin',
                SPRING_DATASOURCE_PASSWORD: 'curso',
                SERVER_PORT: '8080'
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
