CREATE SCHEMA IF NOT EXISTS `taxi_service_db` DEFAULT CHARACTER SET utf8;
USE `taxi_service_db`;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `manufacturers`;
DROP TABLE IF EXISTS `drivers`;

CREATE TABLE `manufacturers` (
                                 `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) DEFAULT NULL,
                                 `country` varchar(255) DEFAULT NULL,
                                 `is_deleted` tinyint(4) DEFAULT 0,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `drivers` (
                           `id` bigint(11) NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) DEFAULT NULL,
                           `license_number` varchar(255) DEFAULT NULL,
                           `is_deleted` tinyint(4) DEFAULT 0,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

CREATE TABLE `cars` (
                        `id` bigint(11) NOT NULL AUTO_INCREMENT,
                        `model` varchar(255) DEFAULT NULL,
                        `manufacturer_id` bigint(11) DEFAULT NULL,
                        `is_deleted` tinyint(4) NOT NULL DEFAULT 0,
                        PRIMARY KEY (`id`),
                        KEY `car_manufacturer_fk` (`manufacturer_id`),
                        CONSTRAINT `car_manufacturer_fk`
                            FOREIGN KEY (`manufacturer_id`)
                            REFERENCES `manufacturers` (`id`)
                            ON DELETE NO ACTION
                            ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `cars_drivers` (
                                `car_id` bigint(11) NOT NULL,
                                `driver_id` bigint(11) NOT NULL,
                                KEY `cars_drivers_cars_fk` (`car_id`),
                                KEY `cars_drivers_drivers_fk` (`driver_id`),
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
