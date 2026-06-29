# GameShelf — Sistema de Microservicios para Biblioteca de Videojuegos

## DSY1103 — Desarrollo FullStack I

**Evaluación Parcial 3 — Encargo con Defensa Técnica**

\---

## Enlaces de entrega

> Los enlaces externos se incorporan cuando los archivos finales estén subidos a Google Drive u otro medio indicado por el docente.

|Recurso de entrega|Estado|
|-|-|
|Versión nativa `(.jar + .bat)`|Pendiente de carga|
|Versión Docker|Pendiente de carga|
|Video de defensa técnica|Pendiente de carga|
|Subtítulos del video / `subtitulos-video.txt`|Pendiente de carga|

**Observación sobre el video:** la defensa técnica grabada debe tener una duración ideal cercana a **15 minutos** y no superar los **18 minutos**. El video debe incluir subtítulos incrustados o acompañarse del archivo `subtitulos-video.txt`.

\---

## 1\. Descripción general

**GameShelf** es un sistema académico de microservicios orientado a la gestión de una biblioteca de videojuegos.  
El sistema permite administrar usuarios, roles, videojuegos, categorías, stock, préstamos, reservas, multas, notificaciones y autorizaciones mediante una arquitectura distribuida basada en **Spring Boot**, **Spring Cloud**, **Eureka Server**, **API Gateway**, **OpenFeign**, **MySQL**, **Swagger/OpenAPI**, **JUnit 5**, **Mockito**, **JaCoCo** y **Docker Compose**.

El proyecto fue desarrollado como una solución Maven multi-módulo, manteniendo separación de responsabilidades por capas bajo el patrón:


Controller → Service → Repository / Model


\---

## 2\. Integrantes

|Integrantes|
|-|
|Pablo González|
|Gabriel Chodil|



## 3\. Objetivo del proyecto

El objetivo de GameShelf es demostrar la construcción, documentación, prueba y despliegue de un ecosistema completo de microservicios independientes, capaces de comunicarse entre sí mediante REST y Feign Client.

El sistema permite ejecutar el siguiente flujo funcional principal:

1. Registrar roles.
2. Registrar usuarios asociados a un rol válido.
3. Registrar categorías de videojuegos.
4. Registrar videojuegos asociados a una categoría activa.
5. Registrar stock asociado a videojuegos existentes.
6. Crear préstamos y reducir automáticamente el stock disponible.
7. Devolver préstamos y aumentar automáticamente el stock disponible.
8. Crear reservas y registrar historial de cambios.
9. Crear y pagar multas asociadas a préstamos.
10. Crear y leer notificaciones asociadas a usuarios.
11. Crear autorizaciones y validar permisos correctos e incorrectos.

\---

## 4\. Arquitectura general


Cliente externo / Postman / Navegador
        |
        v
API Gateway :8080
        |
        +--> ms-usuario       :8081 -> db\_usuario
        +--> ms-roles         :8082 -> db\_roles
        +--> ms-videojuego    :8083 -> db\_videojuego
        +--> ms-categoria     :8084 -> db\_categoria
        +--> ms-stock         :8085 -> db\_stock
        +--> ms-prestamo      :8086 -> db\_prestamo
        +--> ms-multa         :8087 -> db\_multa
        +--> ms-reserva       :8088 -> db\_reserva
        +--> ms-notificacion  :8089 -> db\_notificacion
        +--> ms-autorizacion  :8090 -> db\_autorizacion

Eureka Server :8761
MySQL local   :3307
MySQL Docker  :3308 externo / 3306 interno


\---

## 5\. Microservicios implementados

|Módulo|Puerto|Base de datos|Responsabilidad principal|
|-|-:|-|-|
|`eureka-server`|8761|No aplica|Registro y descubrimiento de microservicios|
|`api-gateway`|8080|No aplica|Punto único de entrada y enrutamiento|
|`ms-usuario`|8081|`db\_usuario`|Gestión de usuarios y validación de rol|
|`ms-roles`|8082|`db\_roles`|Gestión y validación de roles|
|`ms-videojuego`|8083|`db\_videojuego`|Gestión del catálogo de videojuegos|
|`ms-categoria`|8084|`db\_categoria`|Gestión de categorías|
|`ms-stock`|8085|`db\_stock`|Gestión de stock y disponibilidad|
|`ms-prestamo`|8086|`db\_prestamo`|Gestión de préstamos, devoluciones y renovaciones|
|`ms-multa`|8087|`db\_multa`|Gestión de multas y pagos de multas|
|`ms-reserva`|8088|`db\_reserva`|Gestión de reservas e historial|
|`ms-notificacion`|8089|`db\_notificacion`|Gestión de notificaciones|
|`ms-autorizacion`|8090|`db\_autorizacion`|Gestión y validación de permisos|

