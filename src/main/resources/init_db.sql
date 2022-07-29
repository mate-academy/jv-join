CREATE DATABASE `taxi_service_db` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `name` varchar(255) DEFAULT NULL,
                                    `country` varchar(255) DEFAULT NULL,
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;


DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `name` varchar(255) DEFAULT NULL,
                                    `licenseNumber` varchar(255) DEFAULT NULL,
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `cars` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `model` varchar(255) DEFAULT NULL,
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                                    `manufacturer_id` bigint DEFAULT NULL,
                                    `driver_id` bigint DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `cars_maufacturers_fk` (`manufacturer_id`),
                                     CONSTRAINT `cars_maufacturers_fk`
                                     FOREIGN KEY (`manufacturer_id`)
                                     REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
                                    `car_id` bigint NOT NULL,
                                    `driver_id` bigint NOT NULL,
                                     KEY `cars_drivers_cars_fk` (`car_id`),
                                     KEY `cars_drivers_drivers_fk` (`driver_id`),
                                     CONSTRAINT `cars_drivers_cars_fk`
                                     FOREIGN KEY (`car_id`)
                                     REFERENCES `cars` (`id`),
                                     CONSTRAINT `cars_drivers_drivers_fk`
                                     FOREIGN KEY (`driver_id`)
                                     REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;



