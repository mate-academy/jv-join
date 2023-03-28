package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final String CAR_MODEL_1 = "X5";
    private static final String CAR_MODEL_2 = "V40";
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car1 = new Car();
        car1.setModel(CAR_MODEL_1);
        car1.setManufacturer(manufacturerService.get(1L));
        car1.setDrivers(List.of(driverService.get(1L), driverService.get(3L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        System.out.println(carService.get(6L));
        carService.getAll().forEach(System.out::println);
        car1.setId(6L);
        car1.setModel(CAR_MODEL_2);
        car1.setManufacturer(manufacturerService.get(1L));
        car1.setDrivers(List.of(driverService.get(1L), driverService.get(5L)));
        carService.update(car1);
        carService.getAll().forEach(System.out::println);
        carService.delete(3L);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverService.get(1L), carService.get(6L));
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driverService.get(1L), carService.get(6L));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
