CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `country` VARCHAR(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(255) NOT NULL,
                           `license_number` VARCHAR(255) NOT NULL,
                           `is_deleted` TINYINT NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `model` VARCHAR(225) NOT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        `manufacturer_id` BIGINT DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT NOT NULL,
                                `car_id` BIGINT NOT NULL,
                                KEY `cars_id_fk` (`car_id`),
                                KEY `drivers_id_fk` (`driver_id`),
                                CONSTRAINT `cars_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `drivers_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;