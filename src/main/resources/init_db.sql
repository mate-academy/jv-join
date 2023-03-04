DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) DEFAULT NULL,
  `country` VARCHAR(45) DEFAULT NULL,
  `is_deleted` TINYINT DEFAULT '0',
  PRIMARY KEY (`id`)
);

INSERT INTO manufacturers (`name`, `country`) VALUES ('Buick', 'United States');
INSERT INTO manufacturers (`name`, `country`) VALUES ('Chevrolet', 'United States');
INSERT INTO manufacturers (`name`, `country`, `is_deleted`) VALUES ('Jeep', 'United States', 1);
INSERT INTO manufacturers (`name`, `country`) VALUES ('Volvo Cars', 'Sweden');
INSERT INTO manufacturers (`name`, `country`) VALUES ('Kia', 'South Korea');
INSERT INTO manufacturers (`name`, `country`) VALUES ('Toyota', 'Japan');
INSERT INTO manufacturers (`name`, `country`) VALUES ('Fiat', 'Italy');

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) DEFAULT NULL,
  `licenseNumber` VARCHAR(20) NOT NULL,
  `is_deleted` TINYINT DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `licenseNumber_UNIQUE` (`licenseNumber`)
);

INSERT INTO drivers (`name`, `licenseNumber`) VALUES ('Esme Moyer', '7196-84-9223');
INSERT INTO drivers (`name`, `licenseNumber`) VALUES ('Rahul Fisher', 'RI115787Z');
INSERT INTO drivers (`name`, `licenseNumber`) VALUES ('Eddie Noble', '8199-16-5937');
INSERT INTO drivers (`name`, `licenseNumber`) VALUES ('Christine Gallegos', 'M7421392');
INSERT INTO drivers (`name`, `licenseNumber`) VALUES ('Brian Jarvis', 'V7186124');

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(255) NULL,
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  `manufacturer_id` BIGINT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `cars_manufacturers_fk`
  FOREIGN KEY (`manufacturer_id`)
  REFERENCES `taxi_service_db`.`manufacturers` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION 
);

INSERT INTO cars (`model`, `manufacturer_id`) VALUES ('Encore', 1);
INSERT INTO cars (`model`, `manufacturer_id`) VALUES ('Spark', 2);
INSERT INTO cars (`model`, `manufacturer_id`) VALUES ('S80', 4);
INSERT INTO cars (`model`, `manufacturer_id`) VALUES ('Corolla Altis', 6);

DROP TABLE IF EXISTS `cars_drivers`;
CREATE TABLE `cars_drivers` (
  `car_id` BIGINT NOT NULL,
  `driver_id` BIGINT NOT NULL,
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (id),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (id)
);

INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (1, 1);
INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (1, 4);
INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (2, 5);
INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (3, 2);
INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (3, 3);
