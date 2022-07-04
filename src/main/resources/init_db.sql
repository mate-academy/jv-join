CREATE DATABASE `taxi_service_db` / * ! 40100 DEFAULT CHARACTER
SET utf8mb4 COLLATE utf8mb4_0900_ai_ci * / / * ! 80016 DEFAULT ENCRYPTION = 'N' */;

CREATE TABLE `manufacturers`
(
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_cs_0900_ai_ci DEFAULT NULL,
  `country` varchar(255) COLLATE utf8mb4_cs_0900_ai_ci DEFAULT NULL,
  `is_deleted` varchar(45) COLLATE utf8mb4_cs_0900_ai_ci NOT NULL DEFAULT '0',
PRIMARY KEY(`id`)
);

CREATE TABLE `drivers`
(
  `id`             bigint NOT NULL AUTO_INCREMENT,
  `name`           varchar(255) DEFAULT NULL,
  `license_number` varchar(255) DEFAULT NULL,
  `is_deleted`     tinyint NOT NULL DEFAULT '0',
PRIMARY KEY(`id`)
);

CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `manufacturer_id` bigint NOT NULL,
  PRIMARY KEY (`id`), KEY `cars_manufacturers_fk`(`manufacturer_id`),
CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY(`manufacturer_id`)REFERENCES `manufacturers`(`id`)
);

CREATE TABLE `cars_drivers` (
  `cars_id` bigint NOT NULL,
  `drivers_id` bigint NOT NULL,
CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`cars_id`) REFERENCES `carі` (`id`),
CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`drivers_id`) REFERENCES `drivers` (`id`)
) ;