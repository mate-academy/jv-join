CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) DEFAULT NULL,
                                 `country` varchar(255) DEFAULT NULL,
                                 `is_deleted` tinyint DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(45) DEFAULT NULL,
                           `licenseNumber` varchar(255) DEFAULT NULL,
                           `is_deleted` tinyint(1) DEFAULT '0',
                           `manufacturer_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `manufacurer_id_idx` (`manufacturer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) COLLATE utf8_hungarian_ci DEFAULT NULL,
                        `is_deleted` tinyint DEFAULT '0',
                        `manufacturer_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `manufacturer_id_idx` (`manufacturer_id`),
                        CONSTRAINT `manufacturer_id` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_hungarian_ci;

CREATE TABLE `cars_drivers` (
                                `cars_id` bigint NOT NULL,
                                `drivers_id` bigint NOT NULL,
                                KEY `cars_id_idx` (`cars_id`),
                                KEY `drivers_id_idx` (`drivers_id`),
                                CONSTRAINT `cars_id` FOREIGN KEY (`cars_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `drivers_id` FOREIGN KEY (`drivers_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;