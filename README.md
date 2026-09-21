# Mi Hogar Inteligente - Backend

API REST construida con **Spring Boot 3** siguiendo **arquitectura hexagonal (puertos y adaptadores)**.

## Módulo actual: Auth

- Registro de usuarios (`POST /api/v1/auth/register`)
- Login con JWT (`POST /api/v1/auth/login`)

## Estructura (por módulo)

```
<modulo>/
├── domain/            # Reglas de negocio puras (sin Spring)
│   ├── model/
│   ├── port/in/       # Casos de uso
│   ├── port/out/      # Contratos que necesita el dominio
│   └── exception/
├── application/       # Implementación de los casos de uso
│   ├── service/
│   └── dto/
└── infrastructure/    # Todo lo que depende de frameworks
    ├── adapter/in/web/    # Controllers, DTOs HTTP
    ├── adapter/out/...    # JPA, seguridad, etc.
    └── config/
```

Para agregar un nuevo módulo (ej. `finanzas`), se replica esta misma estructura dentro de su propia carpeta.

## Cómo correr localmente

1. Crea la base de datos en MySQL Workbench:
   ```sql
   CREATE DATABASE hogar360_db;
   ```
2. Configura las variables de entorno (o usa los valores por defecto en `application.yml`):
   - `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`
3. Ejecuta:
   ```bash
   mvn spring-boot:run
   ```
4. Flyway creará automáticamente la tabla `users`.
5. Documentación disponible en: `http://localhost:8080/swagger-ui.html`

## Endpoints

| Método | Ruta                     | Descripción              |
|--------|--------------------------|--------------------------|
| POST   | /api/v1/auth/register    | Crea una cuenta nueva    |
| POST   | /api/v1/auth/login       | Inicia sesión, retorna JWT |

## Próximos módulos sugeridos
- `finanzas`
- `tareas`
- `inventario`
