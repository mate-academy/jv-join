CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

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
                                  `id` BIGINT NOT NULL,
                                  `model` VARCHAR(255) NOT NULL,
                                  `manufacturer_id` BIGINT NOT NULL,
                                  `drivers_id` BIGINT NOT NULL,
                                  `is_deleted` TINYINT NOT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);

/* Create relations */
 ALTER TABLE `taxi_db`.`cars`
                                  ADD INDEX `drivers_id_idx` (`drivers_id` ASC) VISIBLE,
                                  ADD INDEX `manufacturer_id_idx` (`manufacturer_id` ASC) VISIBLE;
 ALTER TABLE `taxi_db`.`cars`
                                  ADD CONSTRAINT `manufacturer_id`
                                  FOREIGN KEY (`manufacturer_id`)
                                  REFERENCES `taxi_db`.`manufacturers` (`id`)
                                  ON DELETE NO ACTION
                                  ON UPDATE NO ACTION,
                                  ADD CONSTRAINT `drivers_id`
                                  FOREIGN KEY (`drivers_id`)
                                  REFERENCES `taxi_db`.`drivers` (`id`)
                                  ON DELETE NO ACTION
                                  ON UPDATE NO ACTION;