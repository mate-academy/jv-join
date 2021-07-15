CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE `manufacturers`;
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
    `id`              BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `model`           VARCHAR(255) NOT NULL,
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0,
    `manufacturer_id` BIGINT(11)   NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers`
(
    `car_id`    BIGINT(11) NOT NULL,
    `driver_id` BIGINT(11) NOT NULL,
    CONSTRAINT `cars_drivers_car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `taxi`.`cars` (`id`),
    CONSTRAINT `cars_drivers_driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `taxi`.`drivers` (`id`)
);

INSERT INTO `manufacturers` (`name`, `country`)
values ('BMW', 'Germany'),
       ('TOYOTA', 'Japan'),
       ('Reno', 'France'),
       ('Lada', 'Russia');

INSERT INTO drivers (`name`, `license_number`)
values ('Daniel', 'KD1234'),
       ('Andrew', 'KA1234'),
       ('Vlada', 'BV1234'),
       ('Alex', 'GA1234');

INSERT INTO cars (`model`, `manufacturer_id`)
values ('Rav4', 2),
       ('X5', 1),
       ('Logan', 3),
       ('Priora', 4);

INSERT INTO `cars_drivers` (`car_id`, `driver_id`)
values   (1, 1),
         (2, 1),
         (3, 1),
         (4, 1),
         (2, 2),
         (3, 3),
         (4, 4);
SET FOREIGN_KEY_CHECKS = 1;


