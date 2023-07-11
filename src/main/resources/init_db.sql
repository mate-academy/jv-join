-- Tables creating:
-- sql7630939.manufacturers
CREATE TABLE sql7630939.manufacturers (
	id INT auto_increment NOT NULL,
	name varchar(250) NOT NULL,
	country varchar(250) NOT NULL,
	is_deleted BIT DEFAULT false NOT NULL,
	CONSTRAINT Manufacturers_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

-- sql7630939.drivers
CREATE TABLE sql7630939.drivers (
	id BIGINT auto_increment NOT NULL,
	name varchar(250) NOT NULL,
	license_number varchar(250) NOT NULL,
	is_deleted BIT DEFAULT false NOT NULL,
	CONSTRAINT Drivers_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

-- sql7630939.cars
CREATE TABLE sql7630939.cars (
	id BIGINT auto_increment NOT NULL,
	model varchar(250) NOT NULL,
	is_deleted BIT DEFAULT false NOT NULL,
	manufacturer_id int,
	CONSTRAINT Drivers_PK PRIMARY KEY (id),
	CONSTRAINT Cars_Manufactures_FK FOREIGN KEY (manufacturer_id)
    REFERENCES sql7630939.manufacturers(id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

-- sql7630939.cars_drivers
CREATE TABLE sql7630939.cars_drivers (
	car_id Bigint NOT NULL,
	driver_id Bigint NOT NULL,
	CONSTRAINT Cars_drivers_Cars_FK FOREIGN KEY (car_id)
    REFERENCES sql7630939.cars(id),
    CONSTRAINT Cars_drivers_Drivers_FK FOREIGN KEY (driver_id)
    REFERENCES sql7630939.drivers(id),
    UNIQUE KEY car_id_driver_id_Unique (car_id,driver_id),
    INDEX car_id_idx (car_id),
    INDEX driver_id_idx (driver_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;