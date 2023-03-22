CREATE SCHEMA IF NOT EXISTS `taxi_service` DEFAULT CHARACTER SET utf8;
USE `taxi_service`;

CREATE TABLE `manufacturers` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) NOT NULL,
                                 `country` varchar(255) NOT NULL,
                                 `is_deleted` tinyint NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE `drivers` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) NOT NULL,
                          `license_number` varchar(255) NOT NULL,
                          `is_deleted` tinyint NOT NULL DEFAULT '0',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `model`            VARCHAR(255) NOT NULL,
    `is_deleted`       TINYINT      NOT NULL DEFAULT '0',
    `manufacturers_id` BIGINT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `cars_manufacturers_fk`
        FOREIGN KEY (`manufacturers_id`)
            REFERENCES `taxi_service`.`manufacturers` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `cars_drivers` (
    `drivers_id` BIGINT NOT NULL,
    `cars_id` BIGINT NOT NULL,
    CONSTRAINT `cars_drivers_drivers_fk`
        FOREIGN KEY (`drivers_id`)
            REFERENCES `taxi_service`.`drivers` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT `cars_drivers_cars_fk`
        FOREIGN KEY (`cars_id`)
            REFERENCES `taxi_service`.`cars` (`id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
