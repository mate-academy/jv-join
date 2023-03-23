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

create table `cars`(
                       `id` bigint not null auto_increment,
                       `manufacturer_id` bigint not null,
                       `model` varchar(255) not null,
                       `is_deleted` tinyint not null default 0,
                       primary key (`id`),
                       constraint `manufacturer_cars_fk` foreign key (`manufacturer_id`) references `manufacturers` (`id`)
);

create table `cars_drivers`(
                               `driver_id` bigint not null,
                               `car_id` bigint not null,
                               constraint `cars_drivers_driver_fk` foreign key (`driver_id`) references `drivers` (`id`),
                               constraint `cars_drivers_car_fk` foreign key (`car_id`) references `cars` (`id`)
);