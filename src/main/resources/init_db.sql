CREATE SCHEMA IF NOT EXISTS `taxi_db` DEFAULT CHARACTER SET utf8mb3;
USE `taxi_db`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL,
                        `country` VARCHAR(255) NOT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL,
                        `license_number` VARCHAR(255) NOT NULL,
                        `is_deleted` TINYINT(11) NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`),
                        UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                        UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
    );

CREATE TABLE `cars` (
                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                        `manufacturer_id` BIGINT(11) NOT NULL,
                        `model` VARCHAR(255) NOT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        CONSTRAINT `manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
);

CREATE TABLE `cars_drivers` (
                        `car_id` BIGINT(11) NOT NULL,
                        `driver_id` BIGINT(11) NOT NULL,
                        KEY `car_fx_idx` (`car_id`),
                        KEY `driver_fx_idx` (`driver_id`),
                        CONSTRAINT `driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
);