-- phpMyAdmin SQL Dump
-- version 5.1.1deb5ubuntu1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jun 16, 2023 at 01:08 PM
-- Server version: 8.0.33-0ubuntu0.22.04.2
-- PHP Version: 8.1.2-1ubuntu2.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `taxi_service`
--
USE `taxi_service`;

--
-- Dumping data for table `manufacturers`
--

INSERT INTO `manufacturers` (`id`, `name`, `country`, `is_deleted`) VALUES
                                                                        (0, 'NO NAME', 'NO COUNTRY', 0),
                                                                        (1, 'Chrysler', '🇺🇸USA', 0),
                                                                        (2, 'Dodge', '🇺🇸USA', 0),
                                                                        (3, 'Jeep', '🇺🇸USA', 0),
                                                                        (4, 'Suzuki', '🇯🇵JAPAN', 0),
                                                                        (5, 'Audi', '🇩🇪Germany', 0);
--
-- Dumping data for table `drivers`
--

INSERT INTO `drivers` (`id`, `name`, `license_number`, `is_deleted`) VALUES
    (1, 'John Smith', '2222333322', 0);



--
-- Dumping data for table `cars`
--


INSERT INTO `cars` (`id`, `manufacturer_id`, `model`, `is_deleted`) VALUES
    (1, 0, 'TOYOTA', 0);

--
-- Dumping data for table `cars_drivers`
--

INSERT INTO `cars_drivers` (`driver_id`, `car_id`) VALUES
    (1, 1);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

