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

        Manufacturer manufacturerOne = new Manufacturer("ManufacturerOne", "Japan");
        Manufacturer manufacturerTwo = new Manufacturer("ManufacturerTwo", "USA");
        Driver driverOne = new Driver("driverOne", "df1256");
        Driver driverTwo = new Driver("driverTwo", "gj5896");
        Driver driverThree = new Driver("driverThree", "rn7934");
        Car carOne = new Car("X6", manufacturerOne, List.of(driverOne, driverThree));
        Car carTwo = new Car("Silverado", manufacturerTwo, List.of(driverOne, driverTwo));
        Car carThree = new Car("F-16A", manufacturerOne, List.of(driverTwo, driverThree));

        System.out.println(manufacturerService.create(manufacturerOne));
        System.out.println(manufacturerService.create(manufacturerTwo));
        System.out.println(driverService.create(driverOne));
        System.out.println(driverService.create(driverTwo));
        System.out.println(driverService.create(driverThree));
        System.out.println(carService.create(carOne));
        System.out.println(carService.create(carTwo));
        System.out.println(carService.create(carThree));

        System.out.println(carService.get(carOne.getId()));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverOne.getId()).forEach(System.out::println);
        carOne.setModel("777");
        carOne.setManufacturer(manufacturerTwo);
        carOne.setDrivers(List.of(driverTwo));
        System.out.println(carService.update(carOne));
        System.out.println(carService.delete(carThree.getId()));
        Driver driverFour = new Driver("driverFour", "ll7853");
        driverService.create(driverFour);
        carService.addDriverToCar(driverFour, carTwo);
        System.out.println(carTwo);
        carService.removeDriverFromCar(driverTwo, carTwo);
        System.out.println(carTwo);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverThree.getId()).forEach(System.out::println);
    }
}
