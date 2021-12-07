CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225),
                                        `country` VARCHAR(225),
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`)
                             );

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225),
                                  `license_number` VARCHAR(225),
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`)
                       );
DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                           `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                           `model` VARCHAR(225),
                           `manufacturer_id` BIGINT (11),
                           `is_deleted` TINYINT NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`),
                           CONSTRAINT `car_manufacturer_id`
                                FOREIGN KEY (`manufacturer_id`)
                                    REFERENCES `taxi_service`.`manufacturers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION
                    );
DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT(11),
                                `car_id` BIGINT(11),
                                CONSTRAINT `cars_drivers_driver`
                                    FOREIGN KEY (`driver_id`)
                                        REFERENCES `taxi_service`.`drivers` (`id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION,
                                CONSTRAINT `cars_drivers_car`
                                    FOREIGN KEY (`car_id`)
                                        REFERENCES `taxi_service`.`cars` (`id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION
                            );
