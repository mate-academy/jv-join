CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);
CREATE TABLE `cars` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `manufacturer_id` bigint NOT NULL,
                                  `model` varchar(255) NOT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`id`),
                                  KEY `manufacturer_id` (`manufacturer_id`),
                                  CONSTRAINT `cars_ibfk_1` FOREIGN KEY (`manufacturer_id`)
                                  REFERENCES `manufacturers` (`id`))
                                  ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
CREATE TABLE `cars_drivers` (
                                  `driver_id` bigint NOT NULL,
                                  `car_id` bigint NOT NULL,
                                  KEY `driver_id` (`driver_id`),
                                  KEY `car_id` (`car_id`),
                                  CONSTRAINT `cars_drivers_ibfk_1`
                                  FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`),
                                  CONSTRAINT `cars_drivers_ibfk_2`
                                  FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`))
                                  ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


