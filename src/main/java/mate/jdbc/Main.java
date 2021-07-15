package mate.jdbc;

import mate.jdbc.dao.*;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("Tesla");
        manufacturer.setCountry("USA");
        manufacturerDao.create(manufacturer);
        CarDao carDao = new CarDaoImpl();
        Car car = new Car();
        car.setModel("Model S");
        DriverDao driverDao = new DriverDaoImpl();
        Driver arsen = new Driver("Arsen", "2281337");
        Driver igor = new Driver("Igor", "1112222");
        driverDao.create(arsen);
        driverDao.create(igor);
        car.setDrivers(List.of(arsen, igor));
        car.setManufacturer(manufacturer);
        carDao.create(car);
        System.out.println(carDao.get(car.getId()));
    }
}
