package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Audi", "Germany");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver("Bob", "11111");
        driver = driverService.create(driver);
        drivers.add(driver);
        Driver driver1 = new Driver("Jonny", "22222");
        driver1 = driverService.create(driver1);
        drivers.add(driver1);

        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("80");
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        System.out.println(carService.get(car.getId()));

        carService.getAll().forEach(System.out::println);

        car.setModel("100");
        System.out.println(carService.update(car));

        carService.removeDriverFromCar(driver, car);
        carService.addDriverToCar(driver1, car);

        System.out.println(carService.getAllByDriver(driver1.getId()));

        System.out.println(carService.delete(car.getId()));
    }
}
