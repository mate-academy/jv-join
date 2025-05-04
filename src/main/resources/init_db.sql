DROP TABLE IF EXISTS `cars_drivers`;
DROP TABLE IF EXISTS `cars`;
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
CREATE TABLE `cars` (
                                  `id` bigint(11) NOT NULL AUTO_INCREMENT,
                                  `model` varchar(255) NOT NULL,
                                  `manufacturer_id` bigint(11) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `id_UNIQUE` (`id`),
                                  KEY `manufacturer_id_fk_idx` (`manufacturer_id`),
                                  CONSTRAINT `manufacturer_id_fk` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturers` (`id`)
                                  ON DELETE NO ACTION
                                  ON UPDATE NO ACTION);

CREATE TABLE `cars_drivers` (
								  `car_id` bigint(11) NOT NULL,
								  `driver_id` bigint(11) NOT NULL,
								  KEY `car_id_fk_idx` (`car_id`),
								  KEY `driver_id_fk_idx` (`driver_id`),
								  CONSTRAINT `car_id_fk` FOREIGN KEY (`car_id`) REFERENCES `cars` (`id`)
								  ON DELETE NO ACTION
								  ON UPDATE NO ACTION,
								  CONSTRAINT `driver_id_fk` FOREIGN KEY (`driver_id`) REFERENCES `drivers` (`id`)
								  ON DELETE NO ACTION
								  ON UPDATE NO ACTION);                                  