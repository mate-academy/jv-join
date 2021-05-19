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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer hudsonMotor = new Manufacturer("Hudson Motor", "USA");
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        Manufacturer fiat = new Manufacturer("Fiat", "Italy");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");

        manufacturerService.create(hudsonMotor);
        manufacturerService.create(volkswagen);
        manufacturerService.create(fiat);
        manufacturerService.create(bmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver drHornet = new Driver("Dc Johnson", "11111");
        Driver mrFillmore = new Driver("Mr Fillmore", "12321");
        Driver seniorGuido = new Driver("Senior Guido", "77777");
        Driver seniorLuigi = new Driver("Senior Luigi", "10101");

        driverService.create(drHornet);
        driverService.create(mrFillmore);
        driverService.create(seniorGuido);
        driverService.create(seniorLuigi);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        List<Driver> hornetDrivers = new ArrayList<>();
        hornetDrivers.add(drHornet);
        hornetDrivers.add(mrFillmore);

        List<Driver> volkswagenBusDrivers = new ArrayList<>();
        volkswagenBusDrivers.add(mrFillmore);

        List<Driver> fiatAndBmwDrivers = new ArrayList<>();
        fiatAndBmwDrivers.add(seniorGuido);
        fiatAndBmwDrivers.add(seniorLuigi);

        Car fabulousHudsonHornet = new Car("Hudson", hudsonMotor);
        fabulousHudsonHornet.setDrivers(hornetDrivers);
        Car volkswagenBus = new Car("Volkswagen Bus", volkswagen);
        volkswagenBus.setDrivers(volkswagenBusDrivers);
        Car fiat500 = new Car("Fiat 500", fiat);
        fiat500.setDrivers(fiatAndBmwDrivers);
        Car isettaMesser = new Car("Isetta Messer", bmw);
        isettaMesser.setDrivers(fiatAndBmwDrivers);

        carService.create(fabulousHudsonHornet);
        carService.create(volkswagenBus);
        carService.create(fiat500);
        carService.create(isettaMesser);

        carService.getAll().forEach(System.out::println);
        System.out.println("...Original list of cars...");

        System.out.println(carService.delete(isettaMesser.getId()));
        System.out.println("...Isetta Messer is deleted...");

        carService.addDriverToCar(mrFillmore, fiat500);
        carService.removeDriverFromCar(mrFillmore, fabulousHudsonHornet);
        System.out.println(carService.getAllByDriver(mrFillmore.getId()));
        System.out.println("...Car owners has changed...");

        fabulousHudsonHornet.setModel("Hudson Hornet");
        carService.update(fabulousHudsonHornet);
        System.out.println(carService.get(fabulousHudsonHornet.getId()));
        System.out.println("...Fabulous Hadson Hornet's model has changed...");

        System.out.println(carService.getAllByDriver(mrFillmore.getId()));
        System.out.println("...Mr Fillmore's cars list...");
    }
}
