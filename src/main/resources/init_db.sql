CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

DROP TABLE IF EXISTS `manufacturers`;
CREATE TABLE `manufacturers` (
                                        `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                        `name` VARCHAR(225) NOT NULL,
                                        `country` VARCHAR(225) NOT NULL,
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                        PRIMARY KEY (`id`));

INSERT INTO `taxi_service`.`manufacturers` (`name`, `country`) VALUES ('Toyota', 'Japan'),
 ('Hyundai', 'South Korea'), ('Volkswagen Group', 'Germany'), ('General Motors', 'United States'),
 ('Ford', 'United States'), ('Nissan', 'Japan'), ('Fiat Chrysler Automobiles', 'Italy/United States'),
 ('Renault', 'France'), ('PSA Group', 'France'), ('Suzuki', 'Japan'), ('SAIC', 'China'),
 ('Daimler', 'Germany'), ('BMW', 'Germany'), ('Geely', 'China');

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE `drivers` (
                                  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(225) NOT NULL,
                                  `license_number` VARCHAR(225) NOT NULL,
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE INDEX `id_UNIQUE` (id ASC) VISIBLE,
                                  UNIQUE INDEX `license_number_UNIQUE` (`license_number` ASC) VISIBLE);

INSERT INTO `taxi_service`.`drivers` (`name`, `license_number`)
                            VALUES ('Bob', '55484823'), ('Alice', '6587426'),
                            ('Bogdan Chupika', '231324234');


CREATE TABLE cars (
			`id` BIGINT(11) NOT  NULL AUTO_INCREMENT PRIMARY KEY,
            `model` VARCHAR(255) NOT NULL,
            `manufacturer_id` BIGINT NOT NULL,
            `is_deleted` TINYINT NOT NULL DEFAULT 0,
CONSTRAINT `manufacturer_id_fk` FOREIGN KEY(`manufacturer_id`)
REFERENCES `manufacturers` (`id`) ON DELETE NO ACTION
);

CREATE TABLE cars_drivers (
			`car_id` BIGINT(11) NOT  NULL,
            `driver_id` BIGINT(11) NOT  NULL,
CONSTRAINT `car_id_fk` FOREIGN KEY(`car_id`)
REFERENCES `cars` (`id`) ON DELETE NO ACTION,
CONSTRAINT `driver_id_fk` FOREIGN KEY(`driver_id`)
REFERENCES `drivers` (`id`) ON DELETE NO ACTION
);



