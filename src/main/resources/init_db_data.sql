-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия сервера:               8.0.31 - MySQL Community Server - GPL
-- Операционная система:         Win64
-- HeidiSQL Версия:              12.2.0.6576
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Дамп структуры базы данных taxi_service
DROP DATABASE IF EXISTS `taxi_service`;
CREATE DATABASE IF NOT EXISTS `taxi_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `taxi_service`;

-- Дамп структуры для таблица taxi_service.cars
DROP TABLE IF EXISTS `cars`;
CREATE TABLE IF NOT EXISTS `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manufacturer_id` bigint NOT NULL,
  `model` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_deleted` smallint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы taxi_service.cars: ~0 rows (приблизительно)
INSERT INTO `cars` (`id`, `manufacturer_id`, `model`, `is_deleted`) VALUES
	(1, 0, 'TOYOTA', 0);

-- Дамп структуры для таблица taxi_service.cars_drivers
DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE IF NOT EXISTS `cars_drivers` (
  `driver_id` bigint NOT NULL,
  `car_id` bigint NOT NULL,
  UNIQUE KEY `car_driver_UNIQUE` (`car_id`,`driver_id`),
  KEY `cars_drivers_drivers_fk` (`driver_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы taxi_service.cars_drivers: ~0 rows (приблизительно)
INSERT INTO `cars_drivers` (`driver_id`, `car_id`) VALUES
	(1, 1);

-- Дамп структуры для таблица taxi_service.drivers
DROP TABLE IF EXISTS `drivers`;
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `license_number` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `license_number_UNIQUE` (`license_number`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы taxi_service.drivers: ~0 rows (приблизительно)
INSERT INTO `drivers` (`id`, `name`, `license_number`, `is_deleted`) VALUES
	(1, 'John Smith', '2222333322', 0);

-- Дамп структуры для таблица taxi_service.manufacturers
DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE IF NOT EXISTS `manufacturers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `country` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Дамп данных таблицы taxi_service.manufacturers: ~0 rows (приблизительно)
INSERT INTO `manufacturers` (`id`, `name`, `country`, `is_deleted`) VALUES
	(0, 'NO NAME', 'NO COUNTRY', 0),
	(1, 'Chrysler', '🇺🇸USA', 0),
	(2, 'Dodge', '🇺🇸USA', 0),
	(3, 'Jeep', '🇺🇸USA', 0),
	(4, 'Suzuki', '🇯🇵JAPAN', 0),
	(5, 'Audi', '🇩🇪Germany', 0);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
