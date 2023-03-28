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

        Driver driverArtem = new Driver("Artem", "MN605640");
        Driver driverArtur = new Driver("Artur", "MN605667");
        Driver driverArtemis = new Driver("Artemis", "MN604567");
        Driver driverArtemia = new Driver("Artemia", "MN607777");
        Driver driverArtemio = new Driver("Artemio", "MN608767");
        driverService.create(driverArtem);
        driverService.create(driverArtur);
        driverService.create(driverArtemis);
        driverService.create(driverArtemia);
        driverService.create(driverArtemio);
        List<Driver> allDrivers = driverService.getAll();
        allDrivers.forEach(System.out::println);
        List<Driver> firstTeam = List.of(driverArtem, driverArtemia, driverArtemio);
        List<Driver> secondTeam = List.of(driverArtur, driverArtemis, driverArtemio);

        Car toyotaCorolla01 = new Car("Corolla",
                toyotaManufacturer,
                firstTeam);
        Car toyotaCorolla02 = new Car("Corolla",
                toyotaManufacturer,
                firstTeam);
        Car audi02 = new Car("Q7",
                toyotaManufacturer,
                allDrivers);
        Car firstCorolla = carService.create(toyotaCorolla01);
        Car secondCorolla = carService.create(toyotaCorolla02);
        Car secondAudi = carService.create(audi02);

        List<Car> allCars = carService.getAll();
        System.out.println();
        allCars.forEach(System.out::println);
        firstCorolla.setDrivers(secondTeam);
        carService.update(firstCorolla);
        System.out.println();

        Car audi01 = new Car("Q7",
                toyotaManufacturer,
                secondTeam);
        Car firstAudi = carService.create(audi01);
        carService.delete(firstAudi.getId());
        carService.getAll().forEach(System.out::println);
    }
}
