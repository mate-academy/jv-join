CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                `name` VARCHAR(225) NOT NULL,
                                `country` VARCHAR(225) NOT NULL,
                                `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                PRIMARY KEY (`id`));

INSERT INTO `manufacturers` (`id`, `name`, `country`) VALUES
							(1,'Acura','Japan'),
							(2,'Alfa Romeo','Italy'),
							(3,'Audi','Germany'),
							(4,'BMW','Germany'),
							(5,'Ferrari','Italy'),
							(6,'SEAT','Spain'),
							(7,'Renault','France'),
							(8,'Peugeot','France'),
							(9,'Fiat','Italy'),
							(10,'Honda','Japan'),
							(11,'Lexus','Japan'),
							(12,'Toyota','Japan');

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                          `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                          `name` VARCHAR(225) NOT NULL,
                          `license_number` VARCHAR(225) NOT NULL,
                          `is_deleted` TINYINT NOT NULL DEFAULT 0,
                          PRIMARY KEY (`id`),
                          UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                          UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

INSERT INTO `drivers` (`id`, `name`, `license_number`) VALUES
						(1,'Ihor','643458589'),
						(2,'Oleg','852741963');

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `taxi_service`.`cars` (
									  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `manufacturer_id` BIGINT NOT NULL,
									  `model` VARCHAR(225) NOT NULL,
									  `is_deleted` TINYINT NOT NULL DEFAULT 0,
									  PRIMARY KEY (`id`),
		CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (manufacturer_id)
        REFERENCES `taxi_service`.`manufacturers` (`id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION);

INSERT INTO `taxi_service`.`cars` (`manufacturer_id`, `model`) VALUES 
										('3', 'A3'), 
										('3', 'Q7'),
										('4', 'X5'),
										('4', 'X3');


DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `taxi_service`.`cars_drivers` (
									  `car_id` BIGINT NOT NULL,
                                      `driver_id` BIGINT NOT NULL,
		CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (car_id) REFERENCES `taxi_service`.`cars` (`id`),
		CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (driver_id) REFERENCES `taxi_service`.`drivers` (`id`));

INSERT INTO `taxi_service`.`cars_drivers` (`car_id`, `driver_id`) VALUES  
											(1, 1);
        
-- ALTER TABLE `manufacturers` AUTO_INCREMENT = 1;
SELECT * FROM taxi_service.manufacturers;
SELECT * FROM taxi_service.drivers;
SELECT * FROM taxi_service.cars;
SELECT * FROM taxi_service.cars_drivers;
