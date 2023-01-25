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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerOne = new Manufacturer();
        manufacturerOne.setName("Toyota");
        manufacturerOne.setCountry("Japan");
        System.out.println(manufacturerService.create(manufacturerOne));
        Manufacturer manufacturerTwo = new Manufacturer();
        manufacturerTwo.setName("Mercedes");
        manufacturerTwo.setCountry("Germany");
        System.out.println(manufacturerService.create(manufacturerTwo));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverOne = new Driver();
        driverOne.setName("Lolik");
        driverOne.setLicenseNumber("77777");
        System.out.println(driverService.create(driverOne));
        Driver driverTwo = new Driver();
        driverTwo.setName("Stepan");
        driverTwo.setLicenseNumber("12345");
        System.out.println(driverService.create(driverTwo));
        Driver driverThree = new Driver();
        driverThree.setName("Vasya");
        driverThree.setLicenseNumber("67890");
        System.out.println(driverService.create(driverThree));
        Driver driverFour = new Driver();
        driverFour.setName("Bohdan");
        driverFour.setLicenseNumber("13579");
        System.out.println(driverService.create(driverFour));
        Car car = new Car();
        car.setModel("RAV4");
        car.setManufacturer(manufacturerOne);
        car.setDrivers(List.of(driverOne, driverThree));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        Car carOne = new Car();
        carOne.setModel("AMG GT");
        carOne.setManufacturer(manufacturerTwo);
        carOne.setDrivers(List.of(driverOne, driverTwo, driverThree));
        System.out.println(carService.create(carOne));
        Car carTwo = new Car();
        carTwo.setModel("benz c class");
        carTwo.setManufacturer(manufacturerTwo);
        carTwo.setDrivers(List.of(driverOne, driverFour, driverTwo));
        System.out.println(carService.create(carTwo));

        System.out.println(carService.get(car.getId()));
        System.out.println(System.lineSeparator());
        carService.getAll().forEach(System.out::println);
        carOne.setManufacturer(manufacturerTwo);
        System.out.println(carService.update(carOne));
        System.out.println(carService.delete(car.getId()));
        System.out.println(System.lineSeparator());
        carService.getAll().forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.getAllByDriver(driverOne.getId()).forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.getAllByDriver(driverFour.getId()).forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.removeDriverFromCar(driverTwo, carOne);
        carOne.getDrivers().forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.addDriverToCar(driverFour, carOne);
        carOne.getDrivers().forEach(System.out::println);
    }
}
