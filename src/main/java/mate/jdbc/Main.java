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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        // create cars
        Driver max = new Driver();
        max.setName("Max");
        max.setLicenseNumber("m111");
        driverService.create(max);

        Driver dan = new Driver();
        dan.setName("Dan");
        dan.setLicenseNumber("d222");
        driverService.create(dan);

        List<Driver> driversForLexusNx300 = new ArrayList<>();
        driversForLexusNx300.add(max);
        driversForLexusNx300.add(dan);

        Manufacturer lexusManufacturer = new Manufacturer();
        lexusManufacturer.setName("Lexus");
        lexusManufacturer.setCountry("Japan");
        manufacturerService.create(lexusManufacturer);

        Car lexusNx300 = new Car();
        lexusNx300.setManufacturer(lexusManufacturer);
        lexusNx300.setModel("nx300");
        lexusNx300.setDriverList(driversForLexusNx300);

        carService.create(lexusNx300);

        Driver ron = new Driver();
        ron.setName("Ron");
        ron.setLicenseNumber("r333");
        driverService.create(ron);

        Driver gary = new Driver();
        gary.setName("Gary");
        gary.setLicenseNumber("d444");
        driverService.create(gary);

        List<Driver> driversForSportageKia = new ArrayList<>();
        driversForSportageKia.add(ron);
        driversForSportageKia.add(gary);

        Manufacturer kiaManufacturer = new Manufacturer();
        kiaManufacturer.setName("Kia");
        kiaManufacturer.setCountry("Korea");
        manufacturerService.create(kiaManufacturer);

        Car sportage = new Car();
        sportage.setManufacturer(kiaManufacturer);
        sportage.setModel("Sportage");
        sportage.setDriverList(driversForSportageKia);

        carService.create(sportage);

        // get car
        System.out.println(carService.get(1L));

        // get all cars
        carService.getAll().forEach(System.out::println);

        // update car
        Car mini = new Car();
        mini.setId(1L);
        mini.setModel("mini");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        mini.setManufacturer(manufacturer);

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("b555");
        driverService.create(bob);
        Driver tim = new Driver();
        tim.setName("Tim");
        tim.setLicenseNumber("t333");
        driverService.create(tim);

        List<Driver> driversForMini = new ArrayList<>();
        driversForMini.add(bob);
        driversForMini.add(tim);
        driversForMini.add(gary);
        mini.setDriverList(driversForMini);

        carService.update(mini);

        // delete car
        System.out.println(carService.delete(2L));

        // get all cars by driver
        carService.getAllByDriver(1L).forEach(System.out::println);

        // add driver to car
        carService.addDriverToCar(driverService.get(3L), carService.get(1L));

        // remove driver from car
        carService.removeDriverFromCar(driverService.get(1L), carService.get(1L));
    }
}
