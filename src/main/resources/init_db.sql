CREATE DATABASE `taxi_service_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) DEFAULT NULL,
                                 `country` varchar(255) DEFAULT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `drivers` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) DEFAULT NULL,
                           `license_number` varchar(255) DEFAULT NULL,
                           `is_deleted` tinyint DEFAULT '0',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cars` (
                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                      `model` VARCHAR(225) NULL,
                      `is_deleted` TINYINT NULL DEFAULT 0,
                      `manufacturer_id` BIGINT,
                      PRIMARY KEY (`id`),
                      CONSTRAINT `cars_manufacturer_manufacturers_id_fk`
                          FOREIGN KEY (`manufacturer_id`)
                              REFERENCES taxi_service_db.manufacturers (`id`)
                              ON DELETE NO ACTION
                              ON UPDATE NO ACTION);

CREATE TABLE `cars_drivers` (
                                `driver_id` BIGINT,
                                `car_id` BIGINT NOT NULL,

                                CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`)
                                    REFERENCES `drivers` (`id`),
                                CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`)
                                    REFERENCES `cars` (`id`)
);