package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final String SEPARATOR = System.lineSeparator();
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("All Cars");
        carService.getAll().forEach(System.out::println);

        Driver driver = new Driver();
        driver.setName("Ivan");
        driver.setLicenseNumber("12345");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driver = driverService.create(driver);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Ford");
        manufacturer.setCountry("USA");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturer = manufacturerService.create(manufacturer);
        Car car = new Car();
        car.setModel("Fiesta");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driver));
        car = carService.create(car);
        System.out.println(SEPARATOR + "New car was added to DB ");
        System.out.println(carService.get(car.getId()));

        car.setModel("Focus");
        car = carService.update(car);
        System.out.println(SEPARATOR + "New car was updated ");
        System.out.println(carService.get(car.getId()));

        Driver driver2 = new Driver();
        driver2.setName("Viktor");
        driver2.setLicenseNumber("45678");
        driver2 = driverService.create(driver2);
        carService.addDriverToCar(driver2, car);
        System.out.println(SEPARATOR + "New driver was added to car ");
        System.out.println(carService.get(car.getId()));

        System.out.println(SEPARATOR + "All Cars of Driver " + driver2);
        carService.getAllByDriver(driver2.getId()).forEach(System.out::println);

        carService.removeDriverFromCar(driver, car);
        System.out.println(SEPARATOR + "Driver was removed from car ");
        System.out.println(carService.get(car.getId()));

        System.out.println("All Cars");
        carService.getAll().forEach(System.out::println);

        carService.delete(car.getId());
        System.out.println(SEPARATOR + "Car was deleted from DB ");
    }
}
