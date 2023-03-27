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
                                    `model` VARCHAR(225) NOT NULL,
                                    `is_deleted` TINYINT NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`),
                                    KEY `cars_manufacturers_fk_idx` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk`
                                    FOREIGN KEY (`manufacturer_id`)
                                    REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
CREATE TABLE `cars_drivers` (
                                    `driver_id` BIGINT NOT NULL,
                                    `car_id` BIGINT NOT NULL,
                                    KEY `cars_drivers_drivers_fk_idx` (`driver_id`),
                                    KEY `cars_drivers_cars_fk_idx` (`car_id`),
  CONSTRAINT `cars_drivers_cars_fk`
                                    FOREIGN KEY (`car_id`)
                                    REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk`
                                    FOREIGN KEY (`driver_id`)
                                    REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3
