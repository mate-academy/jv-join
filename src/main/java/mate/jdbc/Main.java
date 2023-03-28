package mate.jdbc;

import java.sql.SQLOutput;
import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private  static Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer peugeotManufacturer = new Manufacturer("Peugeot", "France");
        manufacturerService.create(peugeotManufacturer);
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("manufacturer created" + System.lineSeparator());

        Car peugeot308 = new Car("308", peugeotManufacturer, new ArrayList<>());
        Car peugeotPartner = new Car("Partner", peugeotManufacturer, new ArrayList<>());
        carService.create(peugeot308);
        carService.create(peugeotPartner);
        carService.getAll().forEach(System.out::println);
        System.out.println("cars created" + System.lineSeparator());

        Driver forDeleter = new Driver("For-Deleter", "000000");
        Driver forUpdater = new Driver("For-Updater", "666666");
        driverService.create(forUpdater);
        driverService.create(forDeleter);
        carService.addDriverToCar(forDeleter, peugeotPartner);
        carService.addDriverToCar(forDeleter, peugeot308);
        carService.addDriverToCar(forUpdater, peugeot308);
        carService.addDriverToCar(forUpdater, peugeotPartner);
        carService.getAllByDriver(forDeleter.getId()).forEach(System.out::println);
        carService.getAllByDriver(forDeleter.getId()).forEach(System.out::println);
        System.out.println("drivers added to cars" + System.lineSeparator());

        carService.removeDriverFromCar(forDeleter,peugeotPartner);
        carService.getAllByDriver(forDeleter.getId()).forEach(System.out::println);
        System.out.println("driver removed from car" + System.lineSeparator());

        peugeot308.setModel("3008");
        carService.update(peugeot308);
        System.out.println(carService.get(peugeot308.getId()));
        System.out.println("car updated" + System.lineSeparator());

        carService.delete(peugeot308.getId());
        carService.delete(peugeotPartner.getId());
        carService.getAll().forEach(System.out::println);
        System.out.println("cars deleted" + System.lineSeparator());

        manufacturerService.delete(peugeotManufacturer.getId());
        driverService.delete(forDeleter.getId());
        driverService.delete(forUpdater.getId());
        carService.getAll().forEach(System.out::println);
        System.out.println("must be 4 empty lines before");
    }
}
