-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 07-05-2025 a las 02:19:43
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `prueba`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE `persona` (
  `Uid` varchar(100) NOT NULL,
  `Nombre` varchar(200) NOT NULL,
  `Apellido` varchar(200) NOT NULL,
  `Direccion` varchar(200) NOT NULL,
  `Cedula` varchar(200) NOT NULL,
  `Producto` text NOT NULL,
  `Nom_img` varchar(200) NOT NULL
  `estado` CHAR(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`Uid`, `Nombre`, `Apellido`, `Direccion`, `Cedula`, `Producto`, `Nom_img`) VALUES
('12010', 'Santiago', 'Lopez', 'Cra 2', '544554', 'Compu,Dell,7FHFHF;', 'image', '1'),
('1234', 'Andres', 'Lopez', 'cra 34', '111111', 'Tele,Lg,75yy;Compu,Dell,Y6Y;', 'image1','1'),
('2323', 'Puppy', 'Lopez', 'cra 3', '4733478', 'Tele,Lg,734UUD;', 'image','1'),
('345', 'Sandra', 'Lopez', 'cra 23', '152562', 'Compu,Dell,75UUU;Impresora,HP,7FHFH;', 'image','1'),
('434', 'Jose', 'Erazo', 'cra 5', '323', 'Mouse,Dell,67YU;', 'image2','1'),
('44', 'Pepe', 'Lopez', 'cra 3', '67890', 'Tele,Sony,YU76;', 'image2','1'),
('4567', 'Jose', 'Cortes', 'cra 2', '6437634', 'Tele,Lg,YU76;', 'image2','1'),
('9090', 'Santiago', 'Lopez', 'cra 2', '123456', 'TV,LG,786GHH;', 'image','1');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`Uid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
