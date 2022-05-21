INSERT INTO manufacturers (name, country)
VALUES ('BMW', 'Germany');
INSERT INTO manufacturers (name, country)
VALUES ('Mersedes', 'Germany');
INSERT INTO drivers (name, license_number)
VALUES ('Denys', 'EC-45678');
INSERT INTO drivers (name, license_number)
VALUES ('Makar', 'EC-44557');
INSERT INTO cars (model, manufacturer_id)
VALUES ('X5', 1);
INSERT INTO cars (model, manufacturer_id)
VALUES ('ml300', 1);
INSERT INTO cars_drivers (car_id, driver_id)
VALUES (1, 1);
INSERT INTO cars_drivers (car_id, driver_id)
VALUES (1, 2);
INSERT INTO cars_drivers (car_id, driver_id)
VALUES (2, 2);
INSERT INTO cars_drivers (car_id, driver_id)
VALUES (2, 1);