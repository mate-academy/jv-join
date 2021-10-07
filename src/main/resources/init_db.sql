CREATE SCHEMA `taxi_service` DEFAULT CHARACTER SET utf8mb3;
USE `taxi_service`;

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
                        `id` bigint NOT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `model` varchar(45) NOT NULL,
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        CONSTRAINT `manufactures_id_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
                                `car_id` bigint NOT NULL,
                                `driver_id` bigint NOT NULL,
                                KEY `cars_drivers_car_fk` (`car_id`),
                                KEY `cars_drivers_driver_fk` (`driver_id`),
                                CONSTRAINT `cars_drivers_car_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