\---

## 6\. Tecnologías utilizadas

|Tecnología|Uso en el proyecto|
|-|-|
|Java 21 / JDK compatible superior|Desarrollo y compilación de microservicios|
|Spring Boot 3.5.14|Framework principal|
|Spring Cloud 2025.0.2|Descubrimiento, Gateway y comunicación distribuida|
|Eureka Server / Eureka Client|Registro y descubrimiento de servicios|
|Spring Cloud Gateway|Enrutamiento centralizado|
|OpenFeign|Comunicación REST entre microservicios|
|Spring Web|APIs REST|
|Spring Data JPA|Persistencia|
|MySQL 8|Base de datos|
|XAMPP|MySQL local en puerto 3307|
|Docker Desktop|Despliegue por contenedores|
|Docker Compose|Orquestación local del ecosistema|
|Maven Multi-Módulo|Compilación y gestión de módulos|
|Lombok|Reducción de código repetitivo|
|Bean Validation|Validación de DTOs|
|Swagger / OpenAPI|Documentación de endpoints|
|JUnit 5|Pruebas unitarias|
|Mockito|Simulación de dependencias|
|MockMvc|Pruebas de controllers|
|JaCoCo 0.8.13|Reportes de cobertura|

\---

## 7\. Estructura del proyecto


GameShelf/
|
├── pom.xml
├── README.md
├── docker-compose.yml
├── .gitignore
|
├── eureka-Server/
│   └── eureka-Server/
│       ├── pom.xml
│       └── src/
|
├── api-gateway/
│   └── api-gateway/
│       ├── pom.xml
│       └── src/
|
├── ms-usuario/
│   └── ms-usuario/
│       ├── pom.xml
│       └── src/
|
├── ms-roles/
│   └── ms-roles/
│       ├── pom.xml
│       └── src/
|
├── ms-categoria/
│   └── ms-categoria/
│       ├── pom.xml
│       └── src/
|
├── ms-videojuego/
│   └── ms-videojuego/
│       ├── pom.xml
│       └── src/
|
├── ms-stock/
│   ├── pom.xml
│   └── src/
|
├── ms-prestamo/
│   └── ms-prestamo/
│       ├── pom.xml
│       └── src/
|
├── ms-reserva/
│   └── ms-reserva/
│       ├── pom.xml
│       └── src/
|
├── ms-multa/
│   └── ms-multa/
│       ├── pom.xml
│       └── src/
|
├── ms-notificacion/
│   └── ms-notificacion/
│       ├── pom.xml
│       └── src/
|
└── ms-autorizacion/
    └── ms-autorizacion/
        ├── pom.xml
        └── src/


\---

## 8\. Configuración de base de datos local

La ejecución nativa utiliza **MySQL mediante XAMPP** en el puerto:


3307


Ejemplo de configuración local en los microservicios:

   yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/db\_usuario
    username: root
    password: ""

Cada microservicio trabaja con su propia base de datos:

|Microservicio|Base de datos|
|-|-|
|`ms-usuario`|`db\_usuario`|
|`ms-roles`|`db\_roles`|
|`ms-categoria`|`db\_categoria`|
|`ms-videojuego`|`db\_videojuego`|
|`ms-stock`|`db\_stock`|
|`ms-prestamo`|`db\_prestamo`|
|`ms-reserva`|`db\_reserva`|
|`ms-multa`|`db\_multa`|
|`ms-notificacion`|`db\_notificacion`|
|`ms-autorizacion`|`db\_autorizacion`|

Las bases se crean automáticamente mediante JPA cuando el microservicio inicia con:

  yaml
spring.jpa.hibernate.ddl-auto: update


\---

## 9\. Perfiles de configuración YAML

El proyecto mantiene dos configuraciones principales:

|Archivo|Uso|
|-|-|
|`application.yml`|Ejecución local/nativa con XAMPP y `localhost:3307`|
|`application-docker.yml`|Ejecución con Docker Compose usando red interna Docker|

