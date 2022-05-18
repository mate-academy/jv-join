package mate.jdbc;

import java.util.ArrayList;
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
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerToyota);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverMike = new Driver("Mike", "AC1111");
        Driver driverBob = new Driver("Bob","AC2222");
        driverService.create(driverMike);
        driverService.create(driverBob);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car camry = new Car("Camry", manufacturerToyota, new ArrayList<>());
        Car rav4 = new Car("RAV4", manufacturerToyota, new ArrayList<>());
        carService.create(camry);
        carService.create(rav4);
        camry.setId(17L);
        rav4.setId(18L);
        carService.addDriverToCar(driverMike, camry);
        carService.addDriverToCar(driverMike, rav4);
        carService.addDriverToCar(driverBob, camry);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverBob.getId());
        carService.removeDriverFromCar(driverMike, rav4);
        carService.delete(rav4.getId());
        System.out.println(carService.get(13L));
        carService.getAllByDriver(2L).forEach(System.out::println);
    }
}
