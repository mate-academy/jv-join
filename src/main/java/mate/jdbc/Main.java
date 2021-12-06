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
        Manufacturer lada = new Manufacturer("Lada", "Ukraine");
        Manufacturer deo = new Manufacturer("Deo", "Ukraine");
        manufacturerService.create(lada);
        manufacturerService.create(deo);
        Car car = new Car("VAZ-2107", lada);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driverList = new ArrayList<>();
        System.out.println(driverService.get(2L));
        driverList.add(driverService.get(2L));
        car.setDrivers(driverList);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.get(2L);
        carService.delete(1L);
        car.setModel("VAZ-2108");
        carService.update(car);
        carService.addDriverToCar(driverService.get(2L), car);
        carService.removeDriverFromCar(driverService.get(2L), car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
