package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static CarService carService = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer tesla = new Manufacturer("Tesla", "United States");
        Manufacturer mersedes = new Manufacturer("Mersedes", "Germany");
        System.out.println(manufacturerService.create(toyota));
        System.out.println(manufacturerService.create(tesla));
        System.out.println(manufacturerService.create(mersedes));
        System.out.println(manufacturerService.get(tesla.getId()));
        System.out.println(manufacturerService.getAll());
        System.out.println(manufacturerService.delete(mersedes.getId()));
        mersedes.setName("Mersedes-Benz");
        System.out.println(manufacturerService.update(mersedes));

        Driver driverBob = new Driver("Bob", "12345");
        Driver driverAlice = new Driver("Alice", "67890");
        Driver driverJohn = new Driver("John", "13579");
        System.out.println(driverService.create(driverBob));
        System.out.println(driverService.create(driverAlice));
        System.out.println(driverService.create(driverJohn));
        Car auris = new Car("Auris", toyota);
        auris.setDriverList(new ArrayList<>(Arrays.asList(driverBob, driverAlice)));
        Car modelX = new Car("Model X", tesla);
        modelX.setDriverList(new ArrayList<>(Arrays.asList(driverAlice, driverJohn)));
        System.out.println(carService.create(auris));
        System.out.println(carService.create(modelX));

        System.out.println(driverService.get(driverBob.getId()));
        System.out.println(driverService.getAll());
        driverAlice.setName("Alice Wonderland");
        driverService.update(driverAlice);
        driverService.delete(driverJohn.getId());
        System.out.println(driverService.getAll());

        carService.addDriverToCar(driverBob, modelX);
        carService.removeDriverFromCar(driverAlice, auris);
        System.out.println(carService.getAllByDriver(driverAlice.getId()));
        System.out.println(carService.get(modelX.getId()));
        System.out.println(carService.getAll());
        auris.setModel("AURIS");
        System.out.println(carService.update(auris));
        System.out.println(carService.delete(auris.getId()));
        System.out.println(carService.getAll());
    }
}
