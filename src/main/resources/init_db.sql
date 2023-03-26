CREATE SCHEMA `taxi_db` DEFAULT CHARACTER SET utf8;

CREATE TABLE `manufacturers` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(100) DEFAULT NULL,
                        `country` VARCHAR(100) DEFAULT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(100) DEFAULT NULL,
                        `license_number` VARCHAR(100) DEFAULT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `id_UNIQUE` (`id`),
                        UNIQUE KEY `license_number_UNIQUE` (`license_number`));

CREATE TABLE `cars` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `manufacturer_id` bigint DEFAULT NULL,
                        `model` VARCHAR(100) DEFAULT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `manufacturer_id_idx` (`manufacturer_id`),
                        CONSTRAINT `manufacturer_id` FOREIGN KEY (`manufacturer_id`)
                            REFERENCES `manufacturers` (`id`) ON DELETE NO ACTION);

CREATE TABLE `cars_drivers` (
                        `driver_id` BIGINT DEFAULT NULL,
                        `car_id` BIGINT DEFAULT NULL,
                        KEY `driver_id_idx` (`driver_id`),
                        KEY `car_id_idx` (`car_id`),
                        CONSTRAINT `cars_id` FOREIGN KEY (`car_id`)
                            REFERENCES `cars` (`id`) ON DELETE NO ACTION,
                        CONSTRAINT `drivers_id` FOREIGN KEY (`driver_id`)
                            REFERENCES `drivers` (`id`) ON DELETE NO ACTION);
