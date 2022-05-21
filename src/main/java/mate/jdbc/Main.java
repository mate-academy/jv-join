package mate.jdbc;

import java.io.IOException;
import mate.jdbc.lib.Injector;
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
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer manufacturer = new Manufacturer("BMW", "Germany");
        Driver driver = new Driver("Denys","EC-456789");

        driverService.create(driver);
        System.out.println("driverService.getAll() = " + driverService.getAll());

        manufacturerService.create(manufacturer);
        System.out.println("service.getAll() = " + manufacturerService.getAll());

        System.out.println("carService.getAll() = " + carService.getAll());
        System.out.println("carService.get(1) = " + carService.get(1L));
        System.out.println("carService.getAllByDriver(1L) = " + carService.getAllByDriver(1L));
    }
}
