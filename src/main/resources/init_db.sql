CREATE SCHEMA `taxi_service`;

CREATE TABLE `taxi_service`.`manufacturers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NULL,
    `country` VARCHAR(45) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`));

CREATE TABLE `taxi_service`.`drivers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NULL,
    `licence_number` VARCHAR(45) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`));

CREATE TABLE `taxi_service`.`cars` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `manufacturer_id` BIGINT NULL,
    `model` VARCHAR(45) NULL,
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `fk_cars_manufacturers_idx` (`manufacturer_id` ASC) VISIBLE,
    CONSTRAINT `fk_cars_manufacturers`
    FOREIGN KEY (`manufacturer_id`)
        REFERENCES `taxi_service`.`manufacturers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);
	
CREATE TABLE `taxi_service`.`cars_drivers` (
    `car_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    PRIMARY KEY (`car_id`, `driver_id`),
    INDEX `fk_drivers_idx` (`driver_id` ASC) VISIBLE,
    CONSTRAINT `fk_cars`
    FOREIGN KEY (`car_id`)
        REFERENCES `taxi_service`.`cars` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `fk_drivers`
    FOREIGN KEY (`driver_id`)
        REFERENCES `taxi_service`.`drivers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);
