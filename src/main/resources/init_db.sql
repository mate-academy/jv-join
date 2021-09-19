CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `drivers`;
DROP TABLE IF EXISTS `manufacturers`;

CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

CREATE TABLE `cars` (
                                `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                `manufacturer_id` BIGINT(11) NOT NULL,
                                `name` VARCHAR(225) NOT NULL,
                                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                PRIMARY KEY (`id`),
                                CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`)
                                    REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
                               `driver_id` BIGINT(11) NOT NULL,
                               `car_id` BIGINT(11) NOT NULL,
                                CONSTRAINT `cars_driver_drivers_fk` FOREIGN KEY (`driver_id`)
                                    REFERENCES `drivers` (`id`),
                                CONSTRAINT `cars_driver_cars_fk` FOREIGN KEY (`car_id`)
                                    REFERENCES `cars` (`id`));