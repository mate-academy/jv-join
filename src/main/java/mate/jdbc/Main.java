package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Car car = new Car();
        car.setModel("Jeep");
        car.setManufacturer(manufacturerService.get(5L));
        car.setDrivers(List.of(new Driver[]{driverService.get(5L), driverService.get(4L)}));
        carService.create(car);
        car = carService.get(6L);
        car.setDrivers(List.of(new Driver[]{driverService.get(4L), driverService.get(3L)}));
        carService.update(car);
        carService.delete(3L);
        Driver driver;
        driver = driverService.get(2L);
        car = carService.get(1L);
        carService.getAllByDriver(3L);
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
        carService.getAll();
    }
}
