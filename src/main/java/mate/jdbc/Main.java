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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = driverService.create(new Driver("Bob", "1234"));
        Driver driverAlice = driverService.create(new Driver("Alice", "4321"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerAudi = manufacturerService.create(
                new Manufacturer("Audi", "Germany"));

        Car audi = new Car();
        audi.setDrivers(List.of(driverAlice, driverBob));
        audi.setModel("A6");
        audi.setManufacturer(manufacturerAudi);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        audi = carService.create(audi);

        System.out.println("GetAll:");
        System.out.println(carService.getAll());
        System.out.println(System.lineSeparator());

        audi.setModel("Q5");
        System.out.println("Update:");
        System.out.println(carService.update(audi));
        System.out.println(System.lineSeparator());

        Driver driverJim = driverService.create(new Driver("Jim", "5678"));
        Driver driverMack = driverService.create(new Driver("Mack", "8765"));
        Manufacturer manufacturerMazda = manufacturerService.create(
                new Manufacturer("Mazda", "Japan"));
        Car mazda = new Car();
        mazda.setDrivers(List.of(driverJim, driverMack));
        mazda.setModel("CX5");
        mazda.setManufacturer(manufacturerMazda);
        mazda = carService.create(mazda);

        System.out.println("GetByCarId:");
        System.out.println(carService.get(mazda.getId()));
        System.out.println(System.lineSeparator());

        carService.addDriverToCar(driverAlice, mazda);

        System.out.println("GetByDriverId:");
        System.out.println(carService.getAllByDriver(driverAlice.getId()));
        System.out.println(System.lineSeparator());

        carService.removeDriverFromCar(driverAlice, audi);
        System.out.println("After driver delete:");
        System.out.println(carService.get(audi.getId()));
        System.out.println(System.lineSeparator());

        System.out.println(carService.delete(audi.getId()));
        System.out.println("After audi delete:");
        System.out.println(carService.getAll());
    }
}
