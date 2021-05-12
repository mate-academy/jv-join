CREATE DATABASE `manufacturer_db` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
DROP TABLE IF EXISTS `drivers`;
DROP TABLE `manufacturers`;

CREATE TABLE IF NOT EXISTS `manufacturers` (
  `manufacturer_id` bigint NOT NULL AUTO_INCREMENT,
  `manufacturer_name` varchar(255) DEFAULT NULL,
  `manufacturer_country` varchar(255) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`manufacturer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

INSERT INTO manufacturers (manufacturer_name, manufacturer_country) VALUES ('BMW', 'Germany');
INSERT INTO manufacturers (manufacturer_name, manufacturer_country) VALUES ('Lexus', 'Italia');
INSERT INTO manufacturers (manufacturer_name, manufacturer_country) VALUES ('Hyndai', 'China');

CREATE TABLE `cars` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  `manufacturer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturer_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`manufacturer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

INSERT INTO `cars`(name, manufacturer_id) VALUES ('Hyndai Accent', 3);
INSERT INTO `cars`(name, manufacturer_id) VALUES ('BMW X5', 1);

CREATE TABLE `drivers` (
  `driver_id` bigint NOT NULL AUTO_INCREMENT,
  `driver_name` varchar(255) DEFAULT NULL,
  `driver_license_number` varchar(255) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`driver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb3;

INSERT INTO drivers (driver_name, driver_license_number) VALUES('Bob', '1234');
INSERT INTO drivers (driver_name, driver_license_number) VALUES('Alice', '5678');

CREATE TABLE `cars_drivers` (
  `car_id` bigint NOT NULL,
  `driver_id` bigint NOT NULL,
  KEY `cars_drivers_cars_fk` (`car_id`),
  KEY `cars_drivers_drivers_fk` (`driver_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`driver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO cars_drivers (car_id, driver_id) VALUES(1, 1);
INSERT INTO cars_drivers (car_id, driver_id) VALUES(1, 2);
