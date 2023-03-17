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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer honda = new Manufacturer("Honda", "Japan");

        manufacturerService.create(mercedes);
        manufacturerService.create(bmw);
        manufacturerService.create(honda);

        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "1234");
        Driver alice = new Driver("Alice", "3456");
        Driver ivan = new Driver("Ivan", "7890");

        driverService.create(bob);
        driverService.create(alice);
        driverService.create(ivan);

        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        Car x5 = new Car("X5", manufacturerService.get(2L));
        Car m190 = new Car("190", manufacturerService.get(1L));
        Car civic = new Car("Civic", manufacturerService.get(3L));

        carService.create(x5);
        carService.create(m190);
        carService.create(civic);

        System.out.println(carService.get(1L));
        System.out.println(carService.get(2L));

        carService.getAll().forEach(System.out::println);

        carService.delete(1L);

        civic.setModel("CIVIC");
        civic.setDrivers(List.of(bob));
        carService.update(civic);

        Driver driver = driverService.get(2L);
        Car car = carService.get(3L);

        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);

        carService.getAllByDriver(2L).forEach(System.out::println);
    }
}
