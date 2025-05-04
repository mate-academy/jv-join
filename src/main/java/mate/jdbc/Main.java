package mate.jdbc;

import java.util.NoSuchElementException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final long TEST_CAR_ID = 7L;
    private static final long TEST_DRIVER_ID = 1L;
    private static final long TEST_MANUFACTURER_ID = 1L;
    private static final String TEST_MODEL = "test";
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        //getAll() check
        carService.getAll().forEach(System.out::println);

        //getAllByDriver() check
        carService.getAllByDriver(TEST_DRIVER_ID).forEach(System.out::println);

        //getById() check
        carService.get(TEST_CAR_ID);

        //create() check
        Car car = new Car();
        car.setModel(TEST_MODEL);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        car.setManufacturer(manufacturerService.get(TEST_CAR_ID));
        carService.create(car);

        //update() check
        car = carService.get(TEST_CAR_ID);
        car.setModel(TEST_MODEL);
        car.setManufacturer(manufacturerService.get(TEST_MANUFACTURER_ID));
        carService.update(car);
        System.out.println(carService.get(TEST_CAR_ID));

        //delete() check
        carService.delete(TEST_CAR_ID);
        try {
            System.out.println(carService.get(TEST_CAR_ID));
        } catch (NoSuchElementException e) {
            System.out.println("As expected -> NoSuchElementException after test car was deleted");
        }

        //addDriverToCar() check
        System.out.println(carService.get(TEST_CAR_ID));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        carService.addDriverToCar(driverService.get(TEST_DRIVER_ID), carService.get(TEST_CAR_ID));
        System.out.println(carService.get(TEST_CAR_ID));

        //removeDriverFromCar() check
        carService.removeDriverFromCar(driverService.get(TEST_DRIVER_ID),
                carService.get(TEST_CAR_ID));
        System.out.println(carService.get(TEST_CAR_ID));
    }
}
