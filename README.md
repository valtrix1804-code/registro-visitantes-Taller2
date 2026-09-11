# Registro de Visitantes — Taller 02

Proyecto de **Programación III — Universidad de Córdoba**.

## Objetivo

Preparar la API `registro-visitantes` para producción, construir una imagen Docker multietapa, versionar el proyecto en GitHub y desplegarlo en Render.

El taller también permite comprobar experimentalmente el alcance de los atributos `static` y del estado en memoria de un bean de Spring cuando la aplicación se ejecuta en diferentes procesos o contenedores.

## Tecnologías

* Java 21
* Spring Boot 4.1.1
* Maven
* Docker
* GitHub
* Render

## Repositorio

Repositorio público de GitHub:

`https://github.com/valtrix1804-code/registro-visitantes-Taller2.git`

## Despliegue en Render

URL pública de la aplicación:

`https://registro-visitantes-taller2.onrender.com`

La aplicación se encuentra desplegada mediante Docker en Render.

## Configuración del puerto

El archivo `src/main/resources/application.properties` utiliza:

```properties
spring.application.name=registro-visitantes

server.port=${PORT:8080}
```

La aplicación utiliza la variable de entorno `PORT` proporcionada por el entorno de ejecución.

Si la variable `PORT` no existe, se utiliza `8080` como valor predeterminado, lo que permite ejecutar la aplicación localmente.

Las credenciales, contraseñas y demás información sensible no deben almacenarse en el repositorio. Estos valores deben manejarse mediante variables de entorno o mecanismos de configuración segura.

## Endpoints

### Página principal

```text
GET /
```

Respuesta de ejemplo:

```json
{
  "mensaje": "API Registro de Visitantes funcionando",
  "estado": "OK"
}
```

### Registrar un visitante

```text
POST /api/visitantes?nombre=Ana%20Perez&edad=25
```

### Listar visitantes

```text
GET /api/visitantes
```

### Consultar conteos

```text
GET /api/visitantes/conteos
```

Este endpoint permite consultar:

* Cantidad de visitantes registrados en el servicio.
* Cantidad de objetos `Visitante` creados mediante el atributo `static`.
* Edad mínima configurada.

### Normalizar texto

```text
GET /api/visitantes/normalizar?texto=pedro%20jose%20DIAZ
```

### Consultar instancia

```text
GET /api/visitantes/instancia
```

Este endpoint muestra información de la instancia que está ejecutando la aplicación:

* `host`
* `arranqueJvm`
* `creados`
* `registrados`

El campo `arranqueJvm` permite identificar el momento de inicio de la JVM actual.

## Docker

El proyecto utiliza un `Dockerfile` multietapa.

### Etapa 1: Build

Se utiliza una imagen de Maven con JDK 21 para compilar el proyecto y generar el archivo `.jar`.

### Etapa 2: Runtime

Se utiliza únicamente un JRE de Eclipse Temurin 21 para ejecutar el `.jar` generado.

La separación permite no incluir las herramientas de compilación de Maven en la imagen final de ejecución.

### Construcción de la imagen

```powershell
docker build -t registro-visitantes-taller2 .
```

### Tamaño de la imagen

La imagen construida es:

```text
registro-visitantes-taller2:latest
```

Resultado observado:

```text
DISK USAGE: 518 MB
CONTENT SIZE: 141 MB
```

### Ejecución normal

```powershell
docker run --rm -p 8081:8080 registro-visitantes-taller2
```

Consulta:

```powershell
curl http://localhost:8081/api/visitantes/instancia
```

### Prueba del puerto dinámico

La aplicación también puede utilizar un puerto diferente mediante la variable de entorno `PORT`.

```powershell
docker run --rm -e PORT=9000 -p 8082:9000 registro-visitantes-taller2
```

Consulta:

```powershell
curl http://localhost:8082/api/visitantes/instancia
```

Esta prueba demuestra que la aplicación no depende exclusivamente del puerto `8080`.

## Git

El proyecto se encuentra versionado en Git y publicado en GitHub.

No deben incluirse en el repositorio:

* `target/`
* archivos `.env`
* contraseñas
* credenciales
* archivos de configuración del entorno local
* archivos generados por el IDE

Los commits deben utilizar mensajes descriptivos que permitan identificar los avances realizados durante el taller.

## Render

Para el despliegue se utiliza un **Web Service** conectado al repositorio de GitHub.

Configuración utilizada:

* Runtime/Language: Docker
* Plan: Free
* Health Check Path: `/api/visitantes`

Render utiliza el `Dockerfile` del repositorio para construir la imagen y ejecutar la aplicación.

URL pública:

`https://registro-visitantes-taller2.onrender.com`

## Paso 6 — Prueba de la API pública

Se registraron tres visitantes directamente en la API desplegada en Render:

```json
{
  "nombre": "Ana Perez",
  "edad": 25,
  "id": 1
}
```

```json
{
  "nombre": "Juan Perez",
  "edad": 30,
  "id": 2
}
```

```json
{
  "nombre": "Carlos Diaz",
  "edad": 22,
  "id": 3
}
```

Al consultar:

```text
GET /api/visitantes/conteos
```

se obtuvo:

```json
{
  "registradosEnElServicio": 3,
  "creadosEnLaClase": 3,
  "edadMinima": 18
}
```

La consulta:

```text
GET /api/visitantes/instancia
```

mostró:

```json
{
  "host": "srv-dai2el5g1s2s73c588hg-hibernate-7d88db8f78-72wsd",
  "arranqueJvm": "2026-09-11T22:18:36.536658086Z",
  "creados": 3,
  "registrados": 3
}
```

