DROP DATABASE IF EXISTS taxi_service_db;

CREATE DATABASE taxi_service_db DEFAULT CHAR SET utf8mb4;

USE taxi_service_db;

CREATE TABLE manufacturers (
	`id` BIGINT UNIQUE NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(64) NULL,
    `country` VARCHAR(32) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`id`)
);

CREATE TABLE drivers (
	`id` BIGINT UNIQUE NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(64) NULL,
    `license_number` VARCHAR(32) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`id`)    
);

CREATE TABLE cars (
	`id` BIGINT UNIQUE NOT NULL AUTO_INCREMENT,
    `model` VARCHAR(64) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT FALSE,
    `manufacturer_id` BIGINT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_to_manufacturers_fk`
    FOREIGN KEY (`manufacturer_id`) REFERENCES taxi_service_db.manufacturers(`id`)
);

CREATE TABLE cars_drivers (
	`driver_id` BIGINT,
    `car_id` BIGINT,
    PRIMARY KEY (`driver_id`, `car_id`),
    CONSTRAINT `cars_drivers_to_drivers_fk`
    FOREIGN KEY (`driver_id`) REFERENCES taxi_service_db.drivers(`id`),
    CONSTRAINT `cars_drivers_to_cars_fk`
    FOREIGN KEY (`car_id`) REFERENCES taxi_service_db.cars(`id`)
);
