CREATE DATABASE `taxi_db` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manufacturer_id` int DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `is_deleted` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cars_manufacrurer_id_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacrurer_id_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
  `driver_id` bigint NOT NULL,
  `car_id` bigint NOT NULL,
  KEY `cars_drivers_drivers_fk` (`driver_id`),
  KEY `cars_drivers_cars_fk` (`car_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `drivers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `license_number` varchar(45) NOT NULL,
  `is_deleted` bigint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `manufacturers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `country` varchar(45) NOT NULL,
  `is_deleted` bigint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb3;
