# Identity Service

Microservicio responsable de la **gestión de identidad y emisión de tokens JWT** dentro de la plataforma de microservicios para procesamiento de documentos PDF.

Este servicio actúa como **Source of Truth (SoT) de usuarios y roles**, centralizando la autenticación y permitiendo que otros microservicios validen acceso mediante **JWT firmados con RS256**.

---

# Arquitectura

Este microservicio forma parte de una arquitectura basada en **microservicios desacoplados**.

Responsabilidades principales:

* Gestión de usuarios
* Gestión de roles
* Emisión de tokens JWT
* Validación de autenticación
* Autorización basada en roles

Los demás microservicios no gestionan usuarios directamente; en su lugar confían en los **tokens emitidos por identity-service**.

---

# Tecnologías utilizadas

| Tecnología                             | Propósito                        |
| -------------------------------------- | -------------------------------- |
| Spring Boot 4                          | Framework base del microservicio |
| Spring Security                        | Autenticación y autorización     |
| Spring Security OAuth2 Resource Server | Validación de JWT                |
| Spring Data JPA                        | Persistencia                     |
| PostgreSQL                             | Base de datos                    |
| Hibernate                              | ORM                              |
| Lombok                                 | Reducción de boilerplate         |
| JWT RS256                              | Firma segura de tokens           |
| Gradle                                 | Build system                     |
| OpenAPI / Swagger                      | Documentación de endpoints       |

---

# Estructura del proyecto

La estructura sigue el principio de **Separation of Concerns** para facilitar mantenibilidad y escalabilidad.

```
edu.usip.identity
│
├── api
│   ├── AuthController
│   ├── AdminUserController
│   │
│   ├── dto
│   │   ├── request
│   │   │   ├── LoginRequest
│   │   │   └── UserRequest
│   │   │
│   │   └── response
│   │       ├── LoginResponse
│   │       └── UserResponse
│   │
│   └── error
│       ├── ApiErrorResponse
│       └── GlobalExceptionHandler
│
├── domain
│   ├── AppUser
│   └── Role
│
├── repo
│   └── UserRepository
│
├── service
│   └── UserService
│
├── security
│   ├── SecurityConfig
│   ├── PemKeyLoader
│   ├── JwtKeyConfig
│   └── TokenService
│
└── DataSeeder
```

---

# Justificación de la estructura

La estructura se diseñó para cumplir con principios de ingeniería de software:

### Separation of Concerns

Cada capa tiene una responsabilidad clara:

| Capa     | Responsabilidad               |
| -------- | ----------------------------- |
| api      | Exponer endpoints REST        |
| service  | Lógica de negocio             |
| repo     | Acceso a datos                |
| domain   | Entidades del dominio         |
| security | Configuración de seguridad    |
| dto      | Contratos de entrada y salida |

Esto permite modificar una capa sin afectar las demás.

---

### Alta cohesión y bajo acoplamiento

* Los **DTOs** desacoplan la API del modelo de persistencia.
* Los **servicios encapsulan la lógica de negocio**.
* Los **repositorios solo manejan acceso a datos**.

---

### Preparado para microservicios

Esta estructura permite replicar el patrón en todos los microservicios del sistema, facilitando:

* mantenimiento
* onboarding de desarrolladores
* escalabilidad del código

---

# Modelo de dominio

## Usuario

Entidad principal del sistema.

| Campo      | Descripción               |
| ---------- | ------------------------- |
| id         | Identificador del usuario |
| name       | Nombre                    |
| phone      | Número de teléfono        |
| role       | Rol del usuario           |
| active     | Estado                    |
| created_at | Fecha de creación         |
| updated_at | Fecha de actualización    |

---

# Roles

Actualmente el sistema soporta:

```
ROLE_ADMIN
ROLE_STUDENT
```

Estos roles se incluyen dentro del JWT para permitir autorización en otros servicios.

---

# Flujo de autenticación

1. El cliente envía su teléfono al endpoint de login.
2. El servicio verifica que el usuario exista y esté activo.
3. Se genera un **JWT firmado con RS256**.
4. El cliente usa el token para acceder a otros servicios.

---

# Endpoints

## Login

```
POST /v1/auth/login
```

### Request

```json
{
  "phone": "70000001"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## Crear usuario

```
POST /v1/admin/users
```

Requiere rol **ADMIN**

### Request

```json
{
  "name": "Juan Perez",
  "phone": "70000002",
  "role": "ROLE_STUDENT"
}
```

---

## Actualizar usuario

```
PUT /v1/admin/users
```

---

## Desactivar usuario

```
DELETE /v1/admin/users/{phone}
```

---

## Listar usuarios

```
GET /v1/admin/users
```

---

# Configuración

Archivo principal:

```
src/main/resources/application.properties
```

Ejemplo:

```
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/identity_db
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

security.jwt.private-key-path=private.pem
security.jwt.public-key-path=public.pem
```

---

# Generación de llaves JWT (RS256)

El servicio utiliza **JWT firmados con RSA (RS256)**.
Para ello se requieren dos archivos:

* **private.pem** → utilizado para firmar los tokens
* **public.pem** → utilizado por otros microservicios para validar los tokens

Estas llaves deben generarse **una sola vez en el entorno de desarrollo**.

---

## Paso 1 — Ir al directorio del proyecto

```bash
cd identity-service
```

---

## Paso 2 — Generar la clave privada

```bash
openssl genrsa -out private.pem 2048
```

Esto generará el archivo:

```
private.pem
```

---

## Paso 3 — Generar la clave pública

```bash
openssl rsa -in private.pem -pubout -out public.pem
```

Esto generará el archivo:

```
public.pem
```

---

## Paso 4 — Verificar los archivos

El directorio del proyecto debe contener:

```
identity-service
│
├── private.pem
├── public.pem
├── build.gradle
├── src
└── settings.gradle
```

---

## Paso 5 — Configurar rutas en application.properties

```
security.jwt.private-key-path=private.pem
security.jwt.public-key-path=public.pem
```

El servicio cargará automáticamente estas llaves al iniciar.

---

# Importante

Nunca subir **private.pem** al repositorio.

Agrega al `.gitignore`:

```
*.pem
```

En producción las llaves deben gestionarse mediante:

* Secret Manager
* Vault
* Kubernetes Secrets
* Variables seguras de CI/CD

---

# Cómo ejecutar el proyecto

### 1 Clonar repositorio

```
git clone <repo-url>
cd identity-service
```

---

### 2 Levantar PostgreSQL (Docker)

```
docker run --name identity-postgres \
-e POSTGRES_PASSWORD=postgres \
-e POSTGRES_DB=identity_db \
-p 5432:5432 \
-d postgres:16
```

---

### 3 Generar llaves JWT

```
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
```

---

### 4 Ejecutar aplicación

```
./gradlew bootRun
```

La aplicación iniciará en:

```
http://localhost:8081
```

---

# Seeder inicial

Al iniciar el sistema se crea automáticamente un usuario administrador:

```
phone: 70000001
role: ROLE_ADMIN
```

Esto permite administrar el sistema desde el primer arranque.

---

# Estado actual

✔ Servicio funcional
✔ Login JWT operativo
✔ CRUD de usuarios
✔ Seguridad basada en roles
✔ Persistencia en PostgreSQL

---

# Licencia

Proyecto académico / investigación.
