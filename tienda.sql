DROP DATABASE IF EXISTS Tienda_Electronica;

CREATE DATABASE Tienda_Electronica;
USE Tienda_Electronica;

------------------------- Modulo de ventas

CREATE TABLE Cliente (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    cedula VARCHAR(15) NOT NULL UNIQUE,
    urlImg VARCHAR(200)
);

CREATE TABLE Persona (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contrasenha VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    clienteId VARCHAR(50) NOT NULL,
    FOREIGN KEY(clienteId) REFERENCES Cliente(id)
);

CREATE TABLE Categoria (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE Producto (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    serie VARCHAR(50) NOT NULL,
    stock BIGINT NOT NULL,
    precioActual DOUBLE NOT NULL,
    fechaVencimiento TIMESTAMP NOT NULL,
    urlImg VARCHAR(200),
    idCategoria VARCHAR(50) NOT NULL,
    FOREIGN KEY(idCategoria) REFERENCES Categoria(id)
);

CREATE TABLE Venta (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    fechaVenta TIMESTAMP NOT NULL,
    totalVenta DOUBLE NOT NULL, 
    metodoPago ENUM("EFECTIVO", "TARJETA") NOT NULL,
    idCliente VARCHAR(50) NOT NULL,
    FOREIGN KEY(idCliente) REFERENCES Cliente(id)
);

CREATE TABLE DetalleVenta (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    cantidad BIGINT NOT NULL,
    subtotal DOUBLE NOT NULL,
    precioVenta DOUBLE NOT NULL,
    idProducto VARCHAR(50) NOT NULL,
    idVenta VARCHAR(50) NOT NULL,
    FOREIGN KEY(idProducto) REFERENCES Producto(id),
    FOREIGN KEY(idVenta) REFERENCES Venta(id)
);

CREATE TABLE Promocion (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descuento DOUBLE NOT NULL,
    fechaInicio TIMESTAMP,
    fechaFin TIMESTAMP,
    tipo ENUM('GENERAL', 'ESPECIFICA') NOT NULL
);

CREATE TABLE PromocionCliente(
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    idPromocion VARCHAR(50) NOT NULL,
    idCliente VARCHAR(50) NOT NULL,
    FOREIGN KEY(idPromocion) REFERENCES Promocion(id),
    FOREIGN KEY(idCliente) REFERENCES Cliente(id)
);

CREATE TABLE PromocionProducto(
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    idPromocion VARCHAR(50) NOT NULL,
    idProducto VARCHAR(50) NOT NULL,
    FOREIGN KEY(idPromocion) REFERENCES Promocion(id),
    FOREIGN KEY(idProducto) REFERENCES Producto(id)
);
----------------------------------------------------------

--------- tabla encargada del registro de la sincronizacion

CREATE TABLE ColaSincronizadora(
    secuencia BIGINT NOT NULL AUTO_INCREMENT, -- T1: orden de reproduccion (replay) garantizado
    id VARCHAR(50) NOT NULL,
    accion ENUM("INSERT", "UPDATE", "DELETE") NOT NULL,
    tablaAfectada VARCHAR(50) NOT NULL,
    idRegistroAfectado VARCHAR(50) NOT NULL,
    registroJson TEXT NOT NULL,
    estado VARCHAR(2) NOT NULL,
    PRIMARY KEY (secuencia),
    UNIQUE KEY uq_id (id),       -- id sigue siendo unico (lo usan actualizar/eliminar por id)
    INDEX idx_estado (estado)    -- T2: evita escanear toda la tabla en el vaciado (WHERE estado=0)
);

