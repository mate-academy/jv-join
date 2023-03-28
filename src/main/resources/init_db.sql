CREATE SCHEMA IF NOT EXISTS `taxi_service_base` DEFAULT CHARACTER SET utf8;
USE `taxi_service_base`;

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

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                `id` BIGINT NOT NULL AUTO_INCREMENT,
                `model` VARCHAR(255) NULL,
                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                `manufacturer_id` BIGINT NULL,
                PRIMARY KEY (`id`),
                CONSTRAINT `cars_manufacturers_fk`
                    FOREIGN KEY (`manufacturer_id`)
                        REFERENCES `manufacturers` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                `car_id` BIGINT NOT NULL,
                `driver_id` BIGINT NOT NULL,
                CONSTRAINT `cars_drivers_cars_fk`
                    FOREIGN KEY (`car_id`)
                        REFERENCES `cars` (`id`),
                CONSTRAINT `cars_drivers_drivers_fk`
                    FOREIGN KEY (`driver_id`)
                        REFERENCES `drivers` (`id`)
);
