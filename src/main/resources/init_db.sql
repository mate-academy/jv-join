CREATE SCHEMA IF NOT EXISTS `taxiservice` DEFAULT CHARACTER SET utf8;
USE `taxiservice`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

DROP TABLE IF EXISTS`cars`;
CREATE TABLE `cars` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `model` VARCHAR(225) NOT NULL,
                                  `manufacturer_id` BIGINT DEFAULT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  KEY `cars_manufacturers_fk` (`manufacturer_id`),
                                  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
                                  ) ENGINE=InnoDb DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS`cars_drivers`;
CREATE TABLE `cars_drivers` (
                                  `car_id` BIGINT NOT NULL,
                                  `driver_id` BIGINT NOT NULL,
                                  CONSTRAINT `cars_drivers_cars` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                  CONSTRAINT `cars_drivers_drivers` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
                                  ) ENGINE=InnoDb DEFAULT CHARSET=utf8mb4;
