-- =====================================================
-- Base de datos: Sistema de Gimnasio (proyecto POO)
-- Motor: MySQL
-- =====================================================
-- Nota de diseño:
-- - Persona/Cliente/Entrenador/Administrador se modelan
--   como "tabla por subclase": persona guarda los datos
--   comunes, y cada rol tiene su propia tabla con FK
--   1 a 1 hacia persona (misma PK).
-- - Membresia y Rutina son 1 a 1 con Cliente, así que la
--   FK vive del lado de Membresia/Rutina (evita FKs
--   circulares entre cliente y esas tablas).
-- - RegistroFisico y Asistencia son historicos (muchos
--   por cliente), por eso llevan FK simple + fecha.
-- =====================================================

CREATE DATABASE IF NOT EXISTS gimnasio_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gimnasio_db;

-- -----------------------------------------------------
-- Tabla: persona (base de la herencia)
-- -----------------------------------------------------
CREATE TABLE persona (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    documento   VARCHAR(20)  NOT NULL,
    telefono    VARCHAR(20),
    email       VARCHAR(100),
    CONSTRAINT uq_persona_documento UNIQUE (documento)
);

-- -----------------------------------------------------
-- Tabla: cliente (hereda de persona)
-- -----------------------------------------------------
CREATE TABLE cliente (
    id INT PRIMARY KEY,
    CONSTRAINT fk_cliente_persona
        FOREIGN KEY (id) REFERENCES persona(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: entrenador (hereda de persona)
-- -----------------------------------------------------
CREATE TABLE entrenador (
    id            INT PRIMARY KEY,
    especialidad  VARCHAR(100),
    CONSTRAINT fk_entrenador_persona
        FOREIGN KEY (id) REFERENCES persona(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: administrador (hereda de persona)
-- -----------------------------------------------------
CREATE TABLE administrador (
    id        INT PRIMARY KEY,
    permisos  VARCHAR(255),
    CONSTRAINT fk_administrador_persona
        FOREIGN KEY (id) REFERENCES persona(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: membresia (1 a 1 con cliente)
-- -----------------------------------------------------
CREATE TABLE membresia (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id        INT NOT NULL,
    fecha_pago        DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    tipo              VARCHAR(50) NOT NULL,
    precio            DECIMAL(10,2) NOT NULL,
    CONSTRAINT uq_membresia_cliente UNIQUE (cliente_id),
    CONSTRAINT fk_membresia_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: registro_fisico (historial, muchos por cliente)
-- -----------------------------------------------------
CREATE TABLE registro_fisico (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id  INT NOT NULL,
    fecha       DATE NOT NULL,
    peso        DECIMAL(5,2) NOT NULL,
    altura      DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_registro_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: rutina (rutina actual, 1 a 1 con cliente)
-- -----------------------------------------------------
CREATE TABLE rutina (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id     INT NOT NULL,
    entrenador_id  INT NOT NULL,
    nombre         VARCHAR(100) NOT NULL,
    nivel          VARCHAR(50),
    objetivo       VARCHAR(100),
    CONSTRAINT uq_rutina_cliente UNIQUE (cliente_id),
    CONSTRAINT fk_rutina_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_rutina_entrenador
        FOREIGN KEY (entrenador_id) REFERENCES entrenador(id)
        ON DELETE RESTRICT
);

-- -----------------------------------------------------
-- Tabla: ejercicio (muchos por rutina)
-- -----------------------------------------------------
CREATE TABLE ejercicio (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    rutina_id     INT NOT NULL,
    nombre        VARCHAR(100) NOT NULL,
    series        INT NOT NULL,
    repeticiones  INT NOT NULL,
    descanso      INT COMMENT 'segundos de descanso',
    CONSTRAINT fk_ejercicio_rutina
        FOREIGN KEY (rutina_id) REFERENCES rutina(id)
        ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: asistencia (muchos por cliente y por entrenador)
-- -----------------------------------------------------
CREATE TABLE asistencia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id     INT NOT NULL,
    entrenador_id  INT NOT NULL,
    fecha          DATE NOT NULL,
    hora           TIME NOT NULL,
    turno          ENUM('mañana', 'noche') NOT NULL,
    CONSTRAINT fk_asistencia_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_asistencia_entrenador
        FOREIGN KEY (entrenador_id) REFERENCES entrenador(id)
        ON DELETE RESTRICT
);

select * from persona

