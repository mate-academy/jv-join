CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `manufacturers`;
DROP TABLE IF EXISTS `drivers`;

CREATE TABLE `manufacturers` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(225) NOT NULL,
                                 `country` VARCHAR(225) NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `drivers` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(225) NOT NULL,
                           `license_number` VARCHAR(225) NOT NULL,
                           `is_deleted` TINYINT NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`),
                           UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                           UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) NOT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_idx` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars_drivers` (
                                `driver_id` bigint NOT NULL,
                                `car_id` bigint NOT NULL,
                                KEY `cars_driver_driver_idx` (`driver_id`),
                                KEY `cars_driver_car_idx` (`car_id`),
                                CONSTRAINT `cars_driver_car` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_driver_driver` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
