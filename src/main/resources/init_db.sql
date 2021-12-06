CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

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
                                  PRIMARY KEY (`id`));
CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) DEFAULT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        `manufacturer_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`)
                            REFERENCES `manufacturers` (`id`));
CREATE TABLE `cars_drivers` (
                                `car_id` bigint NOT NULL,
                                `driver_id` bigint NOT NULL,
                                KEY `car_fk_idx` (`car_id`),
                                KEY `driver_fk_idx` (`driver_id`),
                                CONSTRAINT `car_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
