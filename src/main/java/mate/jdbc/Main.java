package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver jon = new Driver("Jon", "0122567");
        Driver bob = new Driver("Bob", "5557775");
        System.out.println(driverService.create(jon));
        System.out.println(driverService.create(bob));

        Manufacturer manufacturerBmw = new Manufacturer("BMW", "Germany");
        Manufacturer manufacturer = manufacturerService.create(manufacturerBmw);

        Car sedan = new Car("Sedan", manufacturer.getId());
        Car pickup = new Car("Pickup", manufacturer.getId());
        carService.create(sedan);
        carService.create(pickup);

        System.out.println(carService.get(sedan.getId()));

        System.out.println(carService.update(new Car(sedan.getId(), "sportCar",
                manufacturer.getId())));

        List<Car> all = carService.getAll();
        all.forEach(System.out::println);

        carService.addDriverToCar(jon, sedan);
        carService.addDriverToCar(bob, sedan);

        List<Car> allByDriver = carService.getAllByDriver(bob.getId());
        allByDriver.forEach(System.out::println);

        carService.removeDriverFromCar(jon, sedan);

        System.out.println(carService.delete(sedan.getId()));
    }
}
