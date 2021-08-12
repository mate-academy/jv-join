package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;

public class Main {
    public static void main(String[] args) {
        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        Manufacturer bmwManufactured = new Manufacturer();
        bmwManufactured.setCountry("Germany");
        bmwManufactured.setName("BMWManufactured");
        manufacturerDao.create(bmwManufactured);

        Car bmw = new Car();
        bmw.setModel("bmw");
        bmw.setManufacturer(bmwManufactured);

        DriverDao driverDao = new DriverDaoImpl();

        List<Driver> bmvDrivers = new ArrayList<>();
        bmvDrivers.add(driverDao.get(1L).get());
        bmvDrivers.add(driverDao.get(2L).get());
        bmvDrivers.add(driverDao.get(3L).get());
        bmw.setDrivers(bmvDrivers);

        CarDao newCar = new CarDaoImpl();
        newCar.create(bmw);
        System.out.println("bmw from DB before update:" + newCar.get(bmw.getId()));
        bmw.getDrivers().remove(1);
        newCar.update(bmw);
        System.out.println("bmw from DB after update:" + newCar.get(bmw.getId()));
    }
}
