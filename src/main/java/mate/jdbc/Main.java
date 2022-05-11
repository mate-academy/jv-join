package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        Car car = new Car("Model Y", manufacturerService.get(13L), drivers);
        carService.create(car);
        System.out.println(carService.getAll());
        carService.addDriverToCar(driverService.get(1L), car);
        carService.get(car.getId());
        carService.removeDriverFromCar(driverService.get(1L), car);
        car.setModel("Model X");
        carService.update(car);
        carService.delete(car.getId());
    }
}
