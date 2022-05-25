-- CREATE DATABASE taxi
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     CONNECTION LIMIT = -1;

-- DROP TABLE IF EXISTS cars_drivers;
-- DROP TABLE IF EXISTS cars;
-- DROP TABLE IF EXISTS drivers;
-- DROP TABLE IF EXISTS manufacturers;

CREATE TABLE IF NOT EXISTS manufacturers
(
    id         BIGSERIAL    NOT NULL,
    name       VARCHAR(100) NOT NULL,
    country    VARCHAR(100) NOT NULL,
    is_deleted boolean DEFAULT false,
    PRIMARY KEY (id)
);

ALTER TABLE manufacturers
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS drivers
(
    id             BIGSERIAL    NOT NULL,
    name           VARCHAR(75)  NOT NULL,
    license_number VARCHAR(155) NOT NULL,
    is_deleted     boolean DEFAULT false,
    PRIMARY KEY (id)
);

ALTER TABLE drivers
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS cars
(
    id              BIGSERIAL   NOT NULL,
    model           VARCHAR(35) NOT NULL,
    manufacturer_id BIGINT      NOT NULL,
    is_deleted      boolean DEFAULT false,
    PRIMARY KEY (id),
    CONSTRAINT cars_manufacturer_fk
        FOREIGN KEY (manufacturer_id) REFERENCES manufacturers (id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);

ALTER TABLE cars
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS cars_drivers
(
    car_id    BIGSERIAL NOT NULL,
    driver_id BIGSERIAL NOT NULL,
    CONSTRAINT cars_drivers_cars_fk FOREIGN KEY (car_id) REFERENCES cars (id),
    CONSTRAINT cars_drivers_driver_fk FOREIGN KEY (driver_id) REFERENCES drivers (id)
);

ALTER TABLE cars_drivers
    OWNER TO postgres;
