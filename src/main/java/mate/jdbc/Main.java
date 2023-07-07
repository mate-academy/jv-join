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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerBmw = new Manufacturer();
        manufacturerBmw.setName("bmw");
        manufacturerBmw.setCountry("germany");

        Manufacturer manufacturerLexus = new Manufacturer();
        manufacturerLexus.setName("lexus");
        manufacturerLexus.setCountry("japan");

        Manufacturer manufacturerFord = new Manufacturer();
        manufacturerFord.setName("ford");
        manufacturerFord.setCountry("usa");

        manufacturerService.create(manufacturerBmw);
        manufacturerService.create(manufacturerLexus);
        manufacturerService.create(manufacturerFord);

        Driver driverDamien = new Driver();
        driverDamien.setName("Damien");
        driverDamien.setLicenseNumber("666");

        Driver driverJim = new Driver();
        driverJim.setName("Jim");
        driverJim.setLicenseNumber("23");

        Driver driverVivec = new Driver();
        driverVivec.setName("Vivec");
        driverVivec.setLicenseNumber("36");

        driverService.create(driverDamien);
        driverService.create(driverJim);
        driverService.create(driverVivec);

        Car bmw = new Car();
        bmw.setModel("i5");
        bmw.setManufacturer(manufacturerBmw);
        bmw.setDrivers(List.of(driverDamien, driverJim));

        Car lexus = new Car();
        lexus.setModel("lx 570");
        lexus.setManufacturer(manufacturerLexus);
        lexus.setDrivers(List.of(driverDamien, driverVivec));

        Car ford = new Car();
        ford.setModel("mustang");
        ford.setManufacturer(manufacturerFord);
        ford.setDrivers(List.of(driverVivec, driverJim));

        carService.create(bmw);
        carService.create(lexus);
        carService.create(ford);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(ford.getId()));

        ford.setModel("charger");
        List<Driver> fordDrivers = List.of(driverDamien, driverVivec, driverJim);
        ford.setDrivers(fordDrivers);
        carService.update(ford);
        carService.delete(bmw.getId());
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverDamien.getId()).forEach(System.out::println);
    }
}
