CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
    `id`         bigint  NOT NULL AUTO_INCREMENT,
    `name`       varchar(255)     DEFAULT NULL,
    `country`    varchar(255)     DEFAULT NULL,
    `is_deleted` tinyint NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb3;

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
    `id`             bigint  NOT NULL AUTO_INCREMENT,
    `name`           varchar(255)                                            DEFAULT NULL,
    `license_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `is_deleted`     tinyint NOT NULL                                        DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb3;

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
    `id`              bigint  NOT NULL AUTO_INCREMENT,
    `manufacturer_id` bigint                                                  DEFAULT NULL,
    `model`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `is_deleted`      tinyint NOT NULL                                        DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `manufacturer_id` (`manufacturer_id`),
    CONSTRAINT `cars_ibfk_1` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb3;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
    `driver_id` bigint DEFAULT NULL,
    `car_id`    bigint DEFAULT NULL,
    KEY `car_id` (`car_id`),
    KEY `driver_id` (`driver_id`),
    CONSTRAINT `cars_drivers_ibfk_2` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
    CONSTRAINT `cars_drivers_ibfk_3` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3;
