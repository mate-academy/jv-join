package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

//        Car car = new Car();
//        car.setModel("VW");
//
//        Manufacturer manufacturer = new Manufacturer();
//        manufacturer.setName("VW Auto");
//        manufacturer.setCountry("Germany");
//        manufacturer = manufacturerService.create(manufacturer);
//        car.setManufacturer(manufacturer);
//
//        List<Driver> drivers = new ArrayList<>();
//        Driver driverAlice = new Driver();
//        driverAlice.setName("Alice");
//        driverAlice.setLicenseNumber("22222");
//        driverAlice = driverService.create(driverAlice);
//        drivers.add(driverAlice);
//
//        Driver driverOleh = new Driver();
//        driverOleh.setName("Oleh");
//        driverOleh.setLicenseNumber("44444");
//        driverOleh = driverService.create(driverOleh);
//        drivers.add(driverOleh);
//        car.setDrivers(drivers);
//
//        carDao.create(car);

        List<Car> cars = carDao.getAll();
        cars.forEach(System.out::println);
    }
}
