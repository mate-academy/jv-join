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
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Manufacturer honda = new Manufacturer();
        honda.setName("Honda");
        honda.setCountry("Japan");
        manufacturerService.create(honda);
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("aaa111");
        driverService.create(bob);
        Car hondaCRv = new Car();
        hondaCRv.setModel("CRV");
        hondaCRv.setManufacturer(honda);
        List<Driver> hondaDrivers = new ArrayList<>();
        hondaDrivers.add(bob);
        hondaCRv.setDrivers(hondaDrivers);
        carService.create(hondaCRv);
        Driver sem = new Driver();
        sem.setName("Sem");
        sem.setLicenseNumber("bbb222");
        driverService.create(sem);
        carService.addDriverToCar(sem, hondaCRv);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(bob, hondaCRv);
        carService.getAllByDriver(19L).forEach(System.out::println);
        System.out.println(carService.get(2L));
        carService.delete(13L);
        hondaCRv.setModel("Mustang");
        carService.update(hondaCRv);
    }
}
