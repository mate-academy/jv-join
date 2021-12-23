CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

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
  `manufacturer_id` bigint NOT NULL,
  `model` varchar(225) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `manufacturer_id_idx` (`manufacturer_id`),
  CONSTRAINT `manufacturer_id` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
  `driver_id` BIGINT(11) NOT NULL,
  `car_id` BIGINT(11) NOT NULL,
  INDEX `driver_id_idx` (`driver_id` ASC) VISIBLE,
  INDEX `car_id_idx` (`car_id` ASC) VISIBLE,
  CONSTRAINT `driver_id`
    FOREIGN KEY (`driver_id`)
    REFERENCES `drivers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `cars` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
