CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

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

CREATE TABLE CARS
(
    id               BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    model            VARCHAR(255)      NULL,
    is_deleted       TINYINT DEFAULT 0 NOT NULL,
    manufacturers_id BIGINT            NULL,
    CONSTRAINT cars_manufacturers_fk
        FOREIGN KEY (manufacturers_id) REFERENCES manufacturers (id)
);

CREATE TABLE cars_drivers
(
    cars_id    BIGINT NOT NULL,
    drivers_id BIGINT NOT NULL,
    CONSTRAINT cars_drivers_cars_fk
        FOREIGN KEY (cars_id) REFERENCES cars (id),
    CONSTRAINT cars_drivers_drivers_fk
        FOREIGN KEY (drivers_id) REFERENCES drivers (id)
);
