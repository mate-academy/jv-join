CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `drivers`;
DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(225) NOT NULL,
    `country` varchar(225) NOT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(225) NOT NULL,
    `license_number` varchar(225) NOT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`));

CREATE TABLE `cars` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `manufacturer_id` bigint NOT NULL,
    `model` varchar(225) NOT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    CONSTRAINT `manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
    `car_id` bigint NOT NULL,
    `driver_id` bigint NOT NULL,
    KEY `car_fk_idx` (`car_id`),
    KEY `driver_fk_idx` (`driver_id`),
    CONSTRAINT `car_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
    CONSTRAINT `driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));

