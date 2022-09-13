package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer(null, "ferrai", "italy");
        manufacturerService.create(manufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver(null,"Oleg", "GH851678");
        driverService.create(driver);
        driver = new Driver(null,"Jhon", "FI841687");
        driverService.create(driver);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("GHL",
                manufacturerService.get(manufacturer.getId()),
                driverService.getAll());
        System.out.println(carService.create(car));
        System.out.println(carService.get(car.getId()));
        car.setModel("NWF");
        System.out.println(carService.update(car));
        System.out.println(carService.getAllByDriver(car.getId()));
        carService.removeDriverFromCar(driver, car);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.getAll());
    }
}
