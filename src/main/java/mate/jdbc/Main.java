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
    private static ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        manufacturerService.create(new Manufacturer(null, "Honda", "China"));
        manufacturerService.get(12L);
        manufacturerService.update(new Manufacturer(12L, "Honda", "Japan"));
        manufacturerService.delete(6L);
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);

        driverService.create(new Driver(null, "Bilbo", "123888"));
        driverService.get(11L);
        driverService.update(new Driver(11L, "Frodo Beggins", "123888"));
        driverService.delete(8L);
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        carService.create(new Car(null, "Civic", manufacturerService.get(12L), null));
        carService.addDriverToCar(driverService.get(11L), carService.get(4L));
        carService.removeDriverFromCar(driverService.get(11L), carService.get(4L));
        carService.get(4L);
        carService.getAllByDriver(11L);
        carService.update(new Car(4L, "Civic",
                manufacturerService.get(12L), driverService.getAll()));
        carService.delete(1L);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
