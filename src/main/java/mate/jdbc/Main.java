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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        final Manufacturer audi =
                  manufacturerService.create(new Manufacturer("Audi","Germany"));
        final Manufacturer lexus =
                   manufacturerService.create(new Manufacturer("Lexus", "Japan"));
        final Manufacturer fiat =
                   manufacturerService.create(new Manufacturer("Fiat", "Italy"));
        fiat.setName("Ferrari");
        manufacturerService.delete(audi.getId());
        manufacturerService.update(fiat);
        System.out.println(manufacturerService.getAll());
        System.out.println(manufacturerService.delete(fiat.getId()));
        Driver driverIsla =
                driverService.create(new Driver("Isla", "AE1283OA"));
        Driver driverAlex =
                driverService.create(new Driver("Alex", "AI8813EI"));
        Driver driverBob =
                driverService.create(new Driver("Bob", "YY1220II"));
        List<Driver> listOfDrivers =
                new ArrayList<>(List.of(driverIsla, driverAlex, driverBob));
        Driver driverOleg = new Driver("Oleg", "AA1280BP");
        driverService.create(driverOleg);
        driverOleg.setLicenseNumber("AE4099");
        driverOleg.setName("Olegovich");
        driverService.update(driverOleg);
        driverService.delete(driverOleg.getId());
        System.out.println("After " + driverService.getAll());
        Car audiA3 = new Car("A3",audi);
        Car lexusIs = new Car("Fiesta",lexus);

        carService.create(audiA3);
        carService.create(lexusIs);
        carService.get(audiA3.getId());
        carService.addDriverToCar(driverIsla,audiA3);
        System.out.println(audiA3 + "Checking if driver is present ");
        carService.removeDriverFromCar(driverIsla,audiA3);
        System.out.println(audiA3 + " Driver is not present in the car!!!! ");
        carService.addDriverToCar(driverIsla,audiA3);
        carService.addDriverToCar(driverIsla,lexusIs);
        System.out.println(carService.getAllByDriver(driverIsla.getId())
                + "Driver Isla is in LexusIS and Audi A3 !!!");
        carService.getAll().forEach(System.out::println);
    }
}
