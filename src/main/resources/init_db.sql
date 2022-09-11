CREATE SCHEMA if NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE
`taxi_service`;

CREATE TABLE `manufacturers`
(
    `id`         bigint(11) NOT NULL AUTO_INCREMENT,
    `name`       varchar(225) NOT NULL,
    `country`    varchar(225) NOT NULL,
    `is_deleted` tinyint      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE `drivers`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT,
    `name`           varchar(225) NOT NULL,
    `license_number` varchar(225) NOT NULL,
    `is_deleted`     tinyint      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
    UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
);

CREATE TABLE `cars`
(
    `id`              bigint(11) NOT NULL AUTO_INCREMENT,
    `manufacturer_id` bigint(11) NOT NULL,
    `model`           varchar(225) NOT NULL,
    `is_deleted`      tinyint      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturers_fk`
        FOREIGN KEY (`manufacturer_id`)
            REFERENCES `manufacturers` (`id`)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
);

CREATE TABLE `cars_drivers`
(
    `car_id`    bigint(11) NOT NULL,
    `driver_id` bigint(11) NOT NULL,
    CONSTRAINT `cars_drivers_cars_fk`
        FOREIGN KEY (`car_id`)
            REFERENCES `cars` (`id`)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
    CONSTRAINT `cars_drivers_drivers_fk`
        FOREIGN KEY (`driver_id`)
            REFERENCES `drivers` (`id`)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
);