En Docker, los microservicios no usan `localhost` para comunicarse con MySQL ni Eureka.  
Se comunican mediante nombres de servicios definidos en `docker-compose.yml`:

   yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/db\_usuario?createDatabaseIfNotExist=true\&useSSL=false\&allowPublicKeyRetrieval=true\&serverTimezone=UTC

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/


Diferencia principal:


Local/nativo:
MySQL -> localhost:3307
Eureka -> http://localhost:8761/eureka/

Docker:
MySQL -> mysql:3306
Eureka -> http://eureka-server:8761/eureka/


\---

## 10\. Orden de ejecución nativa

Antes de levantar los microservicios de forma nativa, se debe iniciar XAMPP y activar MySQL en el puerto `3307`.

El orden jerárquico correcto de arranque es:


1. Eureka Server
2. Microservicios de negocio
3. API Gateway

Detalle recomendado:

|Orden|Servicio|Puerto|
|-:|-|-:|
|0|`eureka-server`|8761|
|2|`ms-roles`|8082|
|3|`ms-usuario`|8081|
|4|`ms-categoria`|8084|
|5|`ms-videojuego`|8083|
|6|`ms-stock`|8085|
|7|`ms-prestamo`|8086|
|8|`ms-reserva`|8088|
|9|`ms-multa`|8087|
|10|`ms-notificacion`|8089|
|11|`ms-autorizacion`|8090|
|12|`api-gateway`|8080|

La versión nativa externa debe incluir un script `.bat` para ejecutar los `.jar` respetando este orden.

\---

## 11\. Ejecución nativa desde VS Code

Se recomienda usar la extensión **Spring Boot Dashboard** de VS Code.

Desde el panel de Spring Boot, ejecutar los servicios respetando el orden:


eureka-server
ms-roles
ms-usuario
ms-categoria
ms-videojuego
ms-stock
ms-prestamo
ms-reserva
ms-multa
ms-notificacion
ms-autorizacion
api-gateway


También se puede ejecutar un módulo desde terminal. Ejemplo:

  bash
cd ms-usuario/ms-usuario
mvn spring-boot:run


\---

## 12\. Compilación y pruebas del proyecto completo

Desde la raíz del proyecto:

  bash
mvn clean install
```

Este comando:

1. Limpia los artefactos anteriores.
2. Compila todos los módulos.
3. Ejecuta la suite de pruebas unitarias.
4. Genera los `.jar`.
5. Genera reportes de cobertura JaCoCo en cada microservicio configurado.

Para compilar sin ejecutar pruebas, solo cuando se necesiten generar `.jar` para el paquete Docker:

  bash
mvn clean package -DskipTests
```

\---

## 13\. Pruebas unitarias y cobertura

El proyecto incorpora pruebas unitarias y de controller en los microservicios de negocio.

Tecnologías utilizadas:


JUnit 5
Mockito
MockMvc
JaCoCo
```

Estructura de pruebas por microservicio:


src/test/java
├── controller/
│   └── \*ControllerTest.java
└── service/
    └── \*ServiceImplTest.java


Las pruebas validan:


Reglas de negocio.
Validaciones de entrada.
Manejo de errores.
Llamadas a repositorios mediante mocks.
Comunicación simulada con otros microservicios.
Respuestas HTTP desde controllers.


El criterio de cobertura configurado para la evaluación es:
Cobertura mínima: 80% o superior en microservicios de negocio.


Los reportes JaCoCo se consultan en:

NOMBRE\_MICROSERVICIO/target/site/jacoco/index.html


Ejemplos:


ms-usuario/ms-usuario/target/site/jacoco/index.html
ms-roles/ms-roles/target/site/jacoco/index.html
ms-categoria/ms-categoria/target/site/jacoco/index.html
ms-videojuego/ms-videojuego/target/site/jacoco/index.html
ms-stock/target/site/jacoco/index.html
ms-prestamo/ms-prestamo/target/site/jacoco/index.html
```

\---

## 14\. Eureka Server

Eureka permite visualizar los microservicios registrados.


http://localhost:8761


Servicios esperados:


API-GATEWAY
MS-USUARIO
MS-ROLES
MS-CATEGORIA
MS-VIDEOJUEGO
MS-STOCK
MS-PRESTAMO
MS-RESERVA
MS-MULTA
MS-NOTIFICACION
MS-AUTORIZACION


\---

## 15\. API Gateway

El Gateway centraliza las solicitudes en:


http://localhost:8080


Rutas principales configuradas:

|Recurso|Ruta Gateway|
|-|-|
|Usuarios|`http://localhost:8080/api/usuarios`|
|Roles|`http://localhost:8080/api/roles`|
|Categorías|`http://localhost:8080/api/categorias`|
|Videojuegos|`http://localhost:8080/api/videojuegos`|
|Stock|`http://localhost:8080/api/stocks`|
|Préstamos|`http://localhost:8080/api/prestamos`|
|Reservas|`http://localhost:8080/api/reservas`|
|Multas|`http://localhost:8080/api/multas`|
|Notificaciones|`http://localhost:8080/api/notificaciones`|
|Autorizaciones|`http://localhost:8080/api/autorizaciones`|

\---

## 16\. Swagger / OpenAPI

Cada microservicio de negocio expone documentación Swagger/OpenAPI.

|Microservicio|Swagger|
|-|-|
|`ms-usuario`|`http://localhost:8081/swagger-ui.html`|
|`ms-roles`|`http://localhost:8082/swagger-ui.html`|
|`ms-videojuego`|`http://localhost:8083/swagger-ui.html`|
|`ms-categoria`|`http://localhost:8084/swagger-ui.html`|
|`ms-stock`|`http://localhost:8085/swagger-ui.html`|
|`ms-prestamo`|`http://localhost:8086/swagger-ui.html`|
|`ms-multa`|`http://localhost:8087/swagger-ui.html`|
|`ms-reserva`|`http://localhost:8088/swagger-ui.html`|
|`ms-notificacion`|`http://localhost:8089/swagger-ui.html`|
|`ms-autorizacion`|`http://localhost:8090/swagger-ui.html`|

Dependencia utilizada:

xml
<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
<version>2.8.17</version>


\---

## 17\. Comunicación entre microservicios

El proyecto utiliza **OpenFeign** para validar información entre servicios.

|Servicio origen|Servicio destino|Objetivo|
|-|-|-|
|`ms-usuario`|`ms-roles`|Validar que el rol exista y esté activo|
|`ms-videojuego`|`ms-categoria`|Validar que la categoría exista y esté activa|
|`ms-stock`|`ms-videojuego`|Validar que el videojuego exista|
|`ms-prestamo`|`ms-usuario`|Validar que el usuario exista|
|`ms-prestamo`|`ms-videojuego`|Validar que el videojuego exista|
|`ms-prestamo`|`ms-stock`|Validar y modificar disponibilidad|
|`ms-reserva`|`ms-usuario`|Validar que el usuario exista|
|`ms-reserva`|`ms-videojuego`|Validar que el videojuego exista|
|`ms-reserva`|`ms-stock`|Reducir disponibilidad al reservar|
|`ms-multa`|`ms-usuario`|Validar usuario asociado|
|`ms-multa`|`ms-prestamo`|Validar préstamo asociado|
|`ms-notificacion`|`ms-usuario`|Validar usuario receptor|
|`ms-autorizacion`|`ms-usuario`|Validar usuario asociado|

\---

## 18\. Endpoints principales

### Roles

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/roles`|Crear rol|
|GET|`/api/roles`|Listar roles|
|GET|`/api/roles/{id}`|Buscar rol por ID|
|GET|`/api/roles/nombre/{nombre}`|Buscar rol por nombre|
|GET|`/api/roles/validar/{nombre}`|Validar existencia de rol|
|PUT|`/api/roles/{id}`|Actualizar rol|
|DELETE|`/api/roles/{id}`|Eliminar rol|

### Usuarios

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/usuarios`|Crear usuario|
|GET|`/api/usuarios`|Listar usuarios|
|GET|`/api/usuarios/{id}`|Buscar usuario por ID|
|GET|`/api/usuarios/rol/{rol}`|Buscar usuarios por rol|
|GET|`/api/usuarios/buscar/{usuario}`|Buscar por nombre de usuario|
|PUT|`/api/usuarios/{id}`|Actualizar usuario|
|DELETE|`/api/usuarios/{id}`|Eliminar usuario|

