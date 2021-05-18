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
    private static CarService carService = (CarService) injector.getInstance(CarService.class);
    private static ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer honda = new Manufacturer();
        honda.setName("Honda");
        honda.setCountry("Japan");
        manufacturerService.create(honda);

        Manufacturer lambdajhini = new Manufacturer();
        lambdajhini.setName("LambdaJhini");
        lambdajhini.setCountry("MarianaTrench");
        manufacturerService.create(lambdajhini);

        Driver chupika = new Driver();
        chupika.setName("Bogdan");
        chupika.setLicenseNumber("666666");
        driverService.create(chupika);

        Driver pochepets = new Driver();
        pochepets.setName("Maxim");
        pochepets.setLicenseNumber("Double.MAX_VALUE");
        driverService.create(pochepets);

        Driver andrii = new Driver();
        andrii.setName("Its-A-Me");
        andrii.setLicenseNumber("10/10");
        driverService.create(andrii);

        Driver itsAme = new Driver();
        itsAme.setName("Mario");
        itsAme.setLicenseNumber("987654321");
        driverService.create(itsAme);

        Car mashunka = new Car();
        mashunka.setModel("Horse");
        mashunka.setManufacturer(lambdajhini);
        mashunka.setDrivers(List.of(pochepets, chupika));
        carService.create(mashunka);
        mashunka.setDrivers(new ArrayList<>(List.of(andrii, itsAme)));
        carService.addDriverToCar(pochepets, mashunka);
        System.out.println(carService.get(mashunka.getId()));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(andrii.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(andrii, mashunka);
        carService.getAllByDriver(andrii.getId()).forEach(System.out::println);
        System.out.println(carService.delete(mashunka.getId()));
    }
}
