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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");

        manufacturerService.create(toyotaManufacturer);
        manufacturerService.create(audiManufacturer);

        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);

        Driver driverIvan = new Driver("Ivan", "1234");
        Driver driverVasya = new Driver("Vasya", "5678");

        driverService.create(driverIvan);
        driverService.create(driverVasya);

        List<Driver> allDrivers = driverService.getAll();
        allDrivers.forEach(System.out::println);

        Car carRav4 = new Car("RAV4", toyotaManufacturer, List.of(driverIvan));
        Car carQ8 = new Car("Q8", audiManufacturer, List.of(driverIvan, driverVasya));

        Car toyoraCar = carService.create(carRav4);
        Car audiCar = carService.create(carQ8);

        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);

        toyoraCar.setDrivers(List.of(driverVasya, driverIvan));
        carService.delete(audiCar.getId());
        carService.update(toyoraCar);
        carService.getAll().forEach(System.out::println);
    }
}
