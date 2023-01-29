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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverOne = new Driver("Giwi", "111");
        Driver driverTwo = new Driver("Gogi", "222");
        Driver driverThree = new Driver("Koba", "555");
        Driver driverFour = new Driver("Ashan", "666");
        driverService.create(driverOne);
        driverService.create(driverTwo);
        driverService.create(driverThree);
        driverService.create(driverFour);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer firstManufacturer = new Manufacturer("Lanos", "Ukraine");
        Manufacturer secondManufacturer = new Manufacturer("Rolls-Royce", "England");
        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carOne = new Car("lanos", firstManufacturer, List.of(driverFour, driverOne));
        Car carTwo = new Car("Rolls-Royce", secondManufacturer, List.of(driverThree, driverTwo));
        carService.create(carOne);
        carService.create(carTwo);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverFour, carOne);
        carService.removeDriverFromCar(driverTwo, carTwo);
        carService.addDriverToCar(driverTwo, carOne);
        carService.addDriverToCar(driverFour, carTwo);
        System.out.println(carService.get(carOne.getId()));
        System.out.println(carService.get(carTwo.getId()));
        carService.getAllByDriver(driverFour.getId()).forEach(System.out::println);
    }
}
