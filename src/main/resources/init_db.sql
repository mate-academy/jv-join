CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE
IF EXISTS `manufacturers`;
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

CREATE TABLE `cars` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
    `model` VARCHAR(255) NULL,
    `manufacturer_id` BIGINT NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT  `cars_manufacturer_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers`
     (`id`)
    );
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Buick', '30');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Ford', '1');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Tavria', '2');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Cadillac', '30');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Buick', '30');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Chevrolet', '30');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('GMC', '30');
INSERT INTO `taxi_service_db`.`cars` (`model`, `manufacturer_id`) VALUES ('Lincoln', '1');


CREATE TABLE `cars_drivers` (
`driver_id` BIGINT NOT NULL,
`car_id` BIGINT NOT NULL,
CONSTRAINT  `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`),
CONSTRAINT  `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
);
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('1', '1');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('1', '2');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('1', '3');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('2', '1');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('2', '1');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('2', '5');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('2', '6');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('3', '6');
INSERT INTO `taxi_service_db`.`cars_drivers` (`driver_id`, `car_id`) VALUES ('4', '5');