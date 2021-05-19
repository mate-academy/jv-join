CREATE SCHEMA `manufacturers_db` DEFAULT CHARACTER SET UTF8MB4 ;

DROP TABLE IF EXISTS `manufacturers_db`.`manufacturers`;
DROP TABLE IF EXISTS `manufacturers_db`.`drivers`;
DROP TABLE IF EXISTS `manufacturers_db`.`cars`;
DROP TABLE IF EXISTS `manufacturers_db`.`cars_drivers`;

CREATE TABLE `manufacturers_db`.`manufacturers` (
`id` BIGINT NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) NOT NULL,
`country` VARCHAR(255) NOT NULL,
`deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`));

CREATE TABLE `manufacturers_db`.`drivers` (
`id` BIGINT NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) NOT NULL,
`license_number` VARCHAR(255) NOT NULL,
`deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`));

CREATE TABLE `manufacturers_db`.`cars` (
`id` BIGINT NOT NULL AUTO_INCREMENT,
`model` VARCHAR(255) NOT NULL,
`manufacturer_id` BIGINT NOT NULL,
`deleted` TINYINT NOT NULL DEFAULT 0,
PRIMARY KEY (`id`),
KEY `cars_manufacturers_fk` (`manufacturer_id`),
CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars_drivers` (
`driver_id` bigint NOT NULL,
`car_id` bigint NOT NULL,
UNIQUE KEY `unique_index` (`driver_id`,`car_id`),
KEY `cars_drivers_cars_fk` (`car_id`),
CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


