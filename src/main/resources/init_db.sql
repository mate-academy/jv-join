CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `manufacturers`;
DROP TABLE IF EXISTS `drivers`;

CREATE TABLE `manufacturers` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
								  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`)
);

CREATE TABLE `cars` (
								`id` BIGINT NOT NULL AUTO_INCREMENT,
                                `model` VARCHAR(255) NOT NULL,
                                `manufacturer_id` BIGINT,
                                `is_deleted` TINYINT DEFAULT 0,
                                PRIMARY KEY(`id`),
                                FOREIGN KEY (`manufacturer_id`) REFERENCES manufacturers (`id`)
);

CREATE TABLE `cars_drivers` (
								`car_id` BIGINT,
                                `driver_id` BIGINT,
                                FOREIGN KEY(`car_id`) REFERENCES cars(`id`),
								FOREIGN KEY(`driver_id`) REFERENCES drivers(`id`)
);
