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
    private static final Long FIRST_DRIVER_ID = 1L;
    private static final int THIRD_DRIVER = 2;
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
        clearAll();
    }

    private static void testCarService(List<Manufacturer> manufacturers, List<Driver> drivers) {
        Car amarok = carService.create(
                new Car("Amarok",
                        manufacturers.get(FIRST_MANUFACTURER),
                        drivers));
        Car santaFe = carService.create(
                new Car("Santa Fe",
                        manufacturers.get(SECOND_MANUFACTURER),
                        drivers.stream().limit(FIRST_DRIVER).collect(Collectors.toList())));
        System.out.println(carService.get(santaFe.getId()));
        carService.addDriverToCar(drivers.get(THIRD_DRIVER), santaFe);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(FIRST_DRIVER_ID).forEach(System.out::println);
        System.out.println(carService.get(santaFe.getId()));
        carService.removeDriverFromCar(drivers.get(THIRD_DRIVER), santaFe);
        System.out.println(carService.get(santaFe.getId()));
        carService.delete(santaFe.getId());
        carService.getAll().forEach(System.out::println);
    }

    private static List<Driver> testDriverService() {
        List<Driver> drivers = new ArrayList<>();
        Driver bob = driverService.create(
                new Driver("Bob", "NY12345678"));
        drivers.add(bob);
        Driver john = driverService.create(
                new Driver("John", "FL12345678"));
        drivers.add(john);
        Driver aliceAkaEmma = driverService.create(
                new Driver("Alice", "NE12345678"));
        System.out.println(driverService.get(john.getId()));
        System.out.println(driverService.get(aliceAkaEmma.getId()));
        aliceAkaEmma.setName("Emma");
        driverService.update(aliceAkaEmma);
        driverService.delete(john.getId());
        Driver boris = driverService.create(
                new Driver("Boris", "JOHNSONUK"));
        drivers.add(boris);
        driverService.getAll().forEach(System.out::println);
        return drivers;
    }

    private static List<Manufacturer> testManufacturerService() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        Manufacturer volkswagen = manufacturerService.create(
                new Manufacturer("Volkswagen", "Germany"));
        manufacturers.add(volkswagen);
        Manufacturer hyundai = manufacturerService.create(
                new Manufacturer("Hyundai", "South Korea"));
        manufacturers.add(hyundai);
        Manufacturer cherryAkaHaval = manufacturerService.create(
                new Manufacturer("Cherry", "China"));
        System.out.println(manufacturerService.get(hyundai.getId()));
        System.out.println(manufacturerService.get(cherryAkaHaval.getId()));
        cherryAkaHaval.setName("Haval");
        manufacturerService.update(cherryAkaHaval);
        manufacturerService.delete(hyundai.getId());
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
