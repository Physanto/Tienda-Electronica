
DROP DATABASE IF EXISTS Tienda_Electronica;

CREATE DATABASE Tienda_Electronica;
USE Tienda_Electronica;

------------------------- Modulo de ventas

CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    cedula VARCHAR(11) NOT NULL UNIQUE,
    nom_img VARCHAR(200)
);

CREATE TABLE Categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nombre VARCHAR(50) NOT NULL
);

CREATE TABLE Producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    serie VARCHAR(50) NOT NULL,
    stock INT NOT NULL,
    precio_actual DECIMAL(10,2) NOT NULL,
    fecha_vencimiento TIMESTAMP NOT NULL,
    nom_img VARCHAR(200),
    id_categoria INT NOT NULL,
    FOREIGN KEY(id_categoria) REFERENCES Categoria(id_categoria)
);

CREATE TABLE Venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    fecha_venta TIMESTAMP NOT NULL,
    total_venta DECIMAL(10,2) NOT NULL, 
    metodo_pago ENUM("efectivo", "tarjeta") NOT NULL,
    id_cliente INT NOT NULL,
    FOREIGN KEY(id_cliente) REFERENCES Cliente(id_cliente)
);

CREATE TABLE Detalle_Venta (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    id_producto INT NOT NULL,
    id_venta INT NOT NULL,
    FOREIGN KEY(id_producto) REFERENCES Producto(id_producto),
    FOREIGN KEY(id_venta) REFERENCES Venta(id_venta)
);

----------------------------------------------------------

--------- tabla encargada del registro de la sincronizacion

CREATE TABLE Cola_Sincronizadora(
    id_cola_sincronizadora INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    accion ENUM("INSERT", "UPDATE", "DELETE") NOT NULL,
    tabla_afectada VARCHAR(50) NOT NULL,
    id_registro_afectado INT NOT NULL,
    tiempo TIMESTAMP NOT NULL
);

