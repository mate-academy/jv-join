package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car = carService.get(3L);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverService.get(2L));
        driverList.add(driverService.get(8L));
        car.setDrivers(driverList);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Car newCar = new Car(manufacturerService.get(5L), "EQS", driverList);
        carService.create(newCar);
        carService.delete(newCar.getId());
        carService.update(car);
        carService.addDriverToCar(driverService.get(1L), car);
        carService.removeDriverFromCar(driverService.get(1L), car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
