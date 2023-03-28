package mate.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        // test your code here
        clearTable();
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer kia = new Manufacturer("KIA", "South Korea");
        Manufacturer bogdan = new Manufacturer("Bogdan", "Ukraine");

        Driver bob = new Driver("bob", "1111");
        Driver sem = new Driver("sem", "2222");
        Driver alice = new Driver("alice", "3333");

        Car kiaCeed = new Car("Ceed",kia, new ArrayList<>());
        kiaCeed.getDrivers().add(bob);
        kiaCeed.getDrivers().add(sem);
        Car toyotaPrius = new Car("Prius",toyota, new ArrayList<>());
        toyotaPrius.getDrivers().add(bob);
        toyotaPrius.getDrivers().add(sem);
        toyotaPrius.getDrivers().add(alice);
        Car bogdanA1204 = new Car("A1204", bogdan, new ArrayList<>());
        bogdanA1204.getDrivers().add(sem);
        bogdanA1204.getDrivers().add(alice);

        manufacturerService.create(toyota);
        manufacturerService.create(kia);
        manufacturerService.create(bogdan);

        driverService.create(bob);
        driverService.create(sem);
        driverService.create(alice);

        carService.create(toyotaPrius);
        carService.create(kiaCeed);
        carService.create(bogdanA1204);

        System.out.println("Test get car by id:");
        System.out.println(carService.get(toyotaPrius.getId()));
        System.out.println(carService.get(kiaCeed.getId()));
        System.out.println(carService.get(bogdanA1204.getId()));

        System.out.println(System.lineSeparator() + "Test get all car:");
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Update drivers");
        toyotaPrius.setDrivers(List.of(bob));
        carService.update(toyotaPrius);
        System.out.println(carService.get(toyotaPrius.getId()));

        System.out.println(System.lineSeparator() + "Update model and manufacturer");
        toyotaPrius.setManufacturer(bogdan);
        toyotaPrius.setModel("priusBogdan");
        carService.update(toyotaPrius);
        System.out.println(carService.get(toyotaPrius.getId()));

        System.out.println(System.lineSeparator() + "Delete car with id " + toyotaPrius.getId()
                + "  " + carService.delete(toyotaPrius.getId()));
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Try Update model and manufacturer after del");
        toyotaPrius.setDrivers(List.of(bob,sem,alice));
        toyotaPrius.setManufacturer(toyota);
        toyotaPrius.setModel("prius");
        carService.update(toyotaPrius);
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Get all cars by driver " + sem);
        carService.getAllByDriver(sem.getId()).forEach(System.out::println);
        System.out.println(System.lineSeparator() + "Get all by driver, after delete driver "
                + sem);
        driverService.delete(sem.getId());
        carService.getAllByDriver(sem.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Get all cars by driver " + bob);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Add driver : " + bob
                + " to car: " + bogdanA1204);
        carService.addDriverToCar(bob, bogdanA1204);
        System.out.println("Get all cars by driver " + bob);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Remote driver : " + bob
                + " from car: " + kiaCeed);
        carService.removeDriverFromCar(bob, kiaCeed);
        System.out.println("Get all cars by driver " + bob);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Get kiaCeed after remote driver: bob"
                + System.lineSeparator()
                + carService.get(kiaCeed.getId()));
    }

    private static void clearTable() {
        String[] queries = {
                "DELETE FROM cars_drivers;",
                "DELETE FROM cars;",
                "ALTER TABLE cars AUTO_INCREMENT = 1;",
                "DELETE FROM drivers;",
                "ALTER TABLE drivers AUTO_INCREMENT = 1;",
                "DELETE FROM manufacturers;",
                "ALTER TABLE manufacturers AUTO_INCREMENT = 1;"
        };
        try (Connection connection = ConnectionUtil.getConnection()) {
            for (String query : queries) {
                try (PreparedStatement deleteStatement =
                             connection.prepareStatement(query)) {
                    deleteStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't perform truncation", e);
        }
    }

}
