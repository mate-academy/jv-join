
CREATE SCHEMA IF NOT EXISTS `taxi_service_schema` DEFAULT CHARACTER SET utf8;
USE `taxi_service_schema`;

CREATE TABLE `manufacturers` (
                                 `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(225) NOT NULL,
                                 `country` VARCHAR(225) NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(45) DEFAULT NULL,
                           `license_number` varchar(45) DEFAULT NULL,
                           `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`)
);

CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) DEFAULT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        `manufacturer_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturer_id_fk` (`manufacturer_id`),
    CONSTRAINT `cars_manufacturer_id_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
)

CREATE TABLE `cars_drivers` (
	`car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    CONSTRAINT `cars_drivers_cars_fk`
										FOREIGN KEY (`car_id`)
                                        REFERENCES `taxi_service_schema`.`cars` (`id`),
	CONSTRAINT `cars_drivers_drivers_fk`
										FOREIGN KEY (`driver_id`)
                                        REFERENCES `taxi_service_schema`.`drivers` (`id`)
);
