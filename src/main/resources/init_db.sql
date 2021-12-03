CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(45) NOT NULL,
                                 `country` varchar(45) NOT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `id_UNIQUE` (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(225) NOT NULL,
                           `license_number` varchar(225) NOT NULL,
                           `is_deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `id_UNIQUE` (`id`),
                           UNIQUE KEY `license_number_UNIQUE` (`license_number`));

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `manufacturer_id` bigint DEFAULT NULL,
                        `model` varchar(225) NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `id_UNIQUE` (`id`),
                        KEY `manufacturer_id_idx` (`manufacturer_id`),
                        CONSTRAINT `manufacturer_id` FOREIGN KEY (`manufacturer_id`)
                            REFERENCES `manufacturers` (`id`));

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` bigint DEFAULT NULL,
                                `car_id` bigint DEFAULT NULL,
                                KEY `driver_id_idx` (`driver_id`),
                                KEY `car_id_idx` (`car_id`),
                                CONSTRAINT `car_id` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `driver_id` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
