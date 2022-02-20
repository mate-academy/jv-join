CREATE DATABASE `library_db` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `cars` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `model` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `manufacturers_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cars_manufacturers_fk` (`manufacturers_id`),
  CONSTRAINT `cars_manufacturers_fk` FOREIGN KEY (`manufacturers_id`) REFERENCES `manufacturers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

CREATE TABLE `cars_drivers` (
  `car_id` bigint(20) DEFAULT NULL,
  `driver_id` bigint(20) DEFAULT NULL,
  KEY `cars_drivers_cars_fk` (`car_id`),
  KEY `cars_drivers_drivers_fk` (`driver_id`),
  CONSTRAINT `cars_drivers_cars_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`),
  CONSTRAINT `cars_drivers_drivers_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `drivers` (
  `name` varchar(211) DEFAULT NULL,
  `license_number` varchar(211) DEFAULT NULL,
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `is_deleted` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

CREATE TABLE `manufacturers` (
  `name` varchar(111) DEFAULT NULL,
  `country` varchar(111) DEFAULT NULL,
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

