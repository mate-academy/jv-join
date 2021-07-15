CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                 `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(225) NOT NULL,
                                 `country` VARCHAR(225) NOT NULL,
                                 `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                           `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(225) NOT NULL,
                           `license_number` VARCHAR(225) NOT NULL,
                           `is_deleted` TINYINT NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`),
                           UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                           UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
);
DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                        `manufacture_id` BIGINT(11) NOT NULL,
                        `model` VARCHAR(225) NOT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`),
                        CONSTRAINT `cars_manufacture_id_fk`
                            FOREIGN KEY(`manufacture_id`)
                                REFERENCES `taxi`.`manufacturers` (`id`)
                                ON DELETE NO ACTION
                                ON UPDATE NO ACTION
);
DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT(11) NULL,
                                `car_id` BIGINT(11),
                                CONSTRAINT `cars_drivers_driver_id_fk` FOREIGN KEY(`driver_id`) REFERENCES `taxi`.`drivers` (`id`),
                                CONSTRAINT `cars_drivers_car_id_fk` FOREIGN KEY(`car_id`) REFERENCES `taxi`.`cars` (`id`)
);
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Renault', 'France');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Volkswagen', 'Germany');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Toyota', 'Japan');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Ford', 'United States');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Honda', 'Japan');

INSERT INTO `taxi`.`drivers` (`name`, `license_number`) VALUES ('Bob', '0000_0000_0000_0001');
INSERT INTO `taxi`.`drivers` (`name`, `license_number`) VALUES ('John', '0000_0000_0000_0002');
INSERT INTO `taxi`.`drivers` (`name`, `license_number`) VALUES ('David', '0000_0000_0000_0003');
INSERT INTO `taxi`.`drivers` (`name`, `license_number`) VALUES ('Alice', '0000_0000_0000_0004');
INSERT INTO `taxi`.`drivers` (`name`, `license_number`) VALUES ('Anna', '0000_0000_0000_0005');
