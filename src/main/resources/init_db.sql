CREATE SCHEMA IF NOT EXISTS taxi_service_join DEFAULT CHARACTER SET utf8;
USE taxi_service_join;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
    `id`         BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(225) NOT NULL,
    `country`    VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
    `id`             BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(225) NOT NULL,
    `license_number` VARCHAR(225) NOT NULL,
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
    UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
);

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
    `id`              BIGINT(11)  NOT NULL AUTO_INCREMENT,
    `manufacturer_id` BIGINT(11)  NOT NULL,
    `model`           VARCHAR(45) NOT NULL,
    `is_deleted`      TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturerId_fk`
    FOREIGN KEY (`manufacturer_id`)
    REFERENCES `taxi_service_join`.`manufacturers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
    `driver_id` BIGINT(11) NOT NULL,
    `car_id`    BIGINT(11) NOT NULL,
    CONSTRAINT `drivers_id_fk`
    FOREIGN KEY (`driver_id`)
    REFERENCES `taxi_service_join`.`drivers` (`id`),
    CONSTRAINT `car_id_fk`
    FOREIGN KEY (`car_id`)
    REFERENCES `taxi_service_join`.`cars` (`id`)
);