### Videojuegos

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/videojuegos`|Crear videojuego|
|GET|`/api/videojuegos`|Listar videojuegos|
|GET|`/api/videojuegos/{id}`|Buscar videojuego por ID|
|GET|`/api/videojuegos/categoria/{categoriaId}`|Listar por categoría|
|GET|`/api/videojuegos/buscar/{titulo}`|Buscar por título|
|GET|`/api/videojuegos/estado/{estado}`|Listar por estado|
|GET|`/api/videojuegos/plataforma/{plataforma}`|Listar por plataforma|
|PUT|`/api/videojuegos/{id}`|Actualizar videojuego|
|DELETE|`/api/videojuegos/{id}`|Eliminar videojuego|

### Stock

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/stocks`|Crear stock|
|GET|`/api/stocks`|Listar stock|
|GET|`/api/stocks/{id}`|Buscar stock por ID|
|GET|`/api/stocks/videojuego/{videojuegoId}`|Buscar stock por videojuego|
|PUT|`/api/stocks/reducir/{videojuegoId}`|Reducir stock disponible|
|PUT|`/api/stocks/aumentar/{videojuegoId}`|Aumentar stock disponible|
|PUT|`/api/stocks/{id}`|Actualizar stock|
|DELETE|`/api/stocks/{id}`|Eliminar stock|

### Préstamos

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/prestamos`|Crear préstamo|
|GET|`/api/prestamos`|Listar préstamos|
|GET|`/api/prestamos/{id}`|Buscar préstamo por ID|
|GET|`/api/prestamos/usuario/{usuarioId}`|Listar préstamos de usuario|
|GET|`/api/prestamos/videojuego/{videojuegoId}`|Listar por videojuego|
|GET|`/api/prestamos/estado/{estado}`|Listar por estado|
|PUT|`/api/prestamos/devolver/{id}`|Devolver préstamo|
|POST|`/api/prestamos/{id}/renovaciones`|Renovar préstamo|
|GET|`/api/prestamos/{id}/renovaciones`|Ver renovaciones|
|DELETE|`/api/prestamos/{id}`|Eliminar préstamo|

### Reservas

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/reservas`|Crear reserva|
|GET|`/api/reservas`|Listar reservas|
|GET|`/api/reservas/{id}`|Buscar reserva por ID|
|GET|`/api/reservas/usuario/{usuarioId}`|Listar reservas de usuario|
|GET|`/api/reservas/videojuego/{videojuegoId}`|Listar por videojuego|
|GET|`/api/reservas/estado/{estado}`|Listar por estado|
|PUT|`/api/reservas/confirmar/{id}`|Confirmar reserva|
|PUT|`/api/reservas/cancelar/{id}`|Cancelar reserva|
|GET|`/api/reservas/{id}/historial`|Ver historial|
|DELETE|`/api/reservas/{id}`|Eliminar reserva|

### Multas

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/multas`|Crear multa|
|GET|`/api/multas`|Listar multas|
|GET|`/api/multas/{id}`|Buscar multa por ID|
|GET|`/api/multas/usuario/{usuarioId}`|Listar multas de usuario|
|GET|`/api/multas/prestamo/{prestamoId}`|Buscar multa por préstamo|
|GET|`/api/multas/estado/{estado}`|Listar por estado|
|PUT|`/api/multas/pagar/{id}`|Marcar multa como pagada|
|PUT|`/api/multas/anular/{id}`|Anular multa|
|POST|`/api/multas/{id}/pagos`|Registrar pago de multa|
|GET|`/api/multas/{id}/pagos`|Ver pagos de multa|
|DELETE|`/api/multas/{id}`|Eliminar multa|

### Notificaciones

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/notificaciones`|Crear notificación|
|GET|`/api/notificaciones`|Listar notificaciones|
|GET|`/api/notificaciones/{id}`|Buscar por ID|
|GET|`/api/notificaciones/usuario/{usuarioId}`|Listar por usuario|
|GET|`/api/notificaciones/usuario/{usuarioId}/pendientes`|Listar pendientes|
|GET|`/api/notificaciones/estado/{estado}`|Listar por estado|
|PUT|`/api/notificaciones/{id}/leer`|Marcar como leída|
|PUT|`/api/notificaciones/{id}`|Actualizar notificación|
|DELETE|`/api/notificaciones/{id}`|Eliminar notificación|

### Autorizaciones

|Método|Endpoint|Descripción|
|-|-|-|
|POST|`/api/autorizaciones`|Crear autorización|
|GET|`/api/autorizaciones`|Listar autorizaciones|
|GET|`/api/autorizaciones/{id}`|Buscar autorización por ID|
|GET|`/api/autorizaciones/usuario/{usuarioId}`|Listar por usuario|
|POST|`/api/autorizaciones/validar`|Validar permiso|
|PUT|`/api/autorizaciones/{id}`|Actualizar autorización|
|DELETE|`/api/autorizaciones/{id}`|Eliminar autorización|

