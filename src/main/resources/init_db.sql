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

DROP TABLE IF EXISTS `cars`;
CREATE TABLE `cars` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `model` varchar(20) NOT NULL,
                                    `manufacturer_id` bigint NOT NULL,
                                    `is_deleted` tinyint NOT NULL DEFAULT '0',
                                    PRIMARY KEY (`id`)
                                  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

ALTER TABLE `taxi`.`cars`
ADD INDEX `cars_manufacturers_id_idx` (`manufacturer_id` ASC) VISIBLE;
;
ALTER TABLE `taxi`.`cars`
ADD CONSTRAINT `cars_manufacturers_fk`
  FOREIGN KEY (`manufacturer_id`)
  REFERENCES `taxi`.`manufacturers` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

CREATE TABLE `cars_drivers` (
  `car_id` bigint NOT NULL,
  `driver_id` bigint NOT NULL,
  KEY `cars_drivers_cars_fk` (`car_id`),
  KEY `cars_drivers_drivers_fk` (`driver_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Ford', 'USA');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Maserati', 'Italy');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Mazda', 'Japan');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('McLaren', 'England');
INSERT INTO `taxi`.`manufacturers` (`name`, `country`) VALUES ('Mercedes-Benz', 'Jermany');

INSERT INTO `taxi`.`drivers` (`name`, `licence_number`) VALUES ('Dopinder', 'MH1420110062821');
INSERT INTO `taxi`.`drivers` (`name`, `licence_number`) VALUES ('Johny English', 'RT1939473219432');
INSERT INTO `taxi`.`drivers` (`name`, `licence_number`) VALUES ('Daniel', 'TY3829104372431');
INSERT INTO `taxi`.`drivers` (`name`, `licence_number`) VALUES ('Baby', 'OI9304127473244');
INSERT INTO `taxi`.`drivers` (`name`, `licence_number`) VALUES ('Dominic', 'DG9432103471241');

INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('Focus', '1');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('Capri', '1');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('Levante', '2');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('RX-8', '3');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('MX-5', '3');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('600LT Spider', '4');
INSERT INTO `taxi`.`cars` (`model`, `manufacturer_id`) VALUES ('Maybach', '5');

INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (1, 1);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (1, 3);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (2, 3);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (2, 2);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (3, 4);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (4, 4);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (4, 5);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (5, 2);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (5, 5);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (6, 5);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (7, 4);
INSERT INTO taxi.cars_drivers (`car_id`, `driver_id`) VALUES (7, 5);
