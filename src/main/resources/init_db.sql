CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `name` varchar(45) DEFAULT NULL,
                                          `country` varchar(45) DEFAULT NULL,
                                          `is_deleted` tinyint NOT NULL DEFAULT '0',
                                          PRIMARY KEY (`id`)
                                        ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;


DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `name` varchar(255) DEFAULT NULL,
                                          `license_number` varchar(255) DEFAULT NULL,
                                          `is_deleted` tinyint NOT NULL DEFAULT '0',
                                          PRIMARY KEY (`id`)
                                        ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `model` varchar(255) DEFAULT NULL,
                                  `manufacturer_id` bigint DEFAULT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`id`),
                                  KEY `cars_manufacturers_fk` (`manufacturer_id`),
                                  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
                                ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                  `car_id` bigint DEFAULT NULL,
                                  `driver_id` bigint DEFAULT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0',
                                  KEY `cars_drivers_cars_fk_idx` (`car_id`),
                                  KEY `cars_drivers_drivers_fk_idx` (`driver_id`),
                                  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;




