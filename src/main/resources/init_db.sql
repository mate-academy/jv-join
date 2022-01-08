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

CREATE TABLE `taxi`.`cars` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(255) NULL,
  `is_deleted` TINYINT NULL DEFAULT 0,
  `manufacturer_id` BIGINT(11) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `manufacturer_id_fk`
								FOREIGN KEY (`manufacturer_id`)
									REFERENCES `taxi`.`manufacturers` (`id`)
                                    ON UPDATE NO ACTION
                                    ON DELETE NO ACTION
  );

CREATE TABLE `taxi`.`cars_drivers` (
  `driver_id` BIGINT(11) NOT NULL,
  `car_id` BIGINT(11) NOT NULL,
  CONSTRAINT `driver_id_fk`
								FOREIGN KEY (`driver_id`)
									REFERENCES `taxi`.`drivers` (`id`)
                                    ON UPDATE NO ACTION
                                    ON DELETE NO ACTION,
  CONSTRAINT `car_id_fk`
								FOREIGN KEY (`car_id`)
									REFERENCES `taxi`.`cars` (`id`)
                                    ON UPDATE NO ACTION
                                    ON DELETE NO ACTION
  );