\---

## 19\. Flujo funcional principal probado por Gateway

Todos los siguientes pasos se consumen desde:


http://localhost:8080


### 1\. Crear rol

http
POST /api/roles
Content-Type: application/json


json
{
  "nombre": "CLIENTE",
  "descripcion": "Usuario cliente que puede reservar y solicitar prestamos",
  "estado": "ACTIVO"
}


### 2\. Crear usuario

http
POST /api/usuarios
Content-Type: application/json


json
{
  "usuario": "pablo\_docker\_001",
  "contrasena": "1234",
  "correo": "pablo.docker.001@gmail.com",
  "rol": "CLIENTE"
}


### 3\. Crear categoría

http
POST /api/categorias
Content-Type: application/json


json
{
  "nombre": "AVENTURA\_DOCKER\_001",
  "descripcion": "Categoria de aventura para prueba funcional",
  "estado": "ACTIVO"
}


### 4\. Crear videojuego

http
POST /api/videojuegos
Content-Type: application/json


json
{
  "titulo": "Zelda Docker 001",
  "descripcion": "Videojuego de aventura para prueba funcional",
  "precio": 49990,
  "categoriaId": 1,
  "plataforma": "PC",
  "estado": "DISPONIBLE"
}


### 5\. Crear stock

http
POST /api/stocks
Content-Type: application/json


json
{
  "videojuegoId": 1,
  "cantidadTotal": 10,
  "cantidadDisponible": 10,
  "estado": "ACTIVO"
}


### 6\. Crear préstamo y validar reducción de stock

http
POST /api/prestamos
Content-Type: application/json


json
{
  "usuarioId": 1,
  "videojuegoId": 1,
  "fechaDevolucion": "2026-07-10"
}


Luego validar:

http
GET /api/stocks/videojuego/1
```

Resultado esperado:


cantidadDisponible: 9


### 7\. Devolver préstamo y validar aumento de stock

http
PUT /api/prestamos/devolver/1


Luego validar:

http
GET /api/stocks/videojuego/1


Resultado esperado:


cantidadDisponible: 10


### 8\. Crear reserva, confirmar y revisar historial

http
POST /api/reservas
Content-Type: application/json


json
{
  "usuarioId": 1,
  "videojuegoId": 1,
  "estado": "PENDIENTE"
}


http
PUT /api/reservas/confirmar/1
GET /api/reservas/1/historial


### 9\. Crear y pagar multa

http
POST /api/multas
Content-Type: application/json


json
{
  "usuarioId": 1,
  "prestamoId": 1,
  "monto": 5000,
  "motivo": "Atraso en la devolucion del videojuego",
  "estado": "PENDIENTE"
}


http
PUT /api/multas/pagar/1


### 10\. Crear y leer notificación

http
POST /api/notificaciones
Content-Type: application/json


json
{
  "usuarioId": 1,
  "titulo": "Prueba Docker GameShelf",
  "mensaje": "El flujo completo de GameShelf fue probado correctamente",
  "tipo": "SISTEMA",
  "estado": "PENDIENTE",
  "referenciaId": 1,
  "referenciaTipo": "RESERVA"
}


http
GET /api/notificaciones/usuario/1/pendientes
PUT /api/notificaciones/1/leer


### 11\. Crear y validar autorización

http
POST /api/autorizaciones
Content-Type: application/json


json
{
  "usuarioId": 1,
  "rol": "CLIENTE",
  "modulo": "PRESTAMOS",
  "permiso": "GESTIONAR\_PRESTAMOS",
  "estado": "ACTIVO"
}


Validación correcta:

http
POST /api/autorizaciones/validar


json
{
  "usuarioId": 1,
  "modulo": "PRESTAMOS",
  "permiso": "GESTIONAR\_PRESTAMOS"
}


Resultado esperado:

json
true


Validación incorrecta:

json
{
  "usuarioId": 1,
  "modulo": "PRESTAMOS",
  "permiso": "ELIMINAR\_PRESTAMOS"
}


Resultado esperado:

json
false


\---

## 20\. Ejecución con Docker Compose

La ejecución con Docker se encuentra configurada mediante:


docker-compose.yml
application-docker.yml


Comando principal:

bash
docker compose up -d


Comando para revisar contenedores:

bash
docker compose ps


Accesos principales:


Eureka Server:
http://localhost:8761

API Gateway:
http://localhost:8080

Prueba rápida:
http://localhost:8080/api/roles


Para detener sin borrar datos:

bash
docker compose down


Para detener y borrar el volumen de MySQL:

bash
docker compose down -v


\---

## 21\. Persistencia de datos en Docker

Docker utiliza un volumen persistente:

yaml
volumes:
  gameshelf\_mysql\_data:


En el servicio MySQL:

yaml
volumes:
  - gameshelf\_mysql\_data:/var/lib/mysql


Esto permite que los datos se mantengan aunque los contenedores se detengan con:

bash
docker compose down

Los datos se eliminan solamente si se ejecuta:

bash
docker compose down -v


o si se borra manualmente el volumen desde Docker Desktop.

\---

## 22\. Manejo de errores

Los microservicios incorporan manejo centralizado de errores con `@RestControllerAdvice`.

Ejemplo de error de validación:

json
{
  "mensaje": "Existen campos inválidos en la solicitud",
  "validaciones": {
    "nombre": "El nombre es obligatorio"
  }
}


El sistema maneja casos como:


Campos obligatorios ausentes.
IDs inexistentes.
Roles no válidos.
Categorías inactivas.
Videojuegos inexistentes.
Stock insuficiente.
Préstamos inexistentes.
Permisos no autorizados.
Errores de comunicación remota entre microservicios.


\---

## 23\. Buenas prácticas aplicadas


Separación por capas Controller, Service, Repository y Model.
Uso de DTOs para entrada y salida.
Validaciones mediante Bean Validation.
Comunicación distribuida mediante Feign Client.
Documentación Swagger por microservicio.
Pruebas unitarias con mocks.
Cobertura JaCoCo.
Configuración mediante YAML.
Perfiles separados para local y Docker.
Gateway centralizado.
Base de datos independiente por microservicio.
Borrado lógico en servicios donde aplica.


\---

## 24\. Comandos útiles

### Compilar y ejecutar pruebas

bash
mvn clean install


### Ejecutar pruebas solamente

bash
mvn clean test


### Generar `.jar` para despliegue

bash
mvn clean package -DskipTests


### Ejecutar un módulo individual

bash
mvn -pl ms-usuario/ms-usuario spring-boot:run
```

