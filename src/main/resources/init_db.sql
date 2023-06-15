CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(225) NOT NULL,
  `country` VARCHAR(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(225) NOT NULL,
  `license_number` VARCHAR(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
   PRIMARY KEY (`id`),

CREATE TABLE `cars` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `manufacturer_id` bigint(11) NOT NULL,
  `model` varchar(225) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`));

CREATE TABLE `cars_drivers` (
   `driver_id` bigint(11) NOT NULL,
   `car_id` bigint(11) NOT NULL,
   KEY `cars_drivers_drivers_fk` (`driver_id`),
   KEY `cars_drivers_cars_fk` (`car_id`),
   CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
   CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`));
