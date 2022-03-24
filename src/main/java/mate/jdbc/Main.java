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
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");

        Manufacturer volkswagen = new Manufacturer();
        volkswagen.setName("Volkswagen");
        volkswagen.setCountry("Germany");

        Manufacturer subaru = new Manufacturer();
        subaru.setName("Subaru");
        subaru.setCountry("Japan");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        manufacturerService.create(bmw);
        manufacturerService.create(volkswagen);
        manufacturerService.create(subaru);
        manufacturerService.getAll().forEach(System.out::println);

        Driver alice = new Driver();
        alice.setName("Alise");
        alice.setLicenseNumber("22222");

        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("33333");

        Driver jack = new Driver();
        jack.setName("Jack");
        jack.setLicenseNumber("44444");

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        driverService.create(alice);
        driverService.create(john);
        driverService.create(jack);
        driverService.getAll().forEach(System.out::println);

        Car taxi1 = new Car();
        taxi1.setModel("x5");
        taxi1.setManufacturer(bmw);
        taxi1.setDrivers(List.of(alice, john));

        Car taxi2 = new Car();
        taxi2.setModel("jetta");
        taxi2.setManufacturer(volkswagen);
        taxi2.setDrivers(List.of(john, jack));

        Car taxi3 = new Car();
        taxi3.setModel("forester");
        taxi3.setManufacturer(subaru);
        taxi3.setDrivers(List.of(alice));

        CarService carService = (CarService)
                injector.getInstance(CarService.class);

        System.out.println("_______create car _________");
        System.out.println("Was created: " + carService.create(taxi1));
        System.out.println("Was created: " + carService.create(taxi2));
        System.out.println("Was created: " + carService.create(taxi3));

        System.out.println("_______get car by id_________");
        System.out.println(carService.get(taxi1.getId()));
        System.out.println(carService.get(taxi2.getId()));
        System.out.println(carService.get(taxi3.getId()));

        System.out.println("_______update car ___________");
        taxi3.setModel("legacy");
        taxi3.setDrivers(List.of(alice, jack, john));
        Car updateTaxi3 = carService.update(taxi3);
        System.out.println(updateTaxi3);

        System.out.println("_______delete driver ___________");
        System.out.println("Was the car " + taxi1.getManufacturer().getName() + " removed? - "
                + carService.delete(taxi1.getId()));

        System.out.println("_______get all car by driver __________");
        System.out.println("Driver " + john.getName() + " is a taxi driver of: "
                + carService.getAllByDriver(john.getId()));

        System.out.println("_______get all cars ___________");
        carService.getAll().forEach(System.out::println);
    }
}
