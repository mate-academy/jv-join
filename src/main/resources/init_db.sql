CREATE SCHEMA IF NOT EXISTS `taxi` DEFAULT CHARACTER SET utf8;
USE `taxi`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `title` VARCHAR(225) NOT NULL,
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
                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                   `title` VARCHAR(45) NULL,
                                   `is_deleted` TINYINT NULL DEFAULT 0,
                                 	`manufacturer_id` BIGINT NOT NULL,
                                 	`driver_id` BIGINT NOT NULL,
                                   PRIMARY KEY (`id`),
                                   CONSTRAINT `manufacturer_id_fk`
                                     FOREIGN KEY (`manufacturer_id`)
                                     REFERENCES `taxi`.`manufacturers` (`id`)
                                     ON DELETE NO ACTION
                                     ON UPDATE NO ACTION,
                                   CONSTRAINT `driver_id_fk`
                                     FOREIGN KEY (`driver_id`)
                                     REFERENCES `taxi`.`drivers` (`id`)
                                     ON DELETE NO ACTION
                                     ON UPDATE NO ACTION)
                                 ENGINE = InnoDB
                                 DEFAULT CHARACTER SET = utf8;

CREATE TABLE `taxi`.`cars_drivers` (
  `car_id` BIGINT(125) NOT NULL,
  `driver_Id` BIGINT(125) NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `car_id_idx` (`car_id` ASC),
  INDEX `driver_id_idx` (`driver_Id` ASC),
  CONSTRAINT `car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `taxi`.`cars` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `driver_id`
    FOREIGN KEY (`driver_Id`)
    REFERENCES `taxi`.`drivers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE `taxi`.`cars_drivers`
DROP FOREIGN KEY `car_id`;
ALTER TABLE `taxi`.`cars_drivers`
CHANGE COLUMN `car_id` `car_id` BIGINT(125) NULL DEFAULT NULL ;
ALTER TABLE `taxi`.`cars_drivers`
ADD CONSTRAINT `car_id`
  FOREIGN KEY (`car_id`)
  REFERENCES `taxi`.`cars` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

  ALTER TABLE `taxi`.`cars_drivers`
  DROP FOREIGN KEY `car_id`,
  DROP FOREIGN KEY `driver_id`;
  ALTER TABLE `taxi`.`cars_drivers`
  CHANGE COLUMN `car_id` `car_id` BIGINT(125) NOT NULL ,
  CHANGE COLUMN `driver_Id` `driver_Id` BIGINT(125) NOT NULL ;
  ALTER TABLE `taxi`.`cars_drivers`
  ADD CONSTRAINT `car_id`
    FOREIGN KEY (`car_id`)
    REFERENCES `taxi`.`cars` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  ADD CONSTRAINT `driver_id`
    FOREIGN KEY (`driver_Id`)
    REFERENCES `taxi`.`drivers` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

    ALTER TABLE `taxi`.`cars_drivers`
    ADD COLUMN `id` BIGINT(125) NOT NULL AUTO_INCREMENT AFTER `is_deleted`,
    ADD PRIMARY KEY (`id`);
    ;

ALTER TABLE `taxi`.`cars_drivers`
DROP FOREIGN KEY `driver_id`;
ALTER TABLE `taxi`.`cars_drivers`
DROP COLUMN `driver_Id`,
DROP INDEX `driver_id_idx` ;
;

ALTER TABLE `taxi`.`cars`
DROP FOREIGN KEY `driver_id_fk`;
ALTER TABLE `taxi`.`cars`
DROP COLUMN `driver_id`,
DROP INDEX `driver_id_fk` ;
;

INSERT INTO `taxi`.`drivers` (`id`, `name`, `license_number`, `is_deleted`) VALUES ('-1', 'Unnamed', '0', '1');

UPDATE `taxi`.`drivers` SET `id` = '0' WHERE (`id` = '-1');

DELETE FROM `taxi`.`drivers` WHERE (`id` = '0');

ALTER TABLE `taxi`.`cars_drivers`
ADD COLUMN `driver_id` BIGINT(125) NOT NULL AFTER `id`,
ADD INDEX `driver_id_idx` (`driver_id` ASC);
;
ALTER TABLE `taxi`.`cars_drivers`
ADD CONSTRAINT `driver_id`
  FOREIGN KEY (`driver_id`)
  REFERENCES `taxi`.`drivers` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `taxi`.`cars_drivers`
DROP COLUMN `id`,
DROP PRIMARY KEY;
;
