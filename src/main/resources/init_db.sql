CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;

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

CREATE TABLE `cars` (
                                `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                `model` VARCHAR(225) NOT NULL,
                                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                `manufacturer_id` BIGINT(11) NULL,
                                PRIMARY KEY (`id`),
                                CONSTRAINT `manufacturers_fk`
                                    FOREIGN KEY (`manufacturer_id`)
                                    REFERENCES `taxi_service`.`manufacturers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION);

CREATE TABLE `cars_drivers` (
								`car_id` BIGINT(11) NOT NULL,
                                `driver_id` BIGINT(11) NOT NULL,
                                CONSTRAINT `cars_drivers_cars_fk`
									FOREIGN KEY (`car_id`)
									REFERENCES `taxi_service`.`cars` (`id`)
									ON DELETE NO ACTION
									ON UPDATE NO ACTION,
                                CONSTRAINT `cars_drivers_drivers_fk`
									FOREIGN KEY (`driver_id`)
                                    REFERENCES `taxi_service`.`drivers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION);
