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
        Driver alex = new Driver();
        alex.setName("Alex");
        alex.setLicenseNumber("fu1662");
        Driver lion = new Driver();
        lion.setName("Lion");
        lion.setLicenseNumber("ar6990");
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("zu7223");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        driverService.create(lion);
        driverService.create(alex);
        driverService.create(bob);

        Manufacturer hyundai = new Manufacturer();
        hyundai.setName("Huyndai");
        hyundai.setCountry("South Korea");
        Manufacturer daewoo = new Manufacturer();
        daewoo.setName("Daewoo");
        daewoo.setCountry("Ukraine");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(hyundai);
        manufacturerService.create(daewoo);

        List<Driver> elantraDrivers = new ArrayList<>();
        elantraDrivers.add(alex);
        elantraDrivers.add(bob);
        Car elantra = new Car();
        elantra.setModel("Elantra");
        elantra.setManufacturer(hyundai);
        elantra.setDrivers(elantraDrivers);
        List<Driver> lanosDrivers = new ArrayList<>();
        lanosDrivers.add(lion);
        Car lanos = new Car();
        lanos.setModel("Lanos");
        lanos.setManufacturer(daewoo);
        lanos.setDrivers(lanosDrivers);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        carService.create(elantra);
        carService.get(elantra.getId());
        carService.create(lanos);
        System.out.println(carService.getAll());
        System.out.println(carService.get(lanos.getId()));
        lanos.setModel("nexia");
        carService.update(lanos);
        System.out.println(carService.get(lanos.getId()));
        carService.delete(elantra.getId());
        System.out.println(carService.getAll());
        Driver tom = new Driver();
        tom.setName("Tom");
        tom.setLicenseNumber("mn4427");
        driverService.create(tom);
        carService.addDriverToCar(tom, lanos);
        System.out.println(carService.get(lanos.getId()));
        carService.removeDriverFromCar(alex, elantra);

        System.out.println(carService.getAllByDriver(lion.getId()));
        System.out.println(carService.getAllByDriver(tom.getId()));
    }
}
