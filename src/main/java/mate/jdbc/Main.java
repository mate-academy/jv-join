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

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        Car car = new Car();
        car.setManufacturer(manufacturerService.get(11L));
        car.setModel("Golf");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(5L));
        drivers.add(driverService.get(6L));
        car.setDrivers(drivers);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        System.out.println(carService.get(1L));

        System.out.println(carService.delete(4L));

        Car car2 = new Car();
        car2.setId(5L);
        car2.setManufacturer(manufacturerService.get(11L));
        car2.setModel("Passat");
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driverService.get(1L));
        drivers2.add(driverService.get(2L));
        car2.setDrivers(drivers2);
        System.out.println(carService.update(car2));

        carService.getAllByDriver(2L).forEach(System.out::println);

        Driver driver5 = new Driver();
        driver5.setId(5L);
        Car car4 = new Car();
        car4.setId(4L);
        carService.addDriverToCar(driver5, car4);
        carService.removeDriverFromCar(driver5, car4);

        carService.getAll().forEach(System.out::println);
    }
}
