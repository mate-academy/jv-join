package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        //        CREATE
        Car car = new Car();
        car.setModel("Abbbababba");
        car.setManufacturer(manufacturerService.get(27L));
        car = carService.create(car);
        //        GET
        System.out.println(carService.get(2L));
        //        UPDATE
        car.setId(1L);
        car.setModel("new model");
        car.setManufacturer(manufacturerService.get(27L));
        System.out.println(carService.update(car));
        //        DELETE
        System.out.println(carService.delete(1L));
        //        DELETE / ADD DRIVERS
        carService.addDriverToCar(new Driver(3L, "Abdul", "osososos"), car);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverService.get(3L), car);
        carService.getAll().forEach(System.out::println);
        //        GETALLBYDRIVER
        System.out.println(carService.getAllByDriver(1L));
    }
}
