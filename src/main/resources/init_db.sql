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
  `model` varchar(45) DEFAULT NULL,
  `is_deleted` tinyint DEFAULT '0',
  `manufacturer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cars_manufacture_id` (`manufacturer_id`),
  CONSTRAINT `fk_cars_manufacture_id` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb3;
DROP TABLE IF EXISTS `cars_drivers`;
    CREATE TABLE `cars_drivers` (
      `driver_id` bigint NOT NULL,
      `car_id` bigint NOT NULL,
      KEY `fk_car_drivers_driver_id_idx` (`driver_id`),
      KEY `fk_car_drivers_car_id_idx` (`car_id`),
      CONSTRAINT `fk_car_drivers_car_id` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
      CONSTRAINT `fk_car_drivers_driver_id` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
