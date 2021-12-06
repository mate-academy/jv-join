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

public class Application {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void run() {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("Audi", "German");
        manufacturer = manufacturerService.create(manufacturer);
        Driver black = new Driver("Black", "787898");
        black = driverService.create(black);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(black);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Q8", manufacturer, drivers);
        car = carService.create(car);
        Driver white = new Driver("White", "45658525");
        white = driverService.create(white);
        carService.addDriverToCar(white, car);
        carService.removeDriverFromCar(black, car);
        List<Car> cars = carService.getAll();
        cars = carService.getAll();
        carService.delete(car.getId());
        cars = carService.getAll();
        List<Car> carsByDriver = carService.getAllByDriver(white.getId());
    }
}
