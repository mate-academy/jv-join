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
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer(null, "Ford", "USA");
        manufacturerService.create(manufacturer);
        Driver driver = new Driver(null, "Ivan", "12365");
        driverService.create(driver);
        Car car = new Car(null,"Mustang", manufacturerService
                .get(manufacturer.getId()), driverService.getAll());
        carService.create(car);
        carService.get(car.getId());
        car.setModel("Mondeo");
        carService.update(car);
        carService.getAllByDriver(car.getId());
        carService.removeDriverFromCar(driver, car);
        carService.addDriverToCar(driver, car);
        carService.getAll().forEach(System.out::println);
    }
}
