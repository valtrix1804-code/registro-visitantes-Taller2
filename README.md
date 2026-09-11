# Registro de Visitantes — Taller 02

Proyecto de Programación III — Universidad de Córdoba.

## Objetivo
Preparar la API `registro-visitantes` para producción, construir una imagen Docker multietapa, versionar el proyecto en GitHub y desplegarlo en Render.

## Tecnologías
- Java 21
- Spring Boot 4.1.1
- Maven
- Docker
- GitHub
- Render

## Configuración del puerto
En `src/main/resources/application.properties`:

```properties
spring.application.name=registro-visitantes
server.port=${PORT:8080}
```

La aplicación usa la variable de entorno `PORT`; si no existe, utiliza `8080`. Las credenciales y demás datos sensibles deben inyectarse mediante variables de entorno y nunca almacenarse en el repositorio.

## Endpoints
- `POST /api/visitantes?nombre=ana%20maria%20perez&edad=25`
- `GET /api/visitantes`
- `GET /api/visitantes/conteos`
- `GET /api/visitantes/normalizar?texto=pedro%20jose%20DIAZ`
- `GET /api/visitantes/instancia`

## Docker
La imagen utiliza dos etapas:
1. **build:** Maven + JDK para compilar el `.jar`.
2. **runtime:** JRE para ejecutar únicamente el `.jar`.

Construcción:

```powershell
docker build -t registro-visitantes .
```

Prueba normal:

```powershell
docker run --rm -p 8081:8080 registro-visitantes
curl http://localhost:8081/api/visitantes/instancia
```

Prueba del puerto dinámico:

```powershell
docker run --rm -e PORT=9000 -p 8082:9000 registro-visitantes
curl http://localhost:8082/api/visitantes/instancia
```

## Git
```powershell
git init
git add .
git commit -m "Taller 02: proyecto listo para desplegar"
git branch -M main
git remote add origin URL_DEL_REPOSITORIO
git push -u origin main
```

No se debe subir `target/`, archivos `.env`, credenciales ni configuraciones del editor.

## Render
Crear un Web Service conectado al repositorio de GitHub:
- Runtime/Language: Docker
- Plan: Free
- Health Check Path: `/api/visitantes`

Render construye la imagen usando el `Dockerfile` y publica una URL.

## Experimentos
### Paso 7 — Reinicio
Registrar tres visitantes y consultar `/conteos` y `/instancia`. Después reiniciar el servicio y consultar nuevamente.

El `static` se mantiene únicamente mientras existe la JVM/proceso que cargó la clase. Al reiniciar el contenedor se crea una nueva JVM, por lo que `totalCreados`, la lista del bean y `ARRANQUE` vuelven a su estado inicial.

### Paso 8 — Dos servidores
Levantar dos contenedores de la misma imagen:

```powershell
docker run -d --name n1 -p 8091:8080 registro-visitantes
docker run -d --name n2 -p 8092:8080 registro-visitantes
```

Registrar en `n1` y consultar `/instancia` en ambos. Cada contenedor posee su propia JVM y su propio estado en memoria. Por tanto, `static` no es un mecanismo válido para compartir datos entre usuarios cuando existen varias instancias.

Para compartir estado se debe usar persistencia externa, por ejemplo una base de datos con JPA y PostgreSQL.

### Errores del Paso 9
**Error A:** usar `server.port=8080` en Render impide que la aplicación escuche el puerto asignado dinámicamente. Corrección:

```properties
server.port=${PORT:8080}
```

**Error B:** una credencial que llegó a Git queda en el historial aunque se borre después. La solución es rotar el secreto y mantenerlo fuera del repositorio, usando variables de entorno.

