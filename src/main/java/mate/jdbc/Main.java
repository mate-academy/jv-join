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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Car car = new Car();
        car.setModel("Logan");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        car.setManufacturer(manufacturerService.get(1L));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(1L));
        drivers.add(driverService.get(2L));
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(car.getId()));
        Car car2 = new Car();
        car2.setModel("Polo");
        car2.setId(1L);
        car2.setManufacturer(manufacturerService.get(2L));
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driverService.get(3L));
        drivers2.add(driverService.get(4L));
        car2.setDrivers(drivers2);
        carService.update(car2);
        System.out.println(carService.get(car.getId()));
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.addDriverToCar(driverService.get(1L), car);
        carService.removeDriverFromCar(driverService.get(1L), car);
        System.out.println(carService.get(car.getId()));
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
