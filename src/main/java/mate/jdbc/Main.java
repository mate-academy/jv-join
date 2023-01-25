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

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerOne = new Manufacturer("oneManufacturer", "South Korea");
        Manufacturer manufacturerTwo = new Manufacturer("twoManufacturer", "Japan");
        System.out.println(manufacturerService.create(manufacturerOne));
        System.out.println(manufacturerService.create(manufacturerTwo));

        Driver driverOne = new Driver("oneDriver", "123456");
        Driver driverTwo = new Driver("twoDriverTwo", "234567");
        Driver driverThree = new Driver("threeDriver", "345678");
        System.out.println(driverService.create(driverOne));
        System.out.println(driverService.create(driverTwo));
        System.out.println(driverService.create(driverThree));

        Car carOne = new Car("KIA", manufacturerOne, List.of(driverOne, driverThree));
        Car carTwo = new Car("FORD", manufacturerTwo, List.of(driverOne, driverTwo));
        Car carThree = new Car("Alfa Romeo", manufacturerOne, List.of(driverTwo, driverThree));
        System.out.println(carService.create(carOne));
        System.out.println(carService.create(carTwo));
        System.out.println(carService.create(carThree));

        System.out.println(carService.get(carOne.getId()));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverOne.getId()).forEach(System.out::println);
        carOne.setModel("new model");
        carOne.setManufacturer(manufacturerTwo);
        carOne.setDrivers(List.of(driverTwo));
        System.out.println(carService.update(carOne));
        System.out.println(carService.delete(carThree.getId()));

        Driver driverFour = new Driver("fourDriver", "256895");
        driverService.create(driverFour);
        carService.addDriverToCar(driverFour, carTwo);
        System.out.println(carTwo);

        carService.removeDriverFromCar(driverTwo, carTwo);
        System.out.println(carTwo);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverThree.getId()).forEach(System.out::println);
    }
}
