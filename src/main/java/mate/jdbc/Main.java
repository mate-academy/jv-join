package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");
    public static final CarService carService = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Car car = new Car();
        List<Driver> drivers = List.of(driverService.get(5L), driverService.get(6L));
        car.setModel("Camry");
        car.setManufacturer(manufacturerService.get(1L));
        car.setDrivers(drivers);
        carService.create(car);

        System.out.println(carService.get(3L));

        carService.getAll().forEach(System.out::println);

        System.out.println(carService.delete(1L));

        carService.getAllByDriver(2L).forEach(System.out::println);

        Car updatingCar = carService.get(5L);
        List<Driver> updatingDrivers = List.of(driverService.get(5L));
        updatingCar.setModel("Camry");
        updatingCar.setManufacturer(manufacturerService.get(1L));
        updatingCar.setDrivers(updatingDrivers);
        carService.update(updatingCar);

        carService.addDriverToCar(driverService.get(4L), carService.get(5L));
        carService.removeDriverFromCar(driverService.get(4L), carService.get(5L));
    }
}
