CREATE DATABASE `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE taxi_service_db ;

CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) NOT NULL,
                                 `country` varchar(255) NOT NULL,
                                 `is_deleted` tinyint DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) NOT NULL,
                           `licenseNumber` varchar(255) NOT NULL,
                           `is_deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) NOT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `table_name_manufacture_fk` (`manufacturer_id`),
                        CONSTRAINT `table_name_manufacture_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cars_drivers` (
                                `driver_id` bigint NOT NULL,
                                `car_id` bigint NOT NULL,
                                KEY `cars_drivers_cars_fk` (`car_id`),
                                KEY `cars_drivers_drivers_fk` (`driver_id`),
                                CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
