CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(225) NOT NULL,
  `country` varchar(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(225) NOT NULL,
  `license_number` varchar(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `license_number_UNIQUE` (`license_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manufacturer_id` bigint NOT NULL,
  `model` varchar(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
    `car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    KEY `cars_drivers_cars_fk` (`car_id`),
    KEY `cars_drivers_drivers_fk` (`driver_id`),
    CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
    CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;