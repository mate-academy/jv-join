CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
    CREATE TABLE `manufacturers`.`manufacturers` (
    `id` BIGINT(55) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NULL,
    `country` VARCHAR(45) NULL,
    `is_deleted` TINYINT(0) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
    CREATE TABLE `manufacturers`.`drivers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NULL,
    `license_number` VARCHAR(45) NULL,
    `is_deleted` TINYINT(0) NOT NULL DEFAULT 0,
     PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `cars`;
    CREATE TABLE `cars` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `model` VARCHAR(255) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `manufacturer_id` BIGINT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturers_fk`
        FOREIGN KEY (`manufacturer_id`)
        REFERENCES `manufacturers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
    `car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    CONSTRAINT `cars_drivers_cars_fk`cars_drivers
        FOREIGN KEY (`car_id`)
        REFERENCES `cars` (`id`),
    CONSTRAINT `cars_drivers_drivers_fk`
        FOREIGN KEY (`driver_id`)
        REFERENCES `drivers` (`id`));