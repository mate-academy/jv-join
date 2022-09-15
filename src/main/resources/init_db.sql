CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service-db`;

CREATE TABLE `drivers` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) DEFAULT NULL,
                          `license_number` varchar(255) DEFAULT NULL,
                          `is_deleted` tinyint NOT NULL DEFAULT '0',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `manufacturers` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `name` varchar(255) DEFAULT NULL,
                                `country` varchar(255) DEFAULT NULL,
                                `is_deleted` tinyint NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `cars` (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `model` varchar(255) DEFAULT NULL,
                       `is_deleted` tinyint NOT NULL DEFAULT '0',
                       `manufacturer_id` bigint DEFAULT NULL,
                       PRIMARY KEY (`id`),
                       KEY `cars_manufacturer_fk` (`manufacturer_id`),
                       CONSTRAINT `cars_manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
                                `car_id` bigint DEFAULT NULL,
                                `driver_id` bigint DEFAULT NULL,
                                KEY `cars_drivers_car_fk` (`car_id`),
                                KEY `cars_drivers_driver_fk` (`driver_id`),
                                CONSTRAINT `cars_drivers_car_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
