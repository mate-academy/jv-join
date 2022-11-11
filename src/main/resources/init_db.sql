CREATE SCHEMA IF NOT EXISTS `taxi_db` DEFAULT CHARACTER SET utf8;
USE `taxi_db`;

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

/* create table cars */
  CREATE TABLE `taxi_db`.`cars` (
                                  `id` BIGINT(11) NOT NULL,
                                  `model` VARCHAR(255) NOT NULL,
                                  `manufacturer_id` BIGINT NOT NULL,
                                  `is_deleted` TINYINT NOT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);

/* create table cars_drivers */
  CREATE TABLE `taxi_db`.`cars_drivers` (
                                  `driver_id` BIGINT(11) NOT NULL,
                                  `car_id` BIGINT(11) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL);

/* Create relations */
 ALTER TABLE `taxi_db`.`cars`
                                  ADD INDEX `manufacturer_id_idx` (`manufacturer_id` ASC) VISIBLE;

 ALTER TABLE `taxi_db`.`cars`
                                  ADD CONSTRAINT `manufacturer_id`
                                    FOREIGN KEY (`manufacturer_id`)
                                    REFERENCES `taxi_db`.`manufacturers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION;

ALTER TABLE `taxi_db`.`cars_drivers`
                                  ADD INDEX `driver_id_idx` (`driver_id` ASC) VISIBLE,
                                  ADD INDEX `car_id_idx` (`car_id` ASC) VISIBLE;
;
ALTER TABLE `taxi_db`.`cars_drivers`
                                  ADD CONSTRAINT `driver_id`
                                    FOREIGN KEY (`driver_id`)
                                    REFERENCES `taxi_db`.`drivers` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION,
                                  ADD CONSTRAINT `car_id`
                                    FOREIGN KEY (`car_id`)
                                    REFERENCES `taxi_db`.`cars` (`id`)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION;
