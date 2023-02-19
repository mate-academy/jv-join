package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Long FIRST_DRIVER_ID = 1L;
    private static final Long SECOND_DRIVER_ID = 2L;
    private static final Long THIRD_DRIVER_ID = 3L;
    private static final Long FIRST_CAR_MANUFACTURER_ID = 2L;
    private static final Long SECOND_CAR_MANUFACTURER_ID = 7L;
    private static final Long FIRST_CAR_ID = 17L;
    private static final Long SECOND_CAR_ID = 18L;
    private static final String FIRST_CAR_MODEL = "A6";
    private static final String SECOND_CAR_MODEL = "Focus";
    private static final String ANOTHER_SECOND_CAR_MODEL = "Fiesta";
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer firstCarManufacturer = manufacturerService.get(FIRST_CAR_MANUFACTURER_ID);
        List<Driver> firstCarDrivers
                = List.of(driverService.get(FIRST_DRIVER_ID), driverService.get(SECOND_DRIVER_ID));
        Car firstCar = new Car();
        firstCar.setModel(FIRST_CAR_MODEL);
        firstCar.setManufacturer(firstCarManufacturer);
        firstCar.setDrivers(firstCarDrivers);

        Manufacturer secondCarManufacturer = manufacturerService.get(SECOND_CAR_MANUFACTURER_ID);
        List<Driver> secondCarDrivers
                = List.of(driverService.get(SECOND_DRIVER_ID), driverService.get(THIRD_DRIVER_ID));
        Car secondCar = new Car();
        secondCar.setModel(SECOND_CAR_MODEL);
        secondCar.setManufacturer(secondCarManufacturer);
        secondCar.setDrivers(secondCarDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(firstCar));
        System.out.println(carService.create(secondCar));

        System.out.println(carService.get(SECOND_CAR_ID));
        Car newSecondCar = carService.get(SECOND_CAR_ID);
        System.out.println(newSecondCar);
        newSecondCar.setModel(ANOTHER_SECOND_CAR_MODEL);
        System.out.println(carService.update(newSecondCar));

        Car newFirstCar = carService.get(FIRST_CAR_ID);
        carService.addDriverToCar(driverService.get(THIRD_DRIVER_ID), newFirstCar);
        carService.removeDriverFromCar(driverService.get(THIRD_DRIVER_ID), newSecondCar);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(THIRD_DRIVER_ID).forEach(System.out::println);

        carService.delete(SECOND_CAR_ID);
        carService.getAll().forEach(System.out::println);
    }
}
