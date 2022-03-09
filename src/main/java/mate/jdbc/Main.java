package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car = new Car();
        car.setId(1L);
        car.setModel("A8");
        car.setManufacturer(manufacturerService.get(2L));
        car.setDrivers(List.of(driverService.get(1L), driverService.get(2L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(1L));
        System.out.println(carService.getAll());
        System.out.println(carService.update(car));
        carService.delete(1L);
        System.out.println(carService.getAllByDriver(1L));
    }
}
