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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver bob = driverService.create(new Driver("Bob", "HLE789612"));
        Driver john = driverService.create(new Driver("John", "GBA926921"));
        Driver sam = driverService.create(new Driver("Sam", "LFA057349"));

        driverService.get(bob.getId());
        driverService.get(john.getId());
        driverService.get(sam.getId());

        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        drivers.add(john);
        drivers.add(sam);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer mazda = new Manufacturer("Mazda", "Japan");
        manufacturerService.create(mazda);
        mazda = manufacturerService.get(mazda.getId());

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car carOne = new Car("6", mazda, drivers);
        Car carTwo = new Car("CX-60", mazda, drivers);

        carService.create(carOne);
        carService.create(carTwo);

        System.out.println(carService.get(carOne.getId()));

        Car carUpdate = new Car(carOne.getId(), "CX-5", mazda, drivers);
        carService.update(carUpdate);

        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(bob, carService.get(carOne.getId()));
        System.out.println(carService.get(carOne.getId()));
        carService.removeDriverFromCar(bob, carService.get(carOne.getId()));
        System.out.println(carService.get(carOne.getId()));
        carService.getAllByDriver(john.getId());
    }
}
