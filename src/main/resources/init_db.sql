CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `car_drivers`, `cars`, `drivers`, `manufacturers`;

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
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(225) NOT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION);

CREATE TABLE `car_drivers` (
                               `car_id` bigint NOT NULL,
                               `driver_id` bigint NOT NULL,
                               KEY `car_drivers_cars_fk` (`car_id`),
                               KEY `car_drivers_drivers_fk` (`driver_id`),
                               CONSTRAINT `car_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
                               ON DELETE NO ACTION
                               ON UPDATE NO ACTION,
                               CONSTRAINT `car_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
                               ON DELETE NO ACTION
                               ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


