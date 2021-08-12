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
        Manufacturer BmwManufactured = new Manufacturer();
        BmwManufactured.setCountry("Germany");
        BmwManufactured.setName("BMWManufactured");
        manufacturerDao.create(BmwManufactured);

        Car BMW = new Car();
        BMW.setModel("BMW");
        BMW.setManufacturer(BmwManufactured);
        CarDao newCar = new CarDaoImpl();

        DriverDao driverDao = new DriverDaoImpl();

//        Driver Anton = new Driver();
//        Anton.setName("Anton");
//        Anton.setLicenseNumber("1919");
//        driverDao.create(Anton);
//
//        Driver Misha = new Driver();
//        Misha.setName("Misha");
//        Misha.setLicenseNumber("2929");
//        driverDao.create(Misha);
//
//        Driver Max = new Driver();
//        Max.setName("Max");
//        Max.setLicenseNumber("3939");
//        driverDao.create(Max);

        List<Driver> BMWDrivers = new ArrayList<>();
        BMWDrivers.add(driverDao.get(1L).get());
        BMWDrivers.add(driverDao.get(2L).get());
        BMWDrivers.add(driverDao.get(3L).get());
        BMW.setDrivers(BMWDrivers);

        newCar.create(BMW);
        System.out.println("bmw from DB before update:" + newCar.get(BMW.getId()));
        BMW.getDrivers().remove(1);
        newCar.update(BMW);
        System.out.println("bmw from DB after update:" + newCar.get(BMW.getId()));
    }
}