### Ejecutar pruebas de un módulo

bash
mvn -pl ms-usuario/ms-usuario clean test


### Abrir reporte JaCoCo

bash
start ms-usuario\\ms-usuario\\target\\site\\jacoco\\index.html


\---

## 25\. Archivos que no deben subirse a GitHub

El repositorio debe mantenerse limpio. No se deben subir:


target/
apps/
\*.jar
\*.zip
\*.mp4
\*.bat
\*.log


Estos archivos se entregan externamente cuando corresponda.

El repositorio sí debe contener:


Código fuente.
pom.xml.
application.yml.
application-docker.yml.
docker-compose.yml.
README.md.
Pruebas unitarias.
.gitignore.


\---

## 26\. Estado final del proyecto

|Elemento|Estado|
|-|-|
|Maven multi-módulo|Implementado|
|10+ microservicios|Implementado|
|Eureka Server|Implementado|
|API Gateway|Implementado|
|Feign Client|Implementado|
|MySQL por microservicio|Implementado|
|Swagger/OpenAPI|Implementado|
|Pruebas unitarias|Implementado|
|Mockito / MockMvc|Implementado|
|JaCoCo|Implementado|
|Cobertura mínima 80%|Implementado|
|Docker Compose|Implementado|
|Perfil `application-docker.yml`|Implementado|
|Flujo funcional completo por Gateway|Probado|
|Video defensa técnica|Pendiente de carga|
|Enlaces externos de entrega|Pendiente de carga|

\---

## 27\. Conclusión

GameShelf implementa una arquitectura de microservicios completa y funcional, con separación real de responsabilidades, comunicación distribuida, API Gateway, documentación Swagger, pruebas unitarias, cobertura JaCoCo y despliegue local mediante Docker Compose.

El sistema fue probado funcionalmente desde Postman a través del Gateway, validando el flujo completo de negocio: usuarios, roles, videojuegos, stock, préstamos, devoluciones, reservas, historial, multas, notificaciones y autorizaciones.

