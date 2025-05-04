CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `name` varchar(45) DEFAULT NULL,
                                  `country` varchar(45) DEFAULT NULL,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0',
                                  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=149 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(45) DEFAULT NULL,
                          `license_number` varchar(45) DEFAULT NULL,
                          `is_deleted` tinyint NOT NULL DEFAULT '0',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `model` varchar(255) DEFAULT NULL,
                              `is_deleted` tinyint NOT NULL DEFAULT '0',
                              `manufacturer_id` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `cars_manufacturers_fk` (`manufacturer_id`),
                              CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                          `car_id` bigint NOT NULL,
                          `driver_id` bigint NOT NULL,
                          KEY `cars_drivers_drivers_fk` (`driver_id`),
                          KEY `cars_drivers_cars_fk` (`car_id`),
                          CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                          CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;