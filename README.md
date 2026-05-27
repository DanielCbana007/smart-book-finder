# Smart Book Finder — Backend

Aplicación web para búsqueda inteligente de libros usando la API pública de Open Library.

## Requisitos

- Java 17 o superior
- Maven (incluye `mvnw`)

## Configuración

Copiar `.env.example` a `.env` y completar las variables:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/smartbookfinder?sslmode=require
DB_USER=tu_usuario
DB_PASSWORD=tu_password
```

El archivo `.env` está en `.gitignore` y **no** se sube al repositorio.

## Cómo ejecutar

### Backend

```bash
# Cargar variables de entorno
set -o allexport; source .env; set +o allexport    # Linux/Mac
# o en PowerShell:
# $env:DATABASE_URL="jdbc:postgresql://..."
# $env:DB_USER="..."
# $env:DB_PASSWORD="..."

# Ejecutar
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Cómo ejecutar tests

```bash
# Todos los tests
./mvnw test

# Con cobertura JaCoCo
./mvnw verify

# Tests de mutación (PIT)
./mvnw pitest:mutationCoverage
```

El reporte de cobertura se genera en `target/site/jacoco/index.html`.
El reporte de mutación se genera en `target/pit-reports/`.

## Perfiles

Por defecto usa la configuración de `application.yml`. Para desarrollo local con H2, sobreescribir las propiedades de base de datos en `application-dev.yml` o usar variables de entorno.

## Estructura del proyecto

```
src/main/java/com/smartbookfinder/
├── client/          # Cliente API externa (Open Library)
├── config/          # Configuración Spring (OpenAPI, RestClient)
├── controller/      # Controladores REST
├── dto/             # DTOs de request/response (records)
├── entity/          # Entidades JPA
├── exception/       # Excepciones personalizadas + GlobalExceptionHandler
├── repository/      # Repositorios JPA
└── service/         # Lógica de negocio
```
