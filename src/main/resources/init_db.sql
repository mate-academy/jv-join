CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `manufacturers`;

CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

CREATE TABLE `manufacturers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `country` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                   PRIMARY KEY (`id`));

CREATE TABLE `taxi_service`.`cars` (
                                 `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                 `manufacturer_id` BIGINT(11) NULL,
                                 `model` VARCHAR(225) NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `cars_manufacturers_manufacturer_id_fk`
                                                 FOREIGN KEY (`manufacturer_id`)
                                                 REFERENCES `taxi_service`.`manufacturers` (`id`)
                                                 ON DELETE NO ACTION
                                                 ON UPDATE NO ACTION);

CREATE TABLE `taxi_service`.`cars_drivers`(
                                  `driver_id`    BIGINT(11) NULL,
                                  `car_id`       BIGINT(11) NULL,
                                   CONSTRAINT `cars_drivers_drivers_id_fk`
                                                  FOREIGN KEY (`driver_id`)
                                                  REFERENCES `taxi_service`.`drivers` (`id`)
                                                  ON DELETE NO ACTION
                                                  ON UPDATE NO ACTION,
			                       CONSTRAINT `cars_drivers_car_id_fk`
                                                  FOREIGN KEY (`car_id`)
                                                  REFERENCES `taxi_service`.`cars` (`id`)
                                                  ON DELETE NO ACTION
					                       	      ON UPDATE NO ACTION);