Estos resultados confirman que la API pública funciona correctamente y que los tres objetos fueron registrados tanto en el servicio como en el contador estático.

## Paso 7 — Experimento de reinicio

Se registraron visitantes y se consultaron los endpoints `/conteos` e `/instancia`.

Posteriormente se reinició el servicio de Render.

Antes del reinicio, el estado de la aplicación contenía datos registrados en memoria. Después del reinicio se observó que:

* `registradosEnElServicio` volvió a `0`.
* `creadosEnLaClase` volvió a `0`.
* El valor de `arranqueJvm` cambió.
* El identificador del `host` también pudo cambiar.

Esto demuestra que tanto el atributo `static` como la lista almacenada por el bean de Spring permanecen únicamente en memoria mientras existe la JVM que ejecuta la aplicación.

### Alcance de `static`

Un atributo `static` pertenece a la clase y existe una sola copia dentro de cada JVM o proceso que carga dicha clase.

Por lo tanto, `static` no significa que el dato sea global para toda la aplicación distribuida en Internet.

Su alcance real es:

```text
Clase → JVM → Proceso → Contenedor/instancia
```

Cuando el contenedor se reinicia y se crea una nueva JVM, los atributos estáticos vuelven a su estado inicial.

## Paso 8 — Dos contenedores

Se levantaron dos contenedores utilizando la misma imagen Docker:

```powershell
docker run -d --name n1 -p 9001:9000 -e PORT=9000 registro-visitantes-taller2

docker run -d --name n2 -p 9002:9000 -e PORT=9000 registro-visitantes-taller2
```

Cada contenedor posee una JVM independiente y, por lo tanto, mantiene su propio estado en memoria.

Se registró un visitante únicamente en `n1`.

El resultado fue:

### Contenedor n1

```json
{
  "registradosEnElServicio": 1,
  "creadosEnLaClase": 1,
  "edadMinima": 18
}
```

### Contenedor n2

```json
{
  "registradosEnElServicio": 0,
  "creadosEnLaClase": 0,
  "edadMinima": 18
}
```

Esto demuestra que el estado almacenado en memoria no se comparte entre los dos contenedores.

### ¿Puede `static` compartir datos entre usuarios?

No.

El atributo `static` no es un mecanismo adecuado para compartir información cuando existen múltiples instancias de una aplicación.

Cada instancia puede tener:

* Su propia JVM.
* Su propia copia de los atributos `static`.
* Su propia lista en memoria.
* Su propio estado.

Para compartir información entre diferentes instancias se necesita un mecanismo externo de persistencia o almacenamiento compartido.

Una alternativa adecuada es utilizar una base de datos, por ejemplo **PostgreSQL**, mediante Spring Data JPA.

## Paso 9A — Prueba con puerto fijo

Se realizó temporalmente una prueba utilizando:

```properties
server.port=8080
```

en lugar de:

```properties
server.port=${PORT:8080}
```

Durante la prueba, Render detectó que la aplicación estaba escuchando en el puerto `8080` y el servicio permaneció en estado **Live**.

En los registros de Render apareció:

```text
Detected a new open port HTTP:8080
```

Por lo tanto, en esta ejecución concreta no se produjo un error de despliegue.

Después de realizar la prueba, se restauró la configuración recomendada:

```properties
server.port=${PORT:8080}
```

Esta configuración permite que Render proporcione dinámicamente el puerto mediante la variable `PORT`, manteniendo `8080` como valor predeterminado para ejecución local.

## Paso 9B — Credencial ficticia en el historial

Se realizó una prueba controlada utilizando una credencial ficticia en el archivo de configuración:

```properties
spring.datasource.password=PASSWORD_FICTICIA_TALLER
```

La modificación fue registrada en Git mediante un commit.

Posteriormente, la línea fue eliminada y se realizó otro commit.

La prueba permitió comprobar que eliminar posteriormente una credencial de un archivo no significa que desaparezca del historial de Git.

Por esta razón, si una credencial real llega a publicarse accidentalmente, no basta con eliminarla del archivo actual. También se debe considerar que el secreto pudo quedar registrado en el historial y debe ser revocado o cambiado.

La configuración final del proyecto no contiene la credencial ficticia.

## Dockerfile multietapa

El `Dockerfile` utiliza dos etapas:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

La primera etapa contiene las herramientas necesarias para compilar el proyecto.

La segunda etapa contiene únicamente el entorno necesario para ejecutar la aplicación.

Esta estrategia permite separar el proceso de construcción del entorno de ejecución y evita incluir herramientas de desarrollo innecesarias en la etapa final.

## Conclusiones

El proyecto `registro-visitantes` fue preparado para ejecutarse en un entorno de producción utilizando Spring Boot, Docker, GitHub y Render.

Las pruebas realizadas permitieron comprobar que:

1. La API está disponible públicamente mediante Render.
2. La aplicación utiliza configuración de puerto dinámico mediante `PORT`.
3. El `Dockerfile` utiliza una construcción multietapa.
4. Los atributos `static` mantienen su estado únicamente dentro de la JVM correspondiente.
5. El reinicio de una instancia elimina el estado almacenado únicamente en memoria.
6. Dos contenedores de la misma aplicación poseen estados independientes.
7. `static` no es una solución adecuada para compartir información entre múltiples instancias.
8. Los datos que deben sobrevivir a reinicios o compartirse entre servidores deben almacenarse mediante un mecanismo externo, como una base de datos.
9. Los secretos no deben almacenarse en el repositorio ni quedar expuestos en el historial de Git.
