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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturer = new Manufacturer("CHEVROLET", "USA");
        manufacturerService.create(manufacturer);

        Driver driver = new Driver("Alice Tompson", "AD7865");
        driverService.create(driver);
        Driver driver1 = new Driver("Michael Lonson", "PO1289");
        driverService.create(driver1);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver1);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Camaro", manufacturer, drivers);
        carService.create(car);
        System.out.println(carService.getAll());
        System.out.println(System.lineSeparator());

        System.out.println(carService.get(car.getId()));
        System.out.println(System.lineSeparator());

        car.setModel("Aveo");
        System.out.println(carService.update(car));
        System.out.println(System.lineSeparator());

        carService.delete(car.getId());
        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(driver.getId()));
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
    }
}
