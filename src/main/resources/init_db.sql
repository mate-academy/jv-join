CREATE DATABASE `taxi_service` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin DEFAULT ENCRYPTION='N';
USE `taxi_service`;

CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                           `license_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                           `is_deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

CREATE TABLE `cars` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `model` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                        `manufacturer_id` bigint NOT NULL,
                        `is_deleted` tinyint NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturer_id` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturer_id` FOREIGN KEY (`manufacturer_id`)
                            REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

CREATE TABLE `car_driver` (
                              `car_id` bigint DEFAULT NULL,
                              `driver_id` bigint DEFAULT NULL,
                              KEY `car_id_fk_idx` (`car_id`),
                              KEY `driver_id_fk_idx` (`driver_id`),
                              CONSTRAINT `car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                              CONSTRAINT `driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
