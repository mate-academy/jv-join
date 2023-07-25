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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driver = driverService.createDefaultDriver();
        Driver createdDriver = driverService.create(driver);

        Manufacturer manufacturer = manufacturerService.createDefautManufacturer();
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);

        Car car = carService.createDefaultCar(createdManufacturer, createdDriver);

        Car createdCar = carService.create(car);
        System.out.println("Created car: " + createdCar);

        Car getCarById = carService.get(createdCar.getId());
        System.out.println("car by id: " + getCarById);

        System.out.println("All cars:");
        carService.getAll().forEach(System.out::println);

        createdCar.setModel("bogdan turbo");
        Car updatedCar = carService.update(createdCar);
        System.out.println("Updated car: " + updatedCar);

        boolean deletedCar = carService.delete(createdCar.getId());
        System.out.println("Deleted car: " + deletedCar);

        System.out.println("Add driver to car:");
        carService.addDriverToCar(driver, createdCar);
        List<Driver> carDrivers = createdCar.getDrivers();
        for (Driver carDriver : carDrivers) {
            System.out.println(carDriver);
        }

        System.out.println("Remove driver from Car:");
        carService.removeDriverFromCar(driver,car);
        car.getDrivers().forEach(System.out::println);

        boolean deletedDriver = carService.delete(driver.getId());
        System.out.println("Deleted driver:" + deletedDriver);

        System.out.println("Get all drivers for car:");
        List<Driver> allDriversByCar = carService.getAllDriversByCar(createdCar.getId());
        for (Driver someDriver : allDriversByCar) {
            System.out.println(someDriver);
        }

        System.out.println("Get all cars for driver:");
        List<Car> allByDriver = carService.getAllByDriver(driver.getId());
        for (Car someCar : allByDriver) {
            System.out.println(someCar);
        }
    }
}
