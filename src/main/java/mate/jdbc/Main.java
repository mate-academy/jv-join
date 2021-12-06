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
        List<Driver> drivers = List.of(new Driver("Bob", "12345678"),
                new Driver("John", "88005353535"),
                new Driver("Ann", "cheburek"),
                new Driver("Alex", "fritz"),
                new Driver("Serhiy", "plkoijhu"));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        for (Driver driver : drivers) {
            driverService.create(driver);
        }
        List<Manufacturer> manufacturers = List.of(new Manufacturer("Toyota", "Japan"),
                new Manufacturer("Nissan", "Japan"),
                new Manufacturer("Audi", "Germany"));
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        for (Manufacturer manufacturer : manufacturers) {
            manufacturerService.create(manufacturer);
        }
        List<Driver> driversFromDB = driverService.getAll();
        List<Manufacturer> manufacturersFromDB = manufacturerService.getAll();
        List<Car> cars = List.of(new Car("Supra", manufacturersFromDB.get(0),
                        List.of(driversFromDB.get(0), driversFromDB.get(1))),
                new Car("GTR", manufacturersFromDB.get(1),
                        List.of(driversFromDB.get(1), driversFromDB.get(2))),
                new Car("e-tron GT", manufacturersFromDB.get(2),
                        List.of(driversFromDB.get(3), driversFromDB.get(4))));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        for (Car car : cars) {
            carService.create(car);
        }
        List<Car> carsFromDB = carService.getAll();
        System.out.println(carService.getAll());
        carService.addDriverToCar(driversFromDB.get(0), carsFromDB.get(2));
        System.out.println(carService.getAllByDriver(1L));
        carService.removeDriverFromCar(driversFromDB.get(0), carsFromDB.get(2));
        System.out.println(carService.getAllByDriver(1L));
        carService.delete(1L);
        System.out.println(carService.getAll());
        System.out.println(carService.get(2L));
        carsFromDB.get(1).setModel("Leaf");
        System.out.println(carService.update(carsFromDB.get(1)));
    }
}
