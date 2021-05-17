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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer tesla = new Manufacturer("Tesla", "United States");
        Manufacturer ford = new Manufacturer("Ford", "United States");
        Manufacturer deo = new Manufacturer("Deo", "Ukraine");
        System.out.println(manufacturerService.create(tesla));
        System.out.println(manufacturerService.create(ford));
        System.out.println(manufacturerService.create(deo));
        System.out.println(manufacturerService.get(tesla.getId()));
        System.out.println(manufacturerService.getAll());
        manufacturerService.delete(ford.getId());
        deo.setName("DeoLanos");
        System.out.println(manufacturerService.update(deo));
        System.out.println(manufacturerService.getAll());

        Driver bob = new Driver("Bob", "123");
        Driver willy = new Driver("Willy", "777");
        Driver vasil = new Driver("Vasil", "888");
        System.out.println(driverService.create(bob));
        System.out.println(driverService.create(willy));
        System.out.println(driverService.create(vasil));
        Car modelX = new Car("ModelX", tesla);
        modelX.setDriverList(new ArrayList<>(Arrays.asList(willy, vasil)));
        Car lanos = new Car("Lanos", deo);
        lanos.setDriverList(new ArrayList<>(Arrays.asList(vasil, willy)));
        System.out.println(carService.create(modelX));
        System.out.println(carService.create(lanos));

        System.out.println(driverService.get(bob.getId()));
        System.out.println(driverService.getAll());
        willy.setName("Willy Wonka!");
        driverService.update(willy);
        driverService.delete(bob.getId());
        System.out.println(driverService.getAll());

        carService.addDriverToCar(willy, lanos);
        carService.addDriverToCar(vasil, lanos);
        carService.addDriverToCar(vasil, modelX);
        carService.removeDriverFromCar(vasil, lanos);
        System.out.println(carService.getAllByDriver(willy.getId()));
        System.out.println(carService.get(modelX.getId()));
        System.out.println(carService.getAll());
        lanos.setModel("DeoLanos");
        System.out.println(carService.update(lanos));
        System.out.println(carService.delete(modelX.getId()));
        System.out.println(carService.getAll());

    }
}
