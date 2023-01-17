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
        List<Driver> drivers = List.of(
                new Driver("Bob", "123"),
                new Driver("Alice", "456"),
                new Driver("Mike", "789")
        );
        List<Driver> newDrivers = List.of(
                new Driver("new Bob", "123"),
                new Driver("new Alice", "456"),
                new Driver("new Mike", "789")
        );
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);
        newDrivers.forEach(driverService::create);
        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("BMW", "Germany"),
                new Manufacturer("BMW", "Germany"),
                new Manufacturer("ZAZ", "Ukraine")
        );
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer newManufacturer = new Manufacturer("new BMW", "new Germany");
        manufacturerService.create(newManufacturer);
        manufacturers.forEach(manufacturerService::create);
        manufacturers = manufacturerService.getAll();

        List<Car> cars = List.of(
                new Car("model1", manufacturers.get(0), drivers),
                new Car("model2", manufacturers.get(1), drivers),
                new Car("model3", manufacturers.get(2), drivers)
        );

        // create
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("CREATE");
        cars.forEach(car -> {
            car = carService.create(car);
            System.out.println(car);
        });

        // update
        System.out.println("UPDATE");
        cars.forEach(car -> {
            car.setDrivers(newDrivers);
            car.setModel("new " + car.getModel());
            car.setManufacturer(newManufacturer);
            carService.update(car);
        });

        // getAll
        System.out.println("GET ALL");
        System.out.println(carService.getAll());

        // getAllByDriver
        System.out.println("GET ALL BY DRIVER");
        newDrivers.forEach(d -> {
            System.out.println(carService.getAllByDriver(d.getId()));
            System.out.println();
        });

        // addDriverToCar
        Driver max = new Driver("Max", "1234567890");
        System.out.println("ADD DRIVER TO CAR");
        driverService.create(max);
        carService.addDriverToCar(max, cars.get(0));
        System.out.println(carService.getAll());

        //removeDriverFromCar
        System.out.println("REMOVE DRIVER FROM CAR");
        carService.removeDriverFromCar(max, cars.get(0));
        System.out.println(carService.getAll());

        // delete
        System.out.println("DELETE");
        manufacturerService.getAll().forEach(m -> manufacturerService.delete(m.getId()));
        driverService.getAll().forEach(d -> driverService.delete(d.getId()));
        carService.getAll().forEach(c -> carService.delete(c.getId()));
        System.out.println(manufacturerService.getAll());
        System.out.println(driverService.getAll());
        System.out.println(carService.getAll());
    }
}
