package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        // test your code here
        System.out.println("\n-----------------\nCheck creating new car");
        Manufacturer manufacturer = new Manufacturer("ZAZ", "Ukraine");
        manufacturerService.create(manufacturer);
        Car car = new Car("2023", manufacturer, new ArrayList<>());
        carService.create(car);
        carService.getAll().forEach(System.out::println);

        System.out.println("\n-----------------\nCheck getting by id");
        System.out.println(carService.get(car.getId()));

        System.out.println("\n-----------------\nCheck getting all");
        carService.getAll().forEach(System.out::println);

        System.out.println("\n-----------------\nCheck updating");
        car.setModel("2023 Default");
        carService.update(car);
        System.out.println(carService.get(car.getId()));

        System.out.println("\n-----------------\nCheck deleting by id");
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);

        carService.create(car);

        System.out.println("\n-----------------\nCheck adding driver to car");
        Driver driver = new Driver("Petro Poroshenko", "77777777777");
        driverService.create(driver);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println("\n-----------------\nCheck deleting driver from car");
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));
        carService.addDriverToCar(driver, car);

        System.out.println("\n-----------------\nCheck getting all cars by driver");
        System.out.println(carService.getAllByDriver(driver.getId()));
    }
}
