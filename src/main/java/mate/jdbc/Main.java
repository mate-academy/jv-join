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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car = new Car();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(7L));
        car.setId(4L);
        Manufacturer manufacturer = manufacturerService.get(1L);
        car.setManufacturer(manufacturer);
        car.setModel("Toyota Corolla");
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        Manufacturer updatedManufacture = manufacturerService.get(2L);
        Car updatedCar = new Car(4L, "Toyota Supra", updatedManufacture, drivers);

        System.out.println(carService.update(updatedCar));
        System.out.println(carService.get(updatedCar.getId()));
        Driver driver = driverService.get(8L);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));

        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println(carService.delete(3L));

    }
}
