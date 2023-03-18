package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector INJECTOR = Injector.getInstance("mate");

    public static void main(String[] args) {
        final CarService carService = (CarService) INJECTOR.getInstance(CarService.class);

        Car car = new Car();
        car.setModel("Audi A3");
        Manufacturer manufacturer = new Manufacturer(1L, "Audi", "Germany");
        car.setManufacturer(manufacturer);
        Driver driver = new Driver(1L, "John", "12345");
        car.getDrivers().add(driver);
        Car createdCar = carService.create(car);
        System.out.println(createdCar);

        Car updatedCar = new Car();
        updatedCar.setId(4L);
        updatedCar.setModel("model");

        Manufacturer updatedManufacturer = new Manufacturer();
        updatedManufacturer.setId(16L);
        updatedCar.setManufacturer(updatedManufacturer);

        Driver updatedDriver = new Driver();
        updatedDriver.setId(6L);
        updatedCar.getDrivers().add(updatedDriver);
        carService.update(updatedCar);

        Car retrievedCar = carService.get(4L);
        System.out.println(retrievedCar);

        List<Car> allCars = carService.getAll();
        System.out.println(allCars);

        System.out.println(carService.delete(1L));

        System.out.println(carService.getAllByDriver(1L));

        Driver newDriver = new Driver();
        newDriver.setId(1L);
        newDriver.setName("John");
        newDriver.setLicenseNumber("293293");

        carService.addDriverToCar(newDriver, car);

        System.out.println(car);
        carService.removeDriverFromCar(newDriver, car);
        System.out.println(car);
    }
}
