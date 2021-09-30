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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer volkswagen = new Manufacturer();
        volkswagen.setName("Volkswagen");
        volkswagen.setCountry("Germany");
        manufacturerService.create(volkswagen);
        Manufacturer landRover = new Manufacturer();
        landRover.setName("Land Rover");
        landRover.setCountry("United Kingdom");
        manufacturerService.create(landRover);
        Manufacturer cadillac = new Manufacturer();
        cadillac.setName("Cadillac");
        cadillac.setCountry("USA");
        manufacturerService.create(cadillac);
        List<Manufacturer> allManufacturers = manufacturerService.getAll();
        System.out.println("Manufacturers:");
        allManufacturers.forEach(System.out::println);
        System.out.println(System.lineSeparator());
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver petro = new Driver("Petro", "PET983453");
        driverService.create(petro);
        Driver stepan = new Driver("Stepan", "STEP983453");
        driverService.create(stepan);
        Driver vasyl = new Driver("Vasyl", "GRT983453");
        driverService.create(vasyl);
        Driver max = new Driver("Max", "MAX983453");
        driverService.create(max);
        List<Driver> allDrivers = driverService.getAll();
        System.out.println("Drivers:");
        allDrivers.forEach(System.out::println);
        System.out.println(System.lineSeparator());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car vwTouareg = new Car("Touareg",volkswagen);
        Car landRoverRangeRover = new Car("Range Rover", landRover);
        Car cadillacEscalade = new Car("Escalade", cadillac);
        carService.create(vwTouareg);
        carService.create(landRoverRangeRover);
        carService.create(cadillacEscalade);
        List<Car> allCars = carService.getAll();
        System.out.println("Cars:");
        allCars.forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.addDriverToCar(petro, vwTouareg);
        carService.addDriverToCar(stepan, landRoverRangeRover);
        carService.addDriverToCar(vasyl, cadillacEscalade);
        carService.addDriverToCar(max, cadillacEscalade);
        carService.addDriverToCar(max, vwTouareg);
        carService.addDriverToCar(petro, landRoverRangeRover);
        List<Car> carsWithDrivers = carService.getAll();
        System.out.println("Cars with drivers:");
        carsWithDrivers.forEach(System.out::println);
        System.out.println(System.lineSeparator());
        carService.removeDriverFromCar(petro, vwTouareg);
        carService.removeDriverFromCar(max, cadillacEscalade);
        cadillacEscalade.setModel("Escalade ESV");
        vwTouareg.setModel("Touareg V6");
        carService.delete(landRoverRangeRover.getId());
        carService.update(cadillacEscalade);
        carService.update(vwTouareg);
        System.out.println("Modified cars:");
        List<Car> all = carService.getAll();
        all.forEach(System.out::println);
    }
}
