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
        `id` bigint NOT NULL AUTO_INCREMENT,
        `manufacturer_id` bigint NOT NULL,
        `model` varchar(255) NOT NULL,
        `is_deleted` tinyint NOT NULL DEFAULT '0',
        PRIMARY KEY (`id`),
        UNIQUE KEY `id_UNIQUE` (`id`),
        KEY `cars_manufacturers_fk` (`manufacturer_id`),
            CONSTRAINT `cars_manufacturers_fk`
                FOREIGN KEY (`manufacturer_id`)
                REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
  `car_id` bigint DEFAULT NULL,
  `driver_id` bigint DEFAULT NULL,
  KEY `car_id_idx` (`car_id`),
  KEY `driver_id_idx` (`driver_id`),
  CONSTRAINT `car_id` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `driver_id` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
