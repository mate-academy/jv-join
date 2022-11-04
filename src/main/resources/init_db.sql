CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturer`;
CREATE TABLE `manufacturer` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `driver`;
CREATE TABLE `driver` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

DROP TABLE IF EXISTS `car`;
CREATE TABLE `car` (
                       `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                       `manufacturer_id` bigint DEFAULT NULL,
                       `model` varchar(45) NOT NULL,
                       `is_deleted` tinyint NOT NULL DEFAULT '0',
                       PRIMARY KEY (`id`),
                       KEY `manufacturer_id_idx` (`manufacturer_id`),
                       CONSTRAINT `` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturer` (`id`)
);

DROP TABLE IF EXISTS `car_driver`;
CREATE TABLE `car_driver` (
                              `car_id` bigint NOT NULL,
                              `driver_id` bigint NOT NULL,
                              PRIMARY KEY (`car_id`,`driver_id`),
                              KEY `driver_idx` (`driver_id`),
                              CONSTRAINT `car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`),
                              CONSTRAINT `driver` FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`)
);

