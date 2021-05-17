CREATE DATABASE `mate_hw_db` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `manufacturers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model` varchar(45) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  `manufacturer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `license_number` varchar(45) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars_drivers` (
  `driver_id` bigint NOT NULL,
  `car_id` bigint NOT NULL,
  UNIQUE KEY `unique_index` (`driver_id`,`car_id`),
  KEY `cars_drivers_cars_fk` (`car_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



