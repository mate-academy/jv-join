CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP table IF EXISTS  `cars_drivers`;
DROP table IF EXISTS `cars`;
DROP table IF EXISTS  `manufacturers`;
DROP table IF EXISTS  `drivers`;

CREATE TABLE `manufacturers` (
                                 `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(225) NOT NULL,
                                 `country` VARCHAR(225) NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY (`id`));

CREATE TABLE `cars` (
                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                        `manufacturer_id` BIGINT(11) NOT NULL ,
                        `model` VARCHAR(255) NOT NULL ,
                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`),
                        CONSTRAINT cars_manufacturer_id_fk FOREIGN KEY (manufacturer_id)
                            REFERENCES manufacturers(id)
                            ON DELETE NO ACTION
                            ON UPDATE CASCADE );

CREATE TABLE `drivers` (
                           `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(225) NOT NULL,
                           `license_number` VARCHAR(225) NOT NULL,
                           `is_deleted` TINYINT NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`),
                           UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                           UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

CREATE TABLE `cars_drivers` (
                                driver_id BIGINT(11),
                                car_id BIGINT,
                                CONSTRAINT cars_drivers_drivers_fk
                                    FOREIGN KEY (driver_id) REFERENCES drivers(id)
                                        ON DELETE NO ACTION
                                        ON UPDATE CASCADE ,
                                CONSTRAINT cars_drivers_cars_fk
                                    FOREIGN KEY (car_id) REFERENCES cars(id)
                                        ON DELETE NO ACTION
                                        ON UPDATE CASCADE
)