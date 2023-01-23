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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer hyndai = new Manufacturer();
        hyndai.setName("Hyndai");
        hyndai.setCountry("Korea");
        manufacturerService.create(hyndai);
        Driver ihor = new Driver();
        ihor.setName("Ihor");
        ihor.setLicenseNumber("1111");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        driverService.create(ihor);
        Driver max = new Driver();
        max.setName("Max");
        max.setLicenseNumber("2222");
        driverService.create(max);
        Driver jimmy = new Driver();
        jimmy.setName("Jimmy");
        jimmy.setLicenseNumber("7777");
        driverService.create(jimmy);
        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("8888");
        driverService.create(john);
        Driver leo = new Driver();
        leo.setName("Leo");
        leo.setLicenseNumber("0000");
        driverService.create(leo);
        List<Driver> driversForSonata = new ArrayList<>();
        driversForSonata.add(max);
        driversForSonata.add(ihor);
        List<Driver> driversForIoniq = new ArrayList<>();
        driversForIoniq.add(jimmy);
        driversForIoniq.add(john);
        Car ioniq = new Car();
        ioniq.setModel("Ioniq");
        ioniq.setManufacturer(manufacturerService.get(hyndai.getId()));
        ioniq.setDrivers(driversForIoniq);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        System.out.println("Creating Ioniq " + carService.create(ioniq));
        System.out.println("Getting Ioniq " + carService.get(ioniq.getId()));
        Car sonata = new Car();
        sonata.setModel("Sonata");
        sonata.setManufacturer(manufacturerService.get(hyndai.getId()));
        sonata.setDrivers(driversForSonata);
        System.out.println("Creating Sonata " + carService.create(sonata));
        System.out.println("Getting Sonata " + carService.get(sonata.getId()));
        sonata.setModel("Sonata new");
        carService.update(sonata);
        System.out.println(carService.get(sonata.getId()));
        carService.getAll().forEach(System.out::println);
        List<Driver> driversForTucson = new ArrayList<>();
        driversForTucson.add(max);
        driversForTucson.add(leo);
        Car tucson = new Car();
        tucson.setModel("Tucson");
        tucson.setManufacturer(manufacturerService.get(hyndai.getId()));
        tucson.setDrivers(driversForTucson);
        carService.create(tucson);
        Driver maria = new Driver();
        maria.setName("Maria");
        maria.setLicenseNumber("4444");
        driverService.create(maria);
        carService.addDriverToCar(maria, tucson);
        carService.removeDriverFromCar(leo, tucson);
        System.out.println(carService.get(tucson.getId()));
        System.out.println(carService.delete(tucson.getId()));
        carService.addDriverToCar(maria, sonata);
        carService.removeDriverFromCar(max, sonata);
        System.out.println(carService.get(sonata.getId()));
        System.out.println(carService.getAllByDriver(maria.getId()));
    }
}
