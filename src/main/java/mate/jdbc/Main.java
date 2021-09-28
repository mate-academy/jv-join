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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver petro = new Driver("Petro", "PET983453");
        driverService.create(petro);
        Driver stepan = new Driver("Stepan", "STEP983453");
        driverService.create(stepan);
        Driver vasyl = new Driver("Vasyl", "GRT983453");
        driverService.create(vasyl);
        Driver max = new Driver("Max", "MAX983453");
        driverService.create(max);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car vwTouareg = new Car("Touareg",volkswagen);
        Car landRoverRangeRover = new Car("Range Rover", landRover);
        Car cadillacEscalade = new Car("Escalade", cadillac);
        carService.addDriverToCar(petro, vwTouareg);
        carService.addDriverToCar(stepan, landRoverRangeRover);
        carService.addDriverToCar(vasyl, cadillacEscalade);
        carService.addDriverToCar(max, cadillacEscalade);
        carService.addDriverToCar(max, vwTouareg);
        carService.removeDriverFromCar(petro, vwTouareg);
        carService.removeDriverFromCar(max, cadillacEscalade);
        carService.addDriverToCar(petro, landRoverRangeRover);
        carService.create(vwTouareg);
        carService.create(landRoverRangeRover);
        carService.create(cadillacEscalade);
        cadillacEscalade.setModel("Escalade ESV");
        carService.delete(landRoverRangeRover.getId());
        carService.update(cadillacEscalade);
        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);
    }
}
