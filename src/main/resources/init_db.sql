CREATE SCHEMA if NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` bigint(11) NOT NULL auto_increment,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` tinyint NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` bigint(11) NOT NULL auto_increment,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE index `id_unique` (id ASC) visible,
                                  UNIQUE index `license_number_unique` (`license_number` ASC) visible);


CREATE TABLE `cars` (
                                    `id` bigint(11) NOT NULL auto_increment,
                                    `model` VARCHAR(255) NOT NULL,
                                    `manufacturer_id` bigint(11) NOT NULL,
                                    `is_deleted` tinyint NOT NULL DEFAULT FALSE,
                                    PRIMARY KEY(`id`));

CREATE TABLE `cars_drivers`(
        `car_id` bigint(11) NOT NULL,
        `driver_id` bigint(11) NOT NULL);
