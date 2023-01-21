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
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `model` VARCHAR(255) NOT NULL,
                                  `is_deleted` TINYINT NOT NUll DEFAULT 0,
                                  `manufacturer_id` BIGINT NULL,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `cars_manufacturers_fk`
                                  FOREIGN KEY (`manufacturer_id`)
                                  REFERENCES `taxi_service`.`manufacturers` (`id`)
                                  ON UPDATE NO ACTION
                                  ON DELETE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                  `cars_id` BIGINT NOT NULL,
                                  `drivers_id` BIGINT NOT NULL,
                                  CONSTRAINT `cars_drivers_cars_fk`
                                  FOREIGN KEY (`cars_id`)
                                  REFERENCES `taxi_service`.`cars` (`id`)
                                  ON UPDATE NO ACTION
                                  ON DELETE NO ACTION,
                                  CONSTRAINT `cars_drivers_drivers_fk`
                                  FOREIGN KEY (`drivers_id`)
                                  REFERENCES `taxi_service`.`drivers` (`id`)
                                  ON UPDATE NO ACTION
                                  ON DELETE NO ACTION);
