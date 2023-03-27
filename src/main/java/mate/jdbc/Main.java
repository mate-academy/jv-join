package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final DriverService driverService =
            (DriverServiceImpl) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerServiceImpl) injector.getInstance(ManufacturerService.class);
    private static CarService carService = (CarServiceImpl) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = manufacturerService.get(3L);
        Car car = new Car("RenoA2",manufacturer);
        System.out.println(carService.create(car));
        carService.getAll().stream()
                .forEach(System.out::println);
        Car carById = carService.get(11L);
        System.out.println(carById);
        carById.setModel("mitsubishiA3");
        System.out.println(carService.update(carById));
        System.out.println(carService.delete(15L));
        carService.getAll().stream()
                .forEach(System.out::println);
        Car currentCar = carService.get(13L);
        currentCar.getDrivers().stream().forEach(System.out::println);
        Driver removedDriver = driverService.get(3L);
        System.out.println(removedDriver);
        carService.removeDriverFromCar(removedDriver, currentCar);
        currentCar.getDrivers().stream().forEach(System.out::println);
        Driver addedDriver = driverService.get(3L);
        carService.addDriverToCar(addedDriver, currentCar);
        currentCar.getDrivers().stream().forEach(System.out::println);
    }
}
