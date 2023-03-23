CREATE DATABASE `taxi_service` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `manufacturers` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(255) NOT NULL,
                                 `country` VARCHAR(255) NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `drivers` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(255) NOT NULL,
                           `licenseNumber` VARCHAR(255) NOT NULL,
                           `is_deleted` TINYINT DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `model` VARCHAR(255) DEFAULT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        `manufacturer_id` BIGINT DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
                                `car_id` BIGINT NOT NULL,
                                `driver_id` BIGINT NOT NULL,
                                KEY `cars_drivers_cars_id_idx` (`car_id`),
                                KEY `cars_drivers_drivers_id_idx` (`driver_id`),
                                CONSTRAINT `cars_drivers_cars_id` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_drivers_id` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
