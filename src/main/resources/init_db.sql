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
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE);

CREATE TABLE `cars` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `manufacturer_id` BIGINT(11) DEFAULT NULL,
  `model` VARCHAR(255) DEFAULT NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
		CONSTRAINT `cars_manufacturers_fk`
        FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
  `car_id` BIGINT(11) NOT NULL,
  `driver_id` BIGINT(11) NOT NULL,
	CONSTRAINT `cars_drivers_cars_fk`
		FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
	CONSTRAINT `cars_drivers_drivers_fk`
		FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));