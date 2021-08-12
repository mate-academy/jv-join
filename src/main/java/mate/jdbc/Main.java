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
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setCountry("Germany");
        bmwManufacturer.setName("BMWManufacturer");
        manufacturerDao.create(bmwManufacturer);

        Car bmw = new Car();
        bmw.setModel("bmw");
        bmw.setManufacturer(bmwManufacturer);

        DriverDao driverDao = new DriverDaoImpl();

        Driver Anton = new Driver();
        Anton.setName("Anton");
        Anton.setLicenseNumber("1919");
        driverDao.create(Anton);

        Driver Misha = new Driver();
        Misha.setName("Misha");
        Misha.setLicenseNumber("2929");
        driverDao.create(Misha);

        Driver Max = new Driver();
        Max.setName("Max");
        Max.setLicenseNumber("3939");
        driverDao.create(Max);

        List<Driver> bmvDrivers = new ArrayList<>();
        bmvDrivers.add(driverDao.get(1L).get());
        bmvDrivers.add(driverDao.get(2L).get());
        bmvDrivers.add(driverDao.get(3L).get());
        bmw.setDrivers(bmvDrivers);

        CarDao carDao = new CarDaoImpl();
        carDao.create(bmw);
        System.out.println("bmw from DB before update:" + carDao.get(bmw.getId()));
        bmw.getDrivers().remove(1);
        carDao.update(bmw);
        System.out.println("bmw from DB after update:" + carDao.get(bmw.getId()));
    }
}
