package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector ingector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) ingector.getInstance(CarService.class);
        ManufacturerService manufacturer = (ManufacturerService) ingector
                .getInstance(ManufacturerService.class);
        DriverService driver = (DriverService) ingector.getInstance(DriverService.class);

        List<Driver> drivers = List.of(driver.get(2L));
        Car car = new Car();
        car.setModel("Focus");
        car.setManufacturer(manufacturer.get(4L));
        car.setDrivers(drivers);
        System.out.println(carService.create(car));

        List<Driver> driversUpdated = List.of(driver.get(1L));
        final Car updatedCar = new Car();
        updatedCar.setId(8L);
        updatedCar.setModel("Focus1");
        updatedCar.setManufacturer(manufacturer.get(3L));
        updatedCar.setDrivers(driversUpdated);
        System.out.println(carService.update(updatedCar));

        System.out.println(carService.get(8L));
        Car carWithUpdatedDrivers = carService.get(8L);

        Driver driverNew = driver.get(1L);
        carService.addDriverToCar(driverNew, carWithUpdatedDrivers);
        System.out.println(carService.get(8L));
        carWithUpdatedDrivers = carService.get(8L);
        carService.removeDriverFromCar(driverNew, carWithUpdatedDrivers);
        System.out.println(carService.get(8L));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(4L).forEach(System.out::println);
        carService.delete(3L);
    }
}
