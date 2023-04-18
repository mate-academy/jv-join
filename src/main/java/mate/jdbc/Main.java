package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.impl.CarDaoImpl;
import mate.jdbc.dao.impl.DriverDaoImpl;
import mate.jdbc.dao.impl.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        DriverDao driverDao = new DriverDaoImpl();

        Driver driverOne = new Driver();
        driverOne.setId(11L);
        driverOne.setLicenseNumber("334");
        driverOne.setName("One");

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = carService.get(9L);
        carService.removeDriverFromCar(driverOne, car);
        carService.getAll().forEach(System.out::println);
    }
}
