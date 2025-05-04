CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

# DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers`
(
    `id`         BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(225) NOT NULL,
    `country`    VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

# DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers`
(
    `id`             BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(225) NOT NULL,
    `license_number` VARCHAR(225) NOT NULL,
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
    UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

# DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars`
(
    `id`              bigint  NOT NULL AUTO_INCREMENT,
    `model`           varchar(255)     DEFAULT NULL,
    `manufacturer_id` bigint           DEFAULT NULL,
    `is_deleted`      tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `car_manufacturer_fk` (`manufacturer_id`),
    CONSTRAINT `car_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;

# DROP TABLE IF EXISTS `drivers_cars`;
CREATE TABLE `drivers_cars`
(
    `driver_id` bigint NOT NULL,
    `car_id`    bigint NOT NULL,
    KEY `drivers_cars_driver_fk` (`driver_id`),
    KEY `drivers_cars_car_fk` (`car_id`),
    CONSTRAINT `drivers_cars_car_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `drivers_cars_driver_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;
