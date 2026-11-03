# Sistema de Gestión de Gimnasio

Aplicación de escritorio desarrollada en Java para administrar las operaciones básicas de un gimnasio.

## Tecnologías utilizadas

- Java 26
- Maven
- MySQL
- JDBC
- FlatLaf
- IntelliJ IDEA

## Arquitectura del proyecto

El proyecto está organizado por capas:

- **Modelo:** representación de las entidades del sistema.
- **Vista:** interfaces gráficas.
- **Controlador:** gestión de la interacción entre las vistas y la lógica.
- **DAO:** acceso a la base de datos.
- **Util:** herramientas auxiliares y conexión a MySQL.

## Base de datos

El proyecto incluye el archivo `gimnasio_db.sql`, que contiene las instrucciones para crear la base de datos y sus tablas.

## Configuración

Antes de ejecutar la aplicación, configura las siguientes variables de entorno:

- `DB_URL`: dirección de conexión a MySQL.
- `DB_USER`: usuario de MySQL.
- `DB_PASSWORD`: contraseña de MySQL.

## Instalación y ejecución

1. Clona este repositorio.
2. Abre el proyecto en IntelliJ IDEA.
3. Espera a que Maven descargue las dependencias.
4. Ejecuta el archivo `gimnasio_db.sql` en MySQL.
5. Configura las variables de entorno.
6. Ejecuta la clase principal de la aplicación.

## Autor

Proyecto académico desarrollado como aplicación de escritorio en Java.