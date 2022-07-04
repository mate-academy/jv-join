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
    public static final long FIRST_MANUFACTURER = 1L;
    public static final long FIRST_DRIVER = 1L;
    public static final long SECOND_DRIVER = 2L;
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("All Cars");
        carService.getAll().forEach(System.out::println);

        Driver driver = driverService.get(FIRST_DRIVER);
        Manufacturer manufacturer = manufacturerService.get(FIRST_MANUFACTURER);
        Car car = new Car();
        car.setModel("Q7");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driver));
        car = carService.create(car);
        System.out.println(SEPARATOR + "New car was added to DB ");
        System.out.println(carService.get(car.getId()));

        car.setModel("Q8");
        car = carService.update(car);
        System.out.println(SEPARATOR + "New car was updated ");
        System.out.println(carService.get(car.getId()));

        Driver driver2 = driverService.get(SECOND_DRIVER);
        carService.addDriverToCar(driver2, car);
        System.out.println(SEPARATOR + "New driver was added to car ");
        System.out.println(carService.get(car.getId()));

        System.out.println(SEPARATOR + "All Cars of Driver " + driver2);
        carService.getAllByDriver(SECOND_DRIVER).forEach(System.out::println);

        carService.removeDriverFromCar(driver, car);
        System.out.println(SEPARATOR + "Driver was removed from car ");
        System.out.println(carService.get(car.getId()));

        System.out.println("All Cars");
        carService.getAll().forEach(System.out::println);

        carService.delete(car.getId());
        System.out.println(SEPARATOR + "Car was deleted from DB ");
    }
}
