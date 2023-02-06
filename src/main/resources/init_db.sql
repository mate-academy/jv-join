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

CREATE TABLE `cars` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                       `manufacturer_id` BIGINT(11) NOT NULL,
                                       `model` VARCHAR(255) CHARACTER SET 'utf8'
                                           COLLATE 'utf8_general_ci' NOT NULL,
                                       `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                       PRIMARY KEY (`id`),
                                       INDEX `manufacturer_id_idx` (`manufacturer_id` ASC) VISIBLE,
                                       CONSTRAINT `cars_manufacturers_fk`
                                           FOREIGN KEY (`manufacturer_id`)
                                               REFERENCES `taxi_service`.`manufacturers` (`id`)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION);

CREATE TABLE `cars_drivers` (
                                               `car_id` BIGINT(11) NOT NULL,
                                               `driver_id` BIGINT(11) NOT NULL,
                                               INDEX `driver_id_idx` (`driver_id` ASC) VISIBLE,
                                               INDEX `car_id_idx` (`car_id` ASC) VISIBLE,
                                               CONSTRAINT `driver_id`
                                                   FOREIGN KEY (`driver_id`)
                                                       REFERENCES `taxi_service`.`drivers` (`id`)
                                                       ON DELETE NO ACTION
                                                       ON UPDATE NO ACTION,
                                               CONSTRAINT `car_id`
                                                   FOREIGN KEY (`car_id`)
                                                       REFERENCES `taxi_service`.`cars` (`id`)
                                                       ON DELETE NO ACTION
                                                       ON UPDATE NO ACTION);
