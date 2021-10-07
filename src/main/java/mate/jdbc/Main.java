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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver("Stas", "1234561");
        Driver secondDriver = new Driver("Vlad", "6543211");
        Driver createdDriver = driverService.create(driver);
        Driver createdSecondDriver = driverService.create(secondDriver);
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        Manufacturer manufacturer = new Manufacturer("Ford", "USA");
        Manufacturer secondManufacturer = new Manufacturer("BMW", "Germany");
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        Manufacturer createdSecondManufacturer = manufacturerService.create(secondManufacturer);
        Car car = new Car("Fusion", createdManufacturer, drivers);
        Car secondCar = new Car("M5", createdSecondManufacturer, drivers);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car createdCar = carService.create(car);
        Car createdSecondCar = carService.create(secondCar);
        Car carGetFromId = carService.get(1L);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        boolean isCarDeleted = carService.delete(1L);
        cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        Car carForUpdate = new Car(1L, "Focus", createdManufacturer, drivers);
        Car carUpdate = carService.update(carForUpdate);
        carService.removeDriverFromCar(driverService.get(1L), carUpdate);
        cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        carService.addDriverToCar(driverService.get(1L), carUpdate);
        cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------");
        List<Car> carsByDriver = carService.getAllByDriver(1L);
        carsByDriver.forEach(System.out::println);
    }
}
