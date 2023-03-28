-- Database: mate

-- DROP DATABASE IF EXISTS mate;

CREATE DATABASE mate
    WITH
    OWNER = mate
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Table: public.manufacturers

-- DROP TABLE IF EXISTS public.manufacturers;

CREATE TABLE public.manufacturers
(
    id bigserial NOT NULL UNIQUE,
    name character varying(255) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    is_deleted boolean NOT NULL DEFAULT 'false',
    CONSTRAINT manufacturers_pkey PRIMARY KEY (id)
)

-- Table: public.drivers

-- DROP TABLE IF EXISTS public.drivers;

CREATE TABLE public.drivers
(
    id bigserial NOT NULL UNIQUE,
    name character varying(255) COLLATE pg_catalog."default",
    license_number character varying(255) COLLATE pg_catalog."default",
    is_deleted boolean NOT NULL DEFAULT 'false',
    CONSTRAINT drivers_pkey PRIMARY KEY (id)
)

-- Table: public.cars

-- DROP TABLE IF EXISTS public.cars;

CREATE TABLE public.cars
(
    id bigserial NOT NULL UNIQUE,
    model character varying(255) COLLATE pg_catalog."default",
    manufacturer_id bigint NOT NULL,
    is_deleted boolean NOT NULL DEFAULT 'false',
    CONSTRAINT cars_pkey PRIMARY KEY (id),
    CONSTRAINT manufacturer_id_fk FOREIGN KEY (manufacturer_id)
        REFERENCES public.manufacturers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: public.cars_drivers

-- DROP TABLE IF EXISTS public.cars_drivers;

CREATE TABLE public.cars_drivers
(
    car_id bigint NOT NULL,
    driver_id bigint NOT NULL,
    UNIQUE (car_id, driver_id)
    CONSTRAINT car_id_fk FOREIGN KEY (car_id)
        REFERENCES public.cars (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT driver_id_fk FOREIGN KEY (driver_id)
        REFERENCES public.drivers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
