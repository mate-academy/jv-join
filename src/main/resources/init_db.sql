CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE
`taxi_service`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `drivers`;
DROP TABLE IF EXISTS `manufacturers`;

CREATE TABLE `manufacturers`
(
    `id`         BIGINT(11) NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(225) NOT NULL,
    `country`    VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE `cars`
(
    `id`              BIGINT(11) NOT NULL AUTO_INCREMENT,
    `model`           VARCHAR(255) NOT NULL,
    `manufacturer_id` BIGINT NOT NULL,
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturers_fk`
        FOREIGN KEY (`manufacturer_id`)
            REFERENCES `manufacturers` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

CREATE TABLE `drivers`
(
    `id`             BIGINT(11) NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(225) NOT NULL,
    `license_number` VARCHAR(225) NOT NULL,
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
    UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
);

CREATE TABLE `cars_drivers`
(
    `car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    PRIMARY KEY (`car_id`, `driver_id`),
    CONSTRAINT `cars_drivers_cars_fk`
        FOREIGN KEY (`car_id`)
            REFERENCES `cars` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `cars_drivers_drivers_fk`
        FOREIGN KEY (`driver_id`)
            REFERENCES `drivers` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
