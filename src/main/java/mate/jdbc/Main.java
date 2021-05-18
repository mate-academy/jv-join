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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService MANUFACTURER_SERVICE = (ManufacturerService)
            INJECTOR.getInstance(ManufacturerService.class);
    private static final DriverService DRIVER_SERVICE = (DriverService)
            INJECTOR.getInstance(DriverService.class);
    private static final CarService CAR_SERVICE = (CarService)
            INJECTOR.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        MANUFACTURER_SERVICE.create(tesla);
        MANUFACTURER_SERVICE.create(audi);

        Driver petya = new Driver("Petya", "23425");
        Driver andryuha = new Driver("Andryuha", "2312455");
        Driver yulik = new Driver("Yulik", "2315524");
        Driver paul = new Driver("Paul", "321345");
        Driver alex = new Driver("Alex", "213556");
        Driver serhiy = new Driver("Serhiy", "869544");
        Driver michael = new Driver("Michael", "324566");
        DRIVER_SERVICE.create(petya);
        DRIVER_SERVICE.create(andryuha);
        DRIVER_SERVICE.create(yulik);
        DRIVER_SERVICE.create(paul);
        DRIVER_SERVICE.create(alex);
        DRIVER_SERVICE.create(serhiy);
        DRIVER_SERVICE.create(michael);

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
        CAR_SERVICE.create(q5);
        CAR_SERVICE.create(modelX);
        System.out.println(CAR_SERVICE.get(q5.getId()));
        System.out.println(CAR_SERVICE.get(modelX.getId()));

        CAR_SERVICE.getAll().forEach(System.out::println);
        CAR_SERVICE.getAllByDriver(q5.getId()).forEach(System.out::println);
        CAR_SERVICE.getAllByDriver(tesla.getId()).forEach(System.out::println);

        q5.setModel("Q_5");
        CAR_SERVICE.update(q5);
        System.out.println(CAR_SERVICE.get(q5.getId()));

        CAR_SERVICE.removeDriverFromCar(petya, q5);
        CAR_SERVICE.get(q5.getId()).getDrivers().forEach(System.out::println);

        CAR_SERVICE.addDriverToCar(petya, modelX);
        CAR_SERVICE.get(modelX.getId()).getDrivers().forEach(System.out::println);

        CAR_SERVICE.delete(q5.getId());
        CAR_SERVICE.getAll().forEach(System.out::println);
    }
}
