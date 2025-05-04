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

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `manufacturer_id` BIGINT(11) NOT NULL,
                                        `model` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`),
                                        INDEX `manufacturer_id_idx` (`manufacturer_id` ASC) VISIBLE,
                                        CONSTRAINT `manufacturer_id`
                                        FOREIGN KEY (`manufacturer_id`)
                                        REFERENCES `manufacturers` (`id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                        `driver_id` BIGINT NOT NULL,
                                        `car_id` BIGINT NOT NULL,
                                        INDEX `driver_id_idx` (`driver_id` ASC) VISIBLE,
                                        INDEX `car_id_idx` (`car_id` ASC) VISIBLE,
                                        CONSTRAINT `driver_id`
                                        FOREIGN KEY (`driver_id`)
                                        REFERENCES `drivers` (`id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION,
                                        CONSTRAINT `car_id`
                                        FOREIGN KEY (`car_id`)
                                        REFERENCES `cars` (`id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION);
