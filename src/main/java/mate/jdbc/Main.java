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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer(null, "Mercedes", "Germany");
        Manufacturer audi = new Manufacturer(null, "Audi", "Germany");
        Manufacturer tesla = new Manufacturer(null, "Tesla Model 3", "USA");
        manufacturerService.create(mercedes);
        manufacturerService.create(audi);
        manufacturerService.create(tesla);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver bob = new Driver(null, "Bob", "123456789LN");
        Driver alice = new Driver(null, "Alice", "123456788LN");
        Driver alex = new Driver(null, "Alex", "123456787LN");
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(alex);

        List<Driver> gold = new ArrayList<>();
        List<Driver> platinum = new ArrayList<>();
        gold.add(bob);
        gold.add(alex);
        platinum.add(alice);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("Create 3 cars!");
        Car mercedesCls = new Car("Cls", mercedes, gold);
        carService.create(mercedesCls);
        System.out.println(carService.get(mercedesCls.getId()));
        Car teslaM = new Car("Model 3", tesla, gold);
        carService.create(teslaM);
        System.out.println(carService.get(teslaM.getId()));
        Car audiQ = new Car("Q7", audi, platinum);
        carService.create(audiQ);
        System.out.println(carService.get(audiQ.getId()));
        System.out.println("--------");
        System.out.println("Delete audi car!");
        carService.delete(audiQ.getId());
        System.out.println(carService.getAll());
        System.out.println("--------");
        System.out.println("Remove Bob driver from mercedes Cls!");
        carService.removeDriverFromCar(bob, mercedesCls);
        System.out.println(carService.get(mercedesCls.getId()));
        System.out.println("-----------");
        System.out.println("Add Alice driver to teslaM!");
        carService.addDriverToCar(alice, teslaM);
        System.out.println(carService.get(teslaM.getId()));
        System.out.println("----------");
        System.out.println("Get all cars by driver Alex");
        System.out.println(carService.getAllByDriver(alex.getId()));
    }
}
