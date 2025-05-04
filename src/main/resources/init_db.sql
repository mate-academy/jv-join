CREATE DATABASE `project_db` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE `manufacturers` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

CREATE TABLE `drivers` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`));
CREATE TABLE `cars` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
    `model` VARCHAR(255) NOT NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `manufacturer_id` BIGINT,
    PRIMARY KEY(`id`),
    CONSTRAINT `cars_manufacturers_fk`
									FOREIGN KEY(`manufacturer_id`)
                                    REFERENCES `project_db`.`manufacturers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION);
CREATE TABLE `cars_drivers` (
  `car_id` BIGINT NOT NULL,
  `driver_id` BIGINT NOT NULL,
  CONSTRAINT `cars_drivers_cars_fk`
                                    FOREIGN KEY (`car_id`)
                                    REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk`
                                    FOREIGN KEY (`driver_id`)
                                    REFERENCES `drivers` (`id`));
