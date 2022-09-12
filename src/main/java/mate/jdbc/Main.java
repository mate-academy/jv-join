package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        final Injector injector = Injector.getInstance("mate.jdbc");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final Manufacturer manufacturer = manufacturerService.get(4L);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(1L);
        Driver driver2 = driverService.get(2L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver2);

        /* Create Car */
        Car car = new Car();
        car.setModel("X5");
        car.setManufacturer(manufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        /* Get Car */
        System.out.println(carService.get(4L));

        /* Get all Cars */
        List<Car> cars = carService.getAll();
        System.out.println(cars);

        /* Add Driver to Car */
        Car newCar = carService.get(4L);
        carService.addDriverToCar(driver2, newCar);

        /* Update Car */
        Car updatedCar = carService.get(4L);
        updatedCar.setId(4L);
        updatedCar.setModel("New Model");
        updatedCar.setDrivers(drivers);
        System.out.println(carService.update(updatedCar));

        /* Get All Cars by Driver */
        System.out.println(carService.getAllByDriver(driver.getId()));
    }
}
