package mate.jdbc;

import java.io.IOException;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CreateDataBase;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CreateDataBaseImpl;
import mate.jdbc.util.FileUtils;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final String INIT_DB = "/init_db.sql";

    public static void main(String[] args) {
        try {
            CreateDataBase createDB = new CreateDataBaseImpl();
            createDB.createdTable(FileUtils.readFile(INIT_DB));
        } catch (IOException e) {
            throw new RuntimeException("Can`t create table for database");
        }

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturerFirst = new Manufacturer("BMWWW", "GAqwqweGermany");
        manufacturerService.create(manufacturerFirst);
        Manufacturer manufacturerSecond = new Manufacturer("Mersedes", "Germany");
        manufacturerService.create(manufacturerSecond);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver driverFirst = new Driver(1L,"Denys", "EC-456789");
        driverService.create(driverFirst);
        Driver driverSecond = new Driver("Makar", "EC-445577");
        driverService.create(driverSecond);
        List<Driver> drivers = List.of(driverFirst, driverSecond);

        Car carFirst = new Car("X5", manufacturerFirst, drivers);
        carService.create(carFirst);
        Car carSecond = new Car("ml350", manufacturerFirst, drivers);
        carService.create(carSecond);

        Car carThird = new Car("999999", manufacturerFirst, drivers);
        carService.create(carThird);

        Manufacturer manufacturerUpdate = new Manufacturer(1L,"BMW", "Germany");
        List<Driver> driversUpdate = List.of(driverFirst);
        Car carUpdate = new Car(3L,"320d", manufacturerUpdate, driversUpdate);
        carService.update(carUpdate);

        System.out.println("manufacturerService.getAll() = " + manufacturerService.getAll());
        System.out.println("driverService.getAll() = " + driverService.getAll());
        System.out.println("carService.getAll() = " + carService.getAll());
        System.out.println("carService.get(1) = " + carService.get(1L));
        System.out.println("carService.getAllByDriver(2L) = " + carService.getAllByDriver(2L));
    }
}
