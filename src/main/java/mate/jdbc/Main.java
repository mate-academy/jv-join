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
    private static final Injector injector =
            Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("1234567");
        driverService.create(john);
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("7890123");
        driverService.create(bob);
        Driver andrew = new Driver();
        andrew.setName("Andrew");
        andrew.setLicenseNumber("0004567");
        driverService.create(andrew);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);
        Manufacturer renault = new Manufacturer();
        renault.setName("Renault");
        renault.setCountry("France");
        manufacturerService.create(renault);
        Car audiAvant = new Car();
        audiAvant.setDrivers(List.of(bob, john));
        audiAvant.setModel("Avant");
        audiAvant.setManufacturer(audi);
        carService.create(audiAvant);
        Car renaultCaptur = new Car();
        renaultCaptur.setDrivers(List.of(bob, john));
        renaultCaptur.setManufacturer(renault);
        renaultCaptur.setModel("Captur");
        carService.create(renaultCaptur);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(andrew, renaultCaptur);
        carService.removeDriverFromCar(john, audiAvant);
        carService.delete(audiAvant.getId());
        carService.getAll().forEach(System.out::println);
    }
}
