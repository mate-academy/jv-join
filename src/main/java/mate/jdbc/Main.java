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
        Manufacturer chery = new Manufacturer("Chery", "China");

        Manufacturer ford = new Manufacturer("Ford", "USA");

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(chery);
        manufacturerService.create(ford);

        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);

        chery = manufacturers.get(0);
        chery.setCountry("China");
        manufacturerService.update(chery);
        System.out.println(manufacturerService.get(chery.getId()));
        manufacturerService.delete(chery.getId());
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverAndrii = new Driver("Andrii", "ooa2002");
        driverService.create(driverAndrii);

        Driver driverJulia = new Driver("Julia", "oao2007");
        driverService.create(driverJulia);

        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        driverService.delete(driverAndrii.getId());
        System.out.println("Driver was deleted");
        drivers.forEach(System.out::println);

        driverJulia.setLicenseNumber("ooo2022");
        System.out.println("Driver license number was updated");
        System.out.println(driverService.update(driverJulia));

        System.out.println("Getting Driver");
        System.out.println(driverService.get(driverJulia.getId()));

        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverAndrii);
        driverList.add(driverJulia);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Create 2 cars");
        Car fordMondeo = new Car("Mondeo", ford, driverList);
        carService.create(fordMondeo);
        System.out.println(carService.get(fordMondeo.getId()));
        Car cheryKimo = new Car("Kimo", chery, driverList);
        carService.create(cheryKimo);
        System.out.println(carService.get(cheryKimo.getId()));

        System.out.println(carService.getAll());
        System.out.println("--------");
        carService.removeDriverFromCar(driverAndrii, fordMondeo);
        System.out.println("Remove driverAndrii from fordMondeo!");
        System.out.println(carService.get(fordMondeo.getId()));
        System.out.println("-----------");
        carService.addDriverToCar(driverJulia, cheryKimo);
        System.out.println("Add driverJulia to cheryKimo!");

    }
}
