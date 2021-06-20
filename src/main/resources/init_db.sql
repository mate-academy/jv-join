CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

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
                                `color` VARCHAR(255)  NOT NULL,
                                `price` DECIMAL NOT NULL,
                                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                `driver_id` BIGINT NULL,
                                PRIMARY KEY (`id`),
                                CONSTRAINT `cars_driver_id_fk` FOREIGN KEY (`driver_id`)
                                    REFERENCES `library_db`.`drivers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION);
