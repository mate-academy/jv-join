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
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(tesla);
        manufacturerService.create(audi);

        Driver petya = new Driver("Petya", "23425");
        Driver andryuha = new Driver("Andryuha", "2312455");
        Driver yulik = new Driver("Yulik", "2315524");
        Driver paul = new Driver("Paul", "321345");
        Driver alex = new Driver("Alex", "213556");
        Driver serhiy = new Driver("Serhiy", "869544");
        Driver michael = new Driver("Michael", "324566");
        driverService.create(petya);
        driverService.create(andryuha);
        driverService.create(yulik);
        driverService.create(paul);
        driverService.create(alex);
        driverService.create(serhiy);
        driverService.create(michael);

        List<Driver> q5Drivers = new ArrayList<>();
        q5Drivers.add(petya);
        q5Drivers.add(yulik);
        q5Drivers.add(paul);
        Car q5 = new Car("Q5", audi);
        q5.setDrivers(q5Drivers);
        List<Driver> modelXDrivers = new ArrayList<>();
        modelXDrivers.add(alex);
        modelXDrivers.add(serhiy);
        modelXDrivers.add(michael);
        Car modelX = new Car("Model X", tesla);
        modelX.setDrivers(modelXDrivers);
        carService.create(q5);
        carService.create(modelX);
        System.out.println(carService.get(q5.getId()));
        System.out.println(carService.get(modelX.getId()));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(petya.getId()).forEach(System.out::println);
        carService.getAllByDriver(michael.getId()).forEach(System.out::println);

        q5.setModel("Q_5");
        carService.update(q5);
        System.out.println(carService.get(q5.getId()));

        carService.removeDriverFromCar(petya, q5);
        carService.get(q5.getId()).getDrivers().forEach(System.out::println);

        carService.addDriverToCar(petya, modelX);
        carService.get(modelX.getId()).getDrivers().forEach(System.out::println);

        carService.delete(q5.getId());
        carService.getAll().forEach(System.out::println);
    }
}
