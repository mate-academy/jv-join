CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `name` varchar(45) DEFAULT NULL,
                                `license_number` varchar(45) DEFAULT NULL,
                                `is_deleted` bigint NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `cars`
CREATE TABLE `cars` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `model` VARCHAR(45) DEFAULT NULL,
                                `manufacturer_id` BIGINT NOT NULL,
                                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                PRIMARY KEY (`id`),
                                CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`));
DROP TABLE IF EXISTS `car_drivers`
CREATE TABLE `car_drivers` (
                                `car_id` BIGINT NOT NULL,
                                `driver_id` BIGINT NOT NULL,
                                KEY `car_drivers_cars_fk_idx` (`car_id`),
                                KEY `car_drivers_drivers_fk_idx` (`driver_id`),
                                CONSTRAINT `car_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `car_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

