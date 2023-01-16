CREATE SCHEMA IF NOT EXISTS `taxi_db` DEFAULT CHARACTER SET utf8;
USE `taxi_db`;

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `license_number` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model` varchar(255) DEFAULT NULL,
  `manufacturer_id` bigint(11) NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `cars_manufacturers_fk`
  FOREIGN KEY (`manufacturer_id`)
  REFERENCES `manufacturers` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION
);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
  `car_id` bigint(11) NOT NULL,
  `driver_id` bigint(11) NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  CONSTRAINT `cars_drivers_cars_fk`
  FOREIGN KEY (`car_id`)
  REFERENCES `cars`(`id`)
						ON DELETE NO ACTION
                        ON UPDATE NO ACTION,
  CONSTRAINT `cars_driers_drivers_fk`
  FOREIGN KEY (`driver_id`)
  REFERENCES `drivers`(`id`)
  						ON DELETE NO ACTION
                        ON UPDATE NO ACTION
);