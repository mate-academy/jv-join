CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `manufacturers`;
SET FOREIGN_KEY_CHECKS=1;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `drivers`;
SET FOREIGN_KEY_CHECKS=1;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `cars`;
SET FOREIGN_KEY_CHECKS=1;
CREATE TABLE `cars` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `manufactured_id` BIGINT(11) NULL,
                                  `model` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `model_UNIQUE` (`model` ASC) VISIBLE,
                                  CONSTRAINT
                                  FOREIGN KEY (`manufactured_id`)
                                  REFERENCES `taxi`.`manufacturers` (`id`)
                                  ON DELETE NO ACTION
                                  ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
									`id_car` BIGINT(11) NULL,
                                    `id_driver` BIGINT(11) NULL,
                                    `is_deleted` TINYINT NULL DEFAULT 0,
CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`id_car`) REFERENCES `taxi`.`cars`(`id`),
CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`id_driver`) REFERENCES `taxi`.`drivers`(`id`));