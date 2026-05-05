DROP DATABASE IF EXISTS Tienda_Electronica;

CREATE DATABASE Tienda_Electronica;
USE Tienda_Electronica;

------------------------- Modulo de ventas

CREATE TABLE Persona (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    usuario VARCHAR(20) NOT NULL UNIQUE,
    contrasenha VARCHAR(20) NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE Cliente (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    cedula VARCHAR(15) NOT NULL UNIQUE,
    urlImg VARCHAR(200)
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

CREATE TABLE Detalle_Venta (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    cantidad BIGINT NOT NULL,
    subtotal DOUBLE NOT NULL,
    precioVenta DOUBLE NOT NULL,
    idProducto VARCHAR(50) NOT NULL,
    idVenta VARCHAR(50) NOT NULL,
    FOREIGN KEY(idProducto) REFERENCES Producto(id),
    FOREIGN KEY(idVenta) REFERENCES Venta(id)
);

----------------------------------------------------------

--------- tabla encargada del registro de la sincronizacion

CREATE TABLE Cola_Sincronizadora(
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    accion ENUM("INSERT", "UPDATE", "DELETE") NOT NULL,
    tablaAfectada VARCHAR(50) NOT NULL,
    idRegistroAfectado VARCHAR(50) NOT NULL,
    tiempo TIMESTAMP NOT NULL
);

