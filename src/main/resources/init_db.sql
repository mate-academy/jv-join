CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

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

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) NOT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
                                `driver_id` bigint NOT NULL,
                                `car_id` bigint NOT NULL,
                                KEY `cars_drivers_driver_id_fk` (`driver_id`),
                                KEY `cars_drivers_car_id_fk` (`car_id`),
                                CONSTRAINT `cars_drivers_car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO cars (model, manufacturer_id) VALUES('Mozzarella', 3);
INSERT INTO cars (model, manufacturer_id) VALUES('Pizza', 1);
INSERT INTO cars (model, manufacturer_id) VALUES('Bratwurst', 2);

INSERT INTO drivers (name, license_number) VALUES('Bob' '12345');
INSERT INTO drivers (name, license_number) VALUES('Alice' '777');
INSERT INTO drivers (name, license_number) VALUES('John' '1234567890');

INSERT INTO cars_drivers (driver_id, car_id) VALUES(1, 1);
INSERT INTO cars_drivers (driver_id, car_id) VALUES(3, 3);
INSERT INTO cars_drivers (driver_id, car_id) VALUES(2, 1);
INSERT INTO cars_drivers (driver_id, car_id) VALUES(3, 2);

INSERT INTO manufacturers (name, country) VALUES('Ferrary', 'Italy');
INSERT INTO manufacturers (name, country) VALUES('Audi', 'Germany');
INSERT INTO manufacturers (name, country) VALUES('Fiat', 'Italy');