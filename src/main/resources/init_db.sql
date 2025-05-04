CREATE DATABASE `taxi_services` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

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
                                  `model` varchar(255) DEFAULT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0',
                                  `manufacturer_id` bigint DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `cars_manufacturer_fk` (`manufacturer_id`),
                                  CONSTRAINT `cars_manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
                                  ) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
                                  `car_id` bigint NOT NULL,
                                  `driver_id` bigint NOT NULL,
                                  KEY `cars_drivers_cars_fr` (`car_id`),
                                  KEY `cars_drivers_drivers_fr` (`driver_id`),
                                  CONSTRAINT `cars_drivers_cars_fr` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                  CONSTRAINT `cars_drivers_drivers_fr` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
                                  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

