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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        List<Driver> drivers = new ArrayList<>();
        Driver john = driverService.create(new Driver("John", "UA777"));
        drivers.add(john);
        Manufacturer zaz1102 = manufacturerService.create(new Manufacturer("ZAZ", "Ukraine"));
        Car tavria = carService.create(new Car("1102", zaz1102, drivers));
        System.out.println(carService.getAllByDriver(john.getId()));
        Driver bob = driverService.create(new Driver("Bob", "UA666"));
        drivers.remove(john);
        carService.delete(tavria.getId());
        drivers.add(bob);
        Manufacturer rover = manufacturerService.create(new Manufacturer("Rover", "UK"));
        Car rover200Si = carService.create(new Car("200Si", rover, drivers));
        System.out.println(carService.getAllByDriver(bob.getId()));
        rover200Si.setModel("X5");
        rover.setName("BMW");
        carService.addDriverToCar(john, rover200Si);
        carService.removeDriverFromCar(bob, rover200Si);
        System.out.println(carService.getAll());
    }

}
