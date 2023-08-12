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
import mate.jdbc.service.*;

import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        CarDao carDao = new CarDaoImpl();
        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        DriverDao driverDao = new DriverDaoImpl();

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        /*Manufacturer manufacturerTesla = new Manufacturer(null, "Tesla", "USA");
        Driver driverArtem = new Driver(null, "Artem", "12345");
        Car carTesla = new Car(null, "Tesla", manufacturerTesla, List.of(driverArtem));
        carService.create(new Car(null, "Tesla", manufacturerTesla, List.of(driverArtem)));*/
    }
}
