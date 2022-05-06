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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audiManufacturer);
        Manufacturer volkswagenManufacturer = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(volkswagenManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver saslan = new Driver("Saslan", "12345");
        Driver aslan = new Driver("Aslan", "123456");
        Driver ryan = new Driver("Ryan Gosling", "4433");
        driverService.create(saslan);
        driverService.create(aslan);
        driverService.create(ryan);

        List<Driver> audiDrivers = List.of(saslan, aslan);
        List<Driver> volkswagenDrivers = List.of(ryan);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audiCar = new Car("TT", audiManufacturer, audiDrivers);
        Car volkswagenCar = new Car("Golf", volkswagenManufacturer, volkswagenDrivers);
        carService.create(audiCar);
        carService.create(volkswagenCar);

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
