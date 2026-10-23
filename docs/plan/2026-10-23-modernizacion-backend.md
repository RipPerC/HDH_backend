# Plan de modernización del backend

## Problema y enfoque propuesto

El backend es una aplicación Java 21 con Spring Boot 3.2.2, Maven, Spring Web, Spring Data JPA y PostgreSQL. Actualmente se empaqueta como WAR, marca Tomcat como dependencia `provided`, usa configuración local de base de datos en `application.properties`, tiene `ddl-auto=update` y el único test identificado comprueba que carga el contexto.

La modernización se hará en el orden solicitado: primero actualizar la plataforma y dependencias a versiones estables vigentes y compatibles, validando el comportamiento existente; después organizar la aplicación como monolito modular con límites por dominio y una arquitectura hexagonal ligera. No se propone dividir el backend en microservicios. El frontend Angular se ejecutará como servicio/contenedor separado, coordinado con el backend mediante Docker Compose. Para el backend se recomienda JAR ejecutable con servidor embebido; no se empaquetará Angular dentro de la aplicación Java.

## Tareas

1. Inventariar módulos, endpoints, entidades, integraciones, configuración Docker/Compose y línea base de compilación; revisar el contrato de comunicación Angular-backend y confirmar que no se requiere un contenedor servlet externo.
2. Actualizar Java/Maven/Spring Boot y dependencias/plugins en incrementos compatibles; resolver avisos deprecados o cambios incompatibles, pasar a JAR ejecutable si no hay requisito de contenedor externo y validar el empaquetado.
3. Definir módulos y puertos/adaptadores a partir de los dominios reales; migrar progresivamente controladores, casos de uso y persistencia sin cambiar contratos públicos.
4. Modernizar configuración, esquema y entrega: configuración externa y secretos, migraciones versionadas de base de datos y servicios Docker Compose separados para Angular y backend; definir red, URL de API, CORS y health checks necesarios, y verificar compilación e integración al final.
5. Actualizar `README.md` al final para reflejar arquitectura y módulos, versiones requeridas, variables de entorno, migraciones, ejecución local y arranque/despliegue con Docker Compose para Angular y backend.

## Consideraciones

- Confirmado: objetivo de monolito modular + hexagonal ligera, sin microservicios.
- Confirmado: Angular y backend se ejecutarán en contenedores separados coordinados por Docker Compose.
- Recomendación: backend como JAR ejecutable con servidor embebido en su propio contenedor; Angular conserva su ciclo de build/servidor estático en un contenedor separado. Esta separación mantiene una responsabilidad por servicio sin fragmentar el backend en microservicios.
- El WAR actual y Tomcat `provided` sugieren un posible despliegue tradicional, pero no son necesarios para este modelo Compose. Verificar Dockerfile/Compose y el flujo real para descartar una dependencia externa antes de cambiar el empaquetado.
- Definir cómo Angular resuelve la URL del API entre desarrollo y producción y configurar CORS/proxy acorde al diseño; no codificar en Angular el nombre interno del servicio como URL pública del navegador.
- Seleccionar la última versión estable compatible al iniciar la implementación; verificar la guía oficial de migración, el JDK disponible y el entorno de despliegue antes de fijar versiones.
- El `application.properties` observado incluye credenciales de desarrollo en claro, logs SQL/web detallados y `ddl-auto=update`; planificar externalización y migraciones explícitas, sin trasladar esos valores a la configuración de producción.
- No definir módulos de dominio por anticipado: extraerlos del inventario de código y contratos existentes.
- Preservar la API y el comportamiento actual; validar compilación, empaquetado y comunicación/configuración de Angular-backend en Docker Compose.
- Los tests quedan explícitamente fuera de este plan y se tratarán en un plan separado.
