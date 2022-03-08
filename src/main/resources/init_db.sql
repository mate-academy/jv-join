CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`)
                             );

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
                       );

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                      `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                      `model` VARCHAR(255) NULL,
                      `manufacturer_id` BIGINT(11) NULL,
                      `is_deleted` tinyint NOT NULL DEFAULT 0,
                      PRIMARY KEY (`id`),
                      CONSTRAINT `cars_manufacturers_fk`
                          FOREIGN KEY (manufacturer_id)
                              REFERENCES `taxi_db`.`manufacturers` (`id`)
                              ON UPDATE NO ACTION
                              ON DELETE NO ACTION
                    );

DROP TABLE  IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT(11) NULL,
                                `car_id` BIGINT(11) NULL,
                                CONSTRAINT `cars_cars_drivers_fk`
                                    FOREIGN KEY (car_id)
                                        REFERENCES `taxi_db`.`cars` (`id`)
                                        ON UPDATE NO ACTION
                                        ON DELETE NO ACTION,
                                CONSTRAINT `drivers_cars_drivers_fk`
                                    FOREIGN KEY (driver_id)
                                        REFERENCES `taxi_db`.`drivers` (`id`)
                                        ON UPDATE NO ACTION
                                        ON DELETE NO ACTION
                            );
