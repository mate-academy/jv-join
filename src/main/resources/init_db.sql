-- Database: postgres

-- DROP DATABASE IF EXISTS postgres;

CREATE DATABASE postgres
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE postgres
    IS 'default administrative connection database';

-- Table: public.manufacturers

-- DROP TABLE IF EXISTS public.manufacturers;

CREATE TABLE IF NOT EXISTS public.manufacturers
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    country character varying(255) COLLATE pg_catalog."default" NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT "manufacturers _pkey" PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.manufacturers
    OWNER to postgres;

-- Table: public.drivers

-- DROP TABLE IF EXISTS public.drivers;

CREATE TABLE IF NOT EXISTS public.drivers
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    license_number character varying(255) COLLATE pg_catalog."default" NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT drivers_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.drivers
    OWNER to postgres;

-- Table: public.cars

-- DROP TABLE IF EXISTS public.cars;

CREATE TABLE IF NOT EXISTS public.cars
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    manufacturer_id bigint NOT NULL,
    model character varying(255) COLLATE pg_catalog."default" NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT cars_pkey PRIMARY KEY (id),
    CONSTRAINT manufacturer_id FOREIGN KEY (manufacturer_id)
        REFERENCES public.manufacturers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cars
    OWNER to postgres;

-- Table: public.cars_drivers

-- DROP TABLE IF EXISTS public.cars_drivers;

CREATE TABLE IF NOT EXISTS public.cars_drivers
(
    driver_id bigint NOT NULL,
    car_id bigint NOT NULL,
    CONSTRAINT car_id FOREIGN KEY (car_id)
        REFERENCES public.cars (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT driver_id FOREIGN KEY (driver_id)
        REFERENCES public.drivers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.cars_drivers
    OWNER to postgres;