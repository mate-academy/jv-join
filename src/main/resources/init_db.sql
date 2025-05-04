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
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `manufacturer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
                                                 CONSTRAINT `cars_manufacturers_fk`
                                                 FOREIGN KEY (`manufacturer_id`)
                                                 REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
  `driver_id` bigint NOT NULL,
  `car_id` bigint NOT NULL,
  KEY `cars_driver_drivers_fk` (`driver_id`),
  KEY `cars_car_drivers_fk` (`car_id`),
                                       CONSTRAINT `cars_car_drivers_fk`
                                       FOREIGN KEY (`car_id`)
                                       REFERENCES `cars` (`id`),
                                       CONSTRAINT `cars_driver_drivers_fk`
                                       FOREIGN KEY (`driver_id`)
                                       REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
