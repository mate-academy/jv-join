CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
`id` BIGINT(11) NOT NULL AUTO_INCREMENT,
`name` VARCHAR(225) NOT NULL,
`country` VARCHAR(225) NOT NULL,
`is_deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
`id` BIGINT(11) NOT NULL AUTO_INCREMENT,
`name` VARCHAR(225) NOT NULL,
`license_number` VARCHAR(225) NOT NULL,
`is_deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`),
UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
`id` BIGINT(11) NOT NULL AUTO_INCREMENT,
`model` VARCHAR(225) NOT NULL,
`manufacturer_id` BIGINT(11) NOT NULL,
`is_deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`),
CONSTRAINT `cars_manufacturer_id`
FOREIGN KEY(`manufacturer_id`)
REFERENCES `taxi_service`.`manufacturers`(`id`)
ON DELETE NO ACTION
ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
`driver_id` BIGINT NOT NULL,
`car_id` BIGINT NOT NULL,
KEY `cars_id_fk` (`car_id`),
KEY `drivers_id_fk` (`driver_id`),
CONSTRAINT `cars_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
CONSTRAINT `drivers_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
