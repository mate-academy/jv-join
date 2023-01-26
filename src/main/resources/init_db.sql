CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `taxi_manufactures`.`manufacturers`;
CREATE TABLE `taxi_manufactures`.`manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `taxi_manufactures`.`drivers`;
CREATE TABLE `taxi_manufactures`.`drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`);

DROP TABLE IF EXISTS `taxi_manufactures`.`cars`;
CREATE TABLE `taxi_manufactures`.`cars` (
	                              `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `model` VARCHAR(255) NULL,
                                  `manufacturer_id` BIGINT(11) NULL,
	                              `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`)
											REFERENCES `taxi_manufactures`.`manufacturers` (`id`)
                                            ON DELETE NO ACTION
                                            ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `taxi_manufactures`.`cars_drivers`;
CREATE TABLE `taxi_manufactures`.`cars_drivers` (
                                `driver_id` BIGINT(11) NOT NULL,
                                `car_id` BIGINT(11) NOT NULL,
                                CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY(`car_id`)
                                REFERENCES `taxi_manufactures`.`cars` (`id`),
                                CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY(`driver_id`)
                                REFERENCES `taxi_manufactures`.`drivers` (`id`);
