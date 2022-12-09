package mate.jdbc;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector myInject = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        final DriverService driverService = (DriverService)
                myInject.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                myInject.getInstance(ManufacturerService.class);
        final CarService carService = (CarService)
                myInject.getInstance(CarService.class);

        manufacturerService.create(new Manufacturer("Daewoo", "Korea"));
        manufacturerService.create(new Manufacturer("Opel", "Germany"));
        manufacturerService.create(new Manufacturer("Kia", "Korea"));
        manufacturerService.create(new Manufacturer("Renault", "France"));
        manufacturerService.create(new Manufacturer("Ford", "USA"));
        manufacturerService.create(new Manufacturer("Fiat", "Italy"));
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        manufacturerService.update(new Manufacturer(1L, "Nissan", "Japan"));
        manufacturerService.delete(1L);
        try {
            System.out.println(manufacturerService.get(1L));
        } catch (NoSuchElementException e) {
            System.out.println("Manufacturer deleted");
        }
        System.out.println(manufacturerService.get(2L));

        driverService.create(new Driver("Ivan", "2317245"));
        driverService.create(new Driver("Kolya", "6243121"));
        driverService.create(new Driver("Vasil", "4643121"));
        driverService.create(new Driver("Petro", "5645433"));
        driverService.create(new Driver("Bogdan", "3874631"));
        driverService.create(new Driver("Stepan", "8774554"));
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        driverService.update(new Driver(1L, "Victor", "5465454"));
        driverService.delete(1L);
        try {
            System.out.println(driverService.get(1L));
        } catch (NoSuchElementException e) {
            System.out.println("Driver deleted");
        }
        System.out.println(driverService.get(2L));

        carService.create(new Car("Mondeo", manufacturerService.get(5L)));
        carService.create(new Car("Rio", manufacturerService.get(3L)));
        carService.create(new Car("Punto", manufacturerService.get(6L)));
        carService.create(new Car("Sandero", manufacturerService.get(4L)));
        carService.create(new Car("Quashkai", manufacturerService.get(2L)));
        carService.delete(1L);
        List<Car> carList = carService.getAll();
        for (Car car: carList) {
            System.out.println(car);
        }
        Car car = carService.get(3L);
        Driver driver = driverService.get(4L);
        System.out.println(car);
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
        System.out.println("Getting by driver 5");
        for (Car c: carService.getAllByDriver(5L)) {
            System.out.println(c);
        }
    }
}
