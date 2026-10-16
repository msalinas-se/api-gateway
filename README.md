# api-gateway

Microservicio que sirve como API gateway para las APIs con Spring Cloud Gateway.

Flujo de autenticación y autorización con JWT: `POST /auth/login` devuelve el token y el resto de rutas lo requiere.

## Tecnologías

- Java 17
- Spring Boot
- Spring Cloud Gateway
- Spring Security (JWT)
- Maven
