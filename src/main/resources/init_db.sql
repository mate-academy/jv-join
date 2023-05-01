CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGIN NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(45) NOT NULL,
                                        `country` VARCHAR(45) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(45) DEFAULT NULL,
                                  `license_number` VARCHAR(45) DEFAULT NULL,
                                  `is_deleted` TINYINT DEFAULT 0,
                                  PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `model` varchar(45) DEFAULT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  `manufacturer_id` BIGINT DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `cars_manufacturer_fk` (`manufacturer_id`),
                                  CONSTRAINT `cars_manufacturer_fk` FOREIGN KEY (`manufacturer_id`)
                                      REFERENCES `manufacturers` (`id`));

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `car_id` BIGINT NOT NULL,
                                `driver_id` BIGINT NOT NULL,
                                KEY `cars_drivers_cars_fk` (`car_id`),
                                KEY `cars_drivers_drivers_fk` (`driver_id`),
                                CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`)
                                    REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`)
                                    REFERENCES `drivers` (`id`));
