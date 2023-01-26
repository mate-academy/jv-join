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
    private static final int ID_CAR_TO_GET = 1;
    private static final int ID_CAR_TO_UPDATE = 1;
    private static final int ID_CAR_TO_DELETE = 2;
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        List<Driver> drivers = List.of(
                new Driver("Alex", "Ukraine"),
                new Driver("Scott", "Poland"),
                new Driver("Shamil", "Iraq"),
                new Driver("Jezz", "England")
        );

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);

        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("Dodge", "USA"),
                new Manufacturer("Renault", "France"),
                new Manufacturer("Peugeot", "France"),
                new Manufacturer("BMW", "Germany")
        );
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturers.forEach(manufacturerService::create);

        List<Car> cars = List.of(
                new Car("RAM 1500", manufacturers.get(0), drivers),
                new Car("Megane", manufacturers.get(1), drivers),
                new Car("507", manufacturers.get(2), drivers),
                new Car("X5", manufacturers.get(3), drivers)
        );
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Creation of new cars");
        cars.forEach(carService::create);

        System.out.println("Car from method get");
        Car carToGet = carService.get(cars.get(ID_CAR_TO_GET).getId());
        System.out.println(carService.get(carToGet.getId()));

        System.out.println("Car from method update");
        Car carToUpdate = carService.get(cars.get(ID_CAR_TO_UPDATE).getId());
        carToUpdate.setModel("WRX STI");
        Manufacturer manufacturerToUpdate = new Manufacturer("Subaru", "Japan");
        manufacturerService.create(manufacturerToUpdate);
        carToUpdate.setManufacturer(manufacturerToUpdate);
        List<Driver> driversToUpdate = List.of(
                new Driver("Bob", "Ukraine"),
                new Driver("Josh", "Japan"),
                new Driver("Harry", "England"),
                new Driver("Oleg", "USA")
        );
        driversToUpdate.forEach(driverService::create);
        carToUpdate.setDrivers(driversToUpdate);
        System.out.println(carService.update(carToUpdate));

        System.out.println("Car from method delete");
        Car carToDelete = carService.get(cars.get(ID_CAR_TO_DELETE).getId());
        System.out.println(carService.delete(carToDelete.getId()));

        System.out.println(carService.getAll());

        Driver newDriver = new Driver("Farhad", "Turkey");
        driverService.create(newDriver);
        carService.addDriverToCar(newDriver, cars.get(3));

        carService.removeDriverFromCar(newDriver, cars.get(3));

        drivers.forEach(driver -> System.out.println(carService.getAllByDriver(driver.getId())));
    }
}
