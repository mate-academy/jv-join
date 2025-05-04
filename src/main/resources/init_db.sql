CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers`
(
    `id`         BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(225) NOT NULL,
    `country`    VARCHAR(225) NOT NULL,
    `is_deleted` TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers`
(
    `id`             BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(225) NOT NULL,
    `license_number` VARCHAR(225) NOT NULL,
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
    UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
);

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars`
(
    `id`              bigint       NOT NULL auto_increment,
    `model`           varchar(255) null,
    `is_deleted`      tinyint      not null default 0,
    `manufacturer_id` bigint       null,
    primary key (`id`),
    constraint `cars_manufacturer_fk` foreign key (`manufacturer_id`) references `manufacturers` (`id`)
        on delete no action
        on update no action
);

DROP TABLE IF EXISTS `cars_drivers`;
create table `cars_drivers`
(
    `car_id`    bigint not null,
    `driver_id` bigint not null,

    constraint `cars_drivers_cars_fk` foreign key (`car_id`) references `cars` (`id`),
    constraint `cars_drivers_drivers_fk` foreign key (`driver_id`) references `drivers` (`id`)
);
