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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer carNumberOne = new Manufacturer("BMW", "Germany");
        manufacturerService.create(carNumberOne);
        Manufacturer carNumberTwo = new Manufacturer("Audi", "Germany");
        manufacturerService.create(carNumberTwo);
        Manufacturer carNumberThree = new Manufacturer("Tesla", "USA");
        manufacturerService.create(carNumberThree);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverNumberOne = new Driver("Alice_Cooper", "05_12_21_AI");
        driverService.create(driverNumberOne);
        Driver driverNumberTwo = new Driver("Maria_De_Santo", "06_12_21_AI");
        driverService.create(driverNumberTwo);
        Driver driverNumberThree = new Driver("James_Bond", "07_12_21_AI");
        driverService.create(driverNumberThree);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carNumberOneModel = new Car("X5", carNumberOne, new ArrayList<>());
        carService.create(carNumberOneModel);
        carService.addDriverToCar(driverNumberOne, carNumberOneModel);
        Car carNumberTwoModel = new Car("TT", carNumberTwo, new ArrayList<>());
        carService.create(carNumberTwoModel);
        carService.addDriverToCar(driverNumberTwo, carNumberTwoModel);
        Car carNumberThreeModel = new Car("Model S", carNumberThree, new ArrayList<>());
        carService.create(carNumberThreeModel);
        carService.addDriverToCar(driverNumberThree, carNumberThreeModel);

        carService.getAllByDriver(driverNumberOne.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(driverNumberOne, carNumberOneModel);
        carService.getAllByDriver(driverNumberTwo.getId()).forEach(System.out::println);
        carService.getAllByDriver(driverNumberOne.getId()).forEach(System.out::println);
        carService.getAllByDriver(driverNumberThree.getId()).forEach(System.out::println);
    }
}
