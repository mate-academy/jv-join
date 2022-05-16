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
        driverService.create(new Driver("John", "52631"));
        driverService.create(new Driver("Bill", "1654"));
        driverService.create(new Driver("Den", "79546"));
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(new Manufacturer("bmw", "Germany"));
        manufacturerService.create(new Manufacturer("ferrari", "Italy"));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audi = new Car("audi", manufacturerService.get(10L),
                List.of(driverService.get(1L), driverService.get(7L)));
        Car bmw = new Car("BMW", manufacturerService.get(15L),
                List.of(driverService.get(9L), driverService.get(10L)));
        Car ferrari = new Car("Ferrari", manufacturerService.get(16L),
                List.of(driverService.get(9L), driverService.get(10L), driverService.get(1L)));
        carService.create(audi);
        carService.create(bmw);
        carService.create(ferrari);
        System.out.println(carService.get(bmw.getId()));
        carService.addDriverToCar(driverService.get(1L), bmw);
        carService.getAllByDriver(10L);
        carService.delete(ferrari.getId());
    }
}
