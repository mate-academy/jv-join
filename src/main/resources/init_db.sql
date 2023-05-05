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
                                    `model` varchar(255) DEFAULT NULL,
                                    `manufacturer_id` bigint DEFAULT NULL,
                                    `is_deleted` varchar(45) NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`)
                                  ) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb3;

                                  CREATE TABLE `cars_drivers` (
                                    `drivers_id` bigint NOT NULL,
                                    `cars_id` bigint NOT NULL
                                  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


