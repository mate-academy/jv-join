CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `taxi_service`.`manufacturers`;
CREATE TABLE `taxi_service`.`manufacturers` (
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(225) NOT NULL,
    `country` VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `taxi_service`.`drivers`;
CREATE TABLE `taxi_service`.`drivers` (
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `licence_number` VARCHAR(45) NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `taxi_service`.`cars`;
CREATE TABLE `taxi_service`.`cars` (
	`id` BIGINT(11) NOT NULL AUTO_INCREMENT,
    `model` VARCHAR(255) NOT NULL,
    `manufacturer_id` BIGINT(11) NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturers_FK`
		FOREIGN KEY (`manufacturer_id`)
        REFERENCES `taxi_service`.`manufacturers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `taxi_service`.`cars_drivers`;
CREATE TABLE `taxi_service`.`cars_drivers` (
    `car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    PRIMARY KEY (car_id, driver_id),
    CONSTRAINT `cars_drivers_cars_FK` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
    CONSTRAINT `cars_drivers_drivers_FK` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
