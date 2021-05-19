CREATE DATABASE `manufacturers` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(45) DEFAULT NULL,
                                 `country` varchar(45) DEFAULT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(45) DEFAULT NULL,
                           `licence_number` varchar(20) DEFAULT NULL,
                           `is_deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `cars` (
                      `id` bigint NOT NULL AUTO_INCREMENT,
                      `model` varchar(255) DEFAULT NULL,
                      `registration_number` varchar(255) DEFAULT NULL,
                      `is_deleted` tinyint NOT NULL DEFAULT '0',
                      `manufacturer_id` bigint DEFAULT NULL,
                      PRIMARY KEY (`id`),
                      KEY `cars_manufacturer_fk` (`manufacturer_id`),
                      CONSTRAINT `cars_manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
CREATE TABLE `cars_drivers` (
                              `car_id` bigint NOT NULL,
                              `driver_id` bigint NOT NULL,
                              KEY `cars_drivers_cars_fk` (`car_id`),
                              KEY `cars_drivers_drivers_fk` (`driver_id`),
                              CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                              CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;