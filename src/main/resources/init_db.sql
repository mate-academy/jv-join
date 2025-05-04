CREATE DATABASE taxi_service_db
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'Ukrainian_Ukraine.1251'
    LC_CTYPE = 'Ukrainian_Ukraine.1251'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE TABLE IF NOT EXISTS public.manufacturers
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(45) COLLATE pg_catalog."default" NOT NULL,
    country character varying(45) COLLATE pg_catalog."default" NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT manufacturers_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.drivers
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(45) COLLATE pg_catalog."default" NOT NULL,
    license_number character varying(15) COLLATE pg_catalog."default" NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT drivers_pkey PRIMARY KEY (id)
    );
CREATE TABLE IF NOT EXISTS public.cars
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    model character varying(45) COLLATE pg_catalog."default" NOT NULL,
    manufacturer_id bigint NOT NULL,
    is_deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT cars_pkey PRIMARY KEY (id),
    CONSTRAINT cars_manufacturer_id_fk FOREIGN KEY (manufacturer_id)
        REFERENCES public.manufacturers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.cars_drivers
(
    driver_id bigint NOT NULL,
    car_id bigint NOT NULL,
    CONSTRAINT car_drivers_car_id_fk FOREIGN KEY (car_id)
        REFERENCES public.cars (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT car_drivers_driver_id_fk FOREIGN KEY (driver_id)
        REFERENCES public.drivers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
