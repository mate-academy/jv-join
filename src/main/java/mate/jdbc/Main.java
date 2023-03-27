package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final long CAR27_ID = 27L;
    public static final long CAR2_ID = 2L;
    public static final long CAR1_ID = 1L;
    public static final long CAR3_ID = 3L;
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        //        CREATE
        Car car = new Car();
        car.setModel("Abbbababba");
        car.setManufacturer(manufacturerService.get(CAR27_ID));
        car = carService.create(car);
        //        GET
        System.out.println(carService.get(CAR2_ID));
        //        UPDATE
        car.setId(CAR1_ID);
        car.setModel("new model");
        car.setManufacturer(manufacturerService.get(CAR27_ID));
        System.out.println(carService.update(car));
        //        DELETE
        System.out.println(carService.delete(CAR1_ID));
        //        DELETE / ADD DRIVERS
        carService.addDriverToCar(new Driver(CAR3_ID, "Abdul", "osososos"), car);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverService.get(Main.CAR3_ID), car);
        carService.getAll().forEach(System.out::println);
        //        GETALLBYDRIVER
        System.out.println(carService.getAllByDriver(CAR1_ID));
    }
}
