package mate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ConnectionUtil;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        // test your code here
        clearAll();
        // Manufacturers
        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer teslaManufacturer = new Manufacturer("Tesla", "USA");
        manufacturerService.create(fordManufacturer);
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(teslaManufacturer);
        //manufacturerService.getAll().forEach(car -> manufacturerService.delete(car.getId()));

        // Drivers
        Driver carl = new Driver("Carl", "KGN62178MJI0");
        Driver tommy = new Driver("Tommy", "JKG64573LOP1");
        Driver john = new Driver("John", "RET43508MHI3");
        Driver mike = new Driver("Mike", "DFB63211MNI6");
        driverService.create(carl);
        driverService.create(tommy);
        driverService.create(john);
        driverService.create(mike);
        //driverService.getAll().forEach(car -> driverService.delete(car.getId()));

        // Cars --------
        Car ford = new Car("Fusion", fordManufacturer, new ArrayList<>());
        carService.create(ford);
        ford.getDrivers().add(carl);
        Car audi = new Car("A4", audiManufacturer, new ArrayList<>());
        carService.create(audi);
        audi.getDrivers().add(mike);
        audi.getDrivers().add(john);
        Car tesla = new Car("ModelY", teslaManufacturer, new ArrayList<>());
        carService.create(tesla);
        carService.getAll().forEach(System.out::println);
        carService.delete(ford.getId());
        System.out.println("Audi after delete");
        carService.getAll().forEach(System.out::println);
        audi.setModel("A8");
        carService.update(audi);
        System.out.println("Audi after update");
        System.out.println(carService.get(audi.getId()));
        carService.addDriverToCar(mike, tesla);
        System.out.println("Add driver to tesla");
        System.out.println(carService.get(tesla.getId()));
        carService.removeDriverFromCar(john, audi);
        System.out.println("Remove driver from audi");
        System.out.println(carService.get(audi.getId()));
    }

    private static void clearAll() {
        String[] truncateQueries = {
                "SET FOREIGN_KEY_CHECKS = 0;",
                "TRUNCATE cars_drivers;",
                "TRUNCATE cars;",
                "TRUNCATE drivers;",
                "TRUNCATE manufacturers;"
        };
        try (Connection connection = ConnectionUtil.getConnection()) {
            for (String query : truncateQueries) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't perform truncation", e);
        }
    }
}
