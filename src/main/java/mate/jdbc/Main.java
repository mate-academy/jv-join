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
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        Manufacturer manufacturerBmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerBmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJohn = new Driver("John", "DG98656");
        Driver driverMark = new Driver("Mark", "FG87453");
        Driver driverCara = new Driver("Cara", "LK87546");
        driverService.create(driverJohn);
        driverService.create(driverMark);
        driverService.create(driverCara);

        List<Driver> firstCarDrivers = new ArrayList<>();
        List<Driver> secondCarDrivers = new ArrayList<>();

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Model5", manufacturerBmw, firstCarDrivers);
        Car secondCar = new Car("Model6", manufacturerToyota, secondCarDrivers);
        carService.create(firstCar);
        carService.create(secondCar);
        firstCarDrivers.add(driverMark);
        firstCarDrivers.add(driverCara);
        secondCarDrivers.add(driverMark);
        secondCarDrivers.add(driverJohn);
        carService.getAllByDriver(firstCar.getId());
        firstCar.setManufacturer(manufacturerService.get(manufacturerBmw.getId()));
        carService.update(firstCar);
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(driverCara, firstCar);
        carService.getAllByDriver(driverMark.getId()).forEach(System.out::println);

        carService.delete(secondCar.getId());
    }
}
