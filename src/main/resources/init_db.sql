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
                        `model` varchar(45) DEFAULT NULL,
                        `id_manufacturer` bigint DEFAULT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `id_idx` (`id_manufacturer`),
                        CONSTRAINT `id` FOREIGN KEY (`id_manufacturer`) REFERENCES `manufacturers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
CREATE TABLE `cars_drivers` (
                                `id_car_driver` bigint NOT NULL AUTO_INCREMENT,
                                `id_car` bigint DEFAULT NULL,
                                `id_driver` bigint DEFAULT NULL,
                                PRIMARY KEY (`id_car_driver`),
                                KEY `id_cars_idx` (`id_car`),
                                KEY `id_idx` (`id_driver`),
                                CONSTRAINT `id_car` FOREIGN KEY (`id_car`) REFERENCES `cars` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                CONSTRAINT `id_driver` FOREIGN KEY (`id_driver`) REFERENCES `drivers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)

