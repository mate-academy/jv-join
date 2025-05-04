CREATE DATABASE `taxi_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `manufacturers` (
    `id`         bigint  NOT NULL AUTO_INCREMENT,
    `name`       varchar(255)     DEFAULT NULL,
    `country`    varchar(255)     DEFAULT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `drivers`(
    `id`             bigint  NOT NULL AUTO_INCREMENT,
    `name`           varchar(255)     DEFAULT NULL,
    `license_number` varchar(255)     DEFAULT NULL,
    `is_deleted`     tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `cars`(
    `id`              bigint       NOT NULL AUTO_INCREMENT,
    `model`           varchar(255) NOT NULL,
    `is_deleted`      tinyint      NOT NULL DEFAULT '0',
    `manufacturer_id` bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `cars_manufacturers_fk` (`manufacturer_id`),
    CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `cars_drivers` (
    `car_id`     bigint  NOT NULL,
    `driver_id`  bigint  NOT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    KEY `cars_drivers_car_id` (`car_id`),
    KEY `cars_drivers_driver_id` (`driver_id`),
    CONSTRAINT `cars_drivers_car_id` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
    CONSTRAINT `cars_drivers_driver_id` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



