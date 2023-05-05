CREATE DATABASE `taxiservice_db` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model` varchar(255) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `manufacturer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `car_manufacturer_fk_idx` (`manufacturer_id`),
  CONSTRAINT `car_manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
  `car_id` bigint NOT NULL,
  `driver_id` bigint NOT NULL,
  UNIQUE KEY `car_id` (`car_id`,`driver_id`),
  KEY `driver_id_fk` (`driver_id`),
  CONSTRAINT `car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb3_general_mysql500_ci DEFAULT NULL,
  `license_number` varchar(255) COLLATE utf8mb3_general_mysql500_ci DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_mysql500_ci;

CREATE TABLE `manufacturers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `country` varchar(255) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
