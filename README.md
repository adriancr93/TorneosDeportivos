# TorneosDeportivos

Proyecto de gestion de torneos deportivos con:

- Backend Java (Maven).
- Persistencia en MongoDB.
- Menu de consola.
- Vistas JavaFX (login, panel de creacion de torneos, dashboard).
- API REST local con Swagger/OpenAPI.
- Frontend separado en React + Vite.
- Integracion de logs con Elasticsearch + Logstash + Kibana.

## 1. Requisitos

- Java JDK 25.
- Maven 3.9+.
- Node.js 20+ (recomendado 22 LTS).
- npm 10+.
- Docker y Docker Compose (para Elastic Stack).
- Conexion a internet (MongoDB Atlas y CDN de Swagger UI).

## 2. Estructura principal

- Backend Java: [src/main/java/org/example](src/main/java/org/example)
- Clase principal: [src/main/java/org/example/Main.java](src/main/java/org/example/Main.java)
- API REST: [src/main/java/org/example/util/ApiServer.java](src/main/java/org/example/util/ApiServer.java)
- Conexion MongoDB: [src/main/java/org/example/config/MongoDBUtil.java](src/main/java/org/example/config/MongoDBUtil.java)
- Frontend React: [frontend](frontend)
- Elastic Stack: [containers](containers)

## 3. Configuracion de base de datos (MongoDB)

Actualmente la conexion a MongoDB esta definida en:

- [src/main/java/org/example/config/MongoDBUtil.java](src/main/java/org/example/config/MongoDBUtil.java)

La URI esta hardcodeada. Si deseas usar otra base de datos:

1. Abre el archivo anterior.
2. Reemplaza la URI de MongoDB Atlas por la de tu entorno.
3. Guarda y vuelve a ejecutar la aplicacion.

Recomendacion para produccion:

- Mover la URI a una variable de entorno.
- No subir credenciales reales al repositorio.

## 4. Como ejecutar el backend

Desde la raiz del proyecto:

```bash
cd /Users/adrian/Documents/UCenfotec/Programacion\ con\ Patrones/Proyecto/Codigo/TorneosDeportivos
mvn clean compile
```

### 4.1 Ejecutar aplicacion en modo menu (consola, por defecto)

Desde IDE (ejecutando Main) o con Maven JavaFX plugin:

```bash
mvn javafx:run
```

Comportamiento:

- Inicia el menu de consola.
- Inicia API REST en puerto 8080 en segundo plano.
- Si abres http://localhost:8080 en navegador, intenta abrir el dashboard JavaFX una vez.

### 4.2 Ejecutar en modo GUI (login JavaFX)

```bash
mvn javafx:run -Djavafx.args="--gui"
```

Comportamiento:

- Abre la vista de login JavaFX.
- Permite crear usuarios y autenticarse.
- Permite crear torneo y generar partidos.
- Inicia API REST en puerto 8080.

## 5. Opciones del menu principal (modo consola)

Las opciones estan definidas en:

- [src/main/java/org/example/controller/TorneoController.java](src/main/java/org/example/controller/TorneoController.java)

Menu principal:

1. Gestion de Equipos
2. Gestion de Jugadores
3. Gestion de Torneos
4. Gestion de Partidos
5. Estadisticas
6. Reportes
7. Cargar datos de prueba
8. Abrir dashboard grafico
0. Salir

## 6. API REST y Swagger

Rutas disponibles al correr el backend:

- Home API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui
- OpenAPI JSON: http://localhost:8080/api/openapi.json

Endpoints de lectura actuales:

- GET /api/torneos
- GET /api/equipos
- GET /api/standings
- GET /api/goleadores
- GET /api/partidos

## 7. Frontend separado (React + Vite)

El frontend esta en carpeta independiente:

- [frontend](frontend)

### 7.1 Instalar dependencias

```bash
cd /Users/adrian/Documents/UCenfotec/Programacion\ con\ Patrones/Proyecto/Codigo/TorneosDeportivos/frontend
npm install
```

### 7.2 Correr en desarrollo

```bash
npm run dev
```

Por defecto abre en:

- http://localhost:5173

### 7.3 Build de produccion

```bash
npm run build
```

Nota:

- El frontend fue creado y compila correctamente.
- La integracion completa de pantallas (login, equipos, torneos, estadisticas) se puede construir consumiendo la API en http://localhost:8080.

## 8. Elastic Stack (Elasticsearch + Kibana + Logstash)

Configuracion ubicada en:

- Compose: [containers/docker-compose.yml](containers/docker-compose.yml)
- Pipeline Logstash: [containers/logstash.conf](containers/logstash.conf)
- Script de reinicio y validacion: [containers/reset_elastic_stack.sh](containers/reset_elastic_stack.sh)

Puertos:

- Elasticsearch: 9200
- Kibana: 5601

### 8.1 Levantar stack manualmente

```bash
cd /Users/adrian/Documents/UCenfotec/Programacion\ con\ Patrones/Proyecto/Codigo/TorneosDeportivos/containers
docker compose up -d
```

### 8.2 Reiniciar stack y validar indices automaticamente

```bash
cd /Users/adrian/Documents/UCenfotec/Programacion\ con\ Patrones/Proyecto/Codigo/TorneosDeportivos/containers
chmod +x reset_elastic_stack.sh
./reset_elastic_stack.sh
```

Este script:

1. Baja contenedores.
2. Levanta contenedores.
3. Espera Elasticsearch en 9200.
4. Espera estado yellow/green del cluster.
5. Verifica documentos en indice torneos-logs-*.
6. Lista indices relevantes.

### 8.3 Ver logs en Kibana

1. Abre http://localhost:5601
2. Crea un Data View para torneos-logs-*
3. Usa Discover para consultar eventos.

## 9. Flujo recomendado paso a paso

1. Configura URI de MongoDB en [src/main/java/org/example/config/MongoDBUtil.java](src/main/java/org/example/config/MongoDBUtil.java).
2. Compila backend con Maven.
3. Levanta Elastic Stack (opcional, para observabilidad).
4. Ejecuta backend:
	 - Modo menu (default), o
	 - Modo GUI con --gui.
5. Verifica API y Swagger en localhost:8080.
6. En otra terminal, levanta frontend desde [frontend](frontend).
7. Consume endpoints desde React contra http://localhost:8080.

## 10. Solucion de problemas

- Puerto 8080 ocupado:
	- Deten otro proceso en 8080 o cambia configuracion del servidor HTTP.
- JavaFX no abre:
	- Verifica JDK 25 y ejecucion desde entorno con soporte grafico.
- MongoDB no conecta:
	- Valida URI, usuario, password, whitelist IP y conectividad.
- Kibana no carga:
	- Revisa docker compose ps y logs de contenedores.

## 11. Comandos utiles

Backend:

```bash
mvn clean compile
mvn javafx:run
mvn javafx:run -Djavafx.args="--gui"
```

Frontend:

```bash
cd frontend
npm install
npm run dev
npm run build
```

Elastic:

```bash
cd containers
docker compose up -d
docker compose down
./reset_elastic_stack.sh
```