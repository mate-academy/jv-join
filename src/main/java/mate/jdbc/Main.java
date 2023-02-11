package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer manufacturer = new Manufacturer("Mercedes", "Germany");
        manufacturerService.create(manufacturer);
        Car car = new Car("E200", manufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        carService.create(car);
        System.out.println(carService.get(car.getId()));
        Driver driver = new Driver("Andrew", "198723645");
        driverService.create(driver);
        System.out.println(driver.getId());
        carService.addDriverToCar(driver, car);
        System.out.println(carService.getAllByDriver(driver.getId()));
        carService.removeDriverFromCar(driver, car);
        carService.delete(car.getId());
        System.out.println(carService.getAll());
    }
}
