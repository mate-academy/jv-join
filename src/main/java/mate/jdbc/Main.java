package mate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long THIRD_ID = 3L;
    private static final int FOURTH_DRIVER = 3;
    private static final int FIRST_DRIVER = 1;
    private static final int FIRST_MANUFACTURER = 0;
    private static final int SECOND_MANUFACTURER = 1;
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        List<Manufacturer> manufacturers = testManufacturerService();
        List<Driver> drivers = testDriverService();
        testCarService(manufacturers, drivers);
        // UNCOMMENT METHOD BELOW WITH CAUTION
        //clearAll();
    }

    private static void testCarService(List<Manufacturer> manufacturers, List<Driver> drivers) {
        System.out.println("\nCAR TEST SERVICE \n");
        Car amarok = carService.create(
                new Car("Amarok",
                        manufacturers.get(FIRST_MANUFACTURER),
                        drivers));
        Car santaFe = carService.create(
                new Car("Santa Fe",
                        manufacturers.get(SECOND_MANUFACTURER),
                        drivers.stream().limit(FIRST_DRIVER).collect(Collectors.toList())));
        System.out.println(carService.get(SECOND_ID));
        carService.addDriverToCar(drivers.get(FOURTH_DRIVER), santaFe);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(FIRST_ID).forEach(System.out::println);
        System.out.println(carService.get(SECOND_ID));
        carService.removeDriverFromCar(drivers.get(FOURTH_DRIVER), santaFe);
        System.out.println(carService.get(SECOND_ID));
        carService.delete(SECOND_ID);
        carService.getAll().forEach(System.out::println);
    }

    private static List<Driver> testDriverService() {
        System.out.println("\nDRIVER TEST SERVICE \n");
        List<Driver> drivers = new ArrayList<>();
        Driver bob = driverService.create(
                new Driver("Bob", "NY12345678"));
        drivers.add(bob);
        Driver john = driverService.create(
                new Driver("John", "FL12345678"));
        drivers.add(john);
        Driver alice = driverService.create(
                new Driver("Alice", "NE12345678"));
        System.out.println(driverService.get(SECOND_ID));
        System.out.println(driverService.get(THIRD_ID));
        Driver aliceToEmma = driverService.update(
                new Driver(THIRD_ID, "Emma", "TE12345678"));
        drivers.add(aliceToEmma);
        driverService.delete(SECOND_ID);
        Driver boris = driverService.create(
                new Driver("Boris", "JOHNSONUK"));
        drivers.add(boris);
        driverService.getAll().forEach(System.out::println);
        return drivers;
    }

    private static List<Manufacturer> testManufacturerService() {
        System.out.println("\nMANUFACTURER TEST SERVICE \n");
        List<Manufacturer> manufacturers = new ArrayList<>();
        Manufacturer volkswagen = manufacturerService.create(
                new Manufacturer("Volkswagen", "Germany"));
        manufacturers.add(volkswagen);
        Manufacturer hyundai = manufacturerService.create(
                new Manufacturer("Hyundai", "South Korea"));
        manufacturers.add(hyundai);
        Manufacturer cherry = manufacturerService.create(
                new Manufacturer("Cherry", "China"));
        System.out.println(manufacturerService.get(SECOND_ID));
        System.out.println(manufacturerService.get(THIRD_ID));
        Manufacturer cherryToHaval = manufacturerService.update(
                new Manufacturer(THIRD_ID, "Haval", "China"));
        manufacturers.add(cherryToHaval);
        manufacturerService.delete(SECOND_ID);
        manufacturerService.getAll().forEach(System.out::println);
        return manufacturers;
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
