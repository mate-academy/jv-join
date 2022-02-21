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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer mtz = manufacturerService.create(new Manufacturer("BelAZ", "Belarus"));
        Manufacturer audi = manufacturerService.create(new Manufacturer("Audi", "Italy"));
        Manufacturer generalMotors = manufacturerService.create(new Manufacturer("GMT", "USA"));

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver firsDriver = driverService.create(new Driver("Petya", "LYC1"));
        Driver secondDriver = driverService.create(new Driver("Vasya", "LYC2"));
        Driver thirdDriver = driverService.create(new Driver("Misha", "LYC3"));
        Driver fourthDriver = driverService.create(new Driver("Masha", "LYC4"));

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        Car belarusianCar = carService.create(new Car("Belaz", mtz,
                new ArrayList<>(List.of(firsDriver, secondDriver))));
        Car italianCar = carService.create(new Car("A8", audi,
                new ArrayList<>(List.of(secondDriver, thirdDriver))));
        Car americanCar = carService.create(new Car("Cadillac", generalMotors,
                new ArrayList<>(List.of(thirdDriver, fourthDriver))));
        System.out.println("Got belarusianCar = " + carService.get(belarusianCar.getId()));
        System.out.println("Got italianCar = " + carService.get(italianCar.getId()));
        System.out.println("Got americanCar = " + carService.get(americanCar.getId()));
        belarusianCar.setModel("Tractor");
        italianCar.setModel("A7");
        Manufacturer dodgeMotors = manufacturerService.create(new Manufacturer("Dodge", "USA"));
        americanCar.setManufacturer(dodgeMotors);
        System.out.println("Updated belarusianCar = " + carService.update(belarusianCar));
        System.out.println("Updated italianCar = " + carService.update(italianCar));
        System.out.println("Updated americanCar = " + carService.update(americanCar));
        carService.addDriverToCar(thirdDriver, belarusianCar);
        System.out.println("BelarusianCarWithNewDriver = " + carService.get(belarusianCar.getId()));
        carService.removeDriverFromCar(firsDriver, belarusianCar);
        System.out.println("BelarusianCarWithoutDriver = " + carService.get(belarusianCar.getId()));
        System.out.println("All cars before deletion:");
        carService.getAll().forEach(System.out::println);
        System.out.println("Deleted = " + carService.delete(belarusianCar.getId()));
        System.out.println("All cars after deletion:");
        carService.getAll().forEach(System.out::println);
        System.out.println("All cars by driver:");
        carService.getAllByDriver(fourthDriver.getId()).forEach(System.out::println);
    }
}
