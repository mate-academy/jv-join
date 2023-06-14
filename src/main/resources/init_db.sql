DROP TABLE `library_db`.`cars`, `library_db`.`cars_drivers`, `library_db`.`drivers`, `library_db`.`manufacturers`;
CREATE TABLE `manufacturers` (
                                 `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                 `name` varchar(45) DEFAULT NULL,
                                 `country` varchar(45) DEFAULT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(45) DEFAULT NULL,
                           `license_number` varchar(45) DEFAULT NULL,
                           `is_deleted` tinyint NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id`),
                           UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                           UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE
)ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars` (
                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                        `manufacturer_id` BIGINT NOT NULL,
                        `model` VARCHAR(225) NOT NULL,
                        `is_deleted` TINYINT NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        KEY `cars_manufacturers_fk` (`manufacturer_id`),
                        CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT NOT NULL,
                                `car_id` BIGINT NOT NULL,
                                KEY `cars_drivers_cars_fk` (`car_id`),
                                KEY `cars_drivers_drivers_fk` (`driver_id`),
                                CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
                                CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
