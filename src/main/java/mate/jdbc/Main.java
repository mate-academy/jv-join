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
        Manufacturer firstManufacturer = new Manufacturer("OPEL", "GERMANY");
        Manufacturer secondManufacturer = new Manufacturer("RENAULT", "FRANCE");
        Manufacturer thirdManufacturer = new Manufacturer("Hyundai", "KOREA");
        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);
        manufacturerService.create(thirdManufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("Bob", "135970");
        driverService.create(firstDriver);
        Driver secondDriver = new Driver("Alex", "246080");
        driverService.create(secondDriver);
        Driver thirdDriver = new Driver("Bogdan", "123789");
        driverService.create(thirdDriver);
        List<Driver> opelDrivers = new ArrayList<>();
        opelDrivers.add(firstDriver);
        opelDrivers.add(secondDriver);
        List<Driver> renaultDrivers = new ArrayList<>();
        renaultDrivers.add(thirdDriver);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car newCar = new Car(null,"Megan", secondManufacturer, renaultDrivers);
        carService.create(newCar);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        List<Car> allByDriver = carService.getAllByDriver(firstDriver.getId());
        allByDriver.forEach(System.out::println);

    }
}
