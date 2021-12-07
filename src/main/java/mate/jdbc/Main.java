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

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        Driver driverOne = driverService.create(new Driver("Bob","1234"));
        drivers.add(driverOne);
        Driver driverTwo = driverService.create(new Driver("Jack","09876"));
        drivers.add(driverTwo);
        Car car = new Car();
        car.setModel("testModel");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService
                .create(new Manufacturer("Peugeot", "France"));
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.getAll());

        carService.delete(car.getId());
        drivers.remove(1);
        Driver driverThree = driverService.create(new Driver("Alice","4567"));
        drivers.add(driverThree);
        car.setDrivers(drivers);
        car.setModel("new X1");
        car = carService.update(car);
        System.out.println(car);

        System.out.println("Cars by driver: ");
        List<Car> allByDriver = carService.getAllByDriver(driverTwo.getId());
        allByDriver.stream().forEach(System.out::println);
    }
}
