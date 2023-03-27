package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    @Inject
    private static final CarService carService;
    @Inject
    private static final DriverService driverService;
    @Inject
    private static final ManufacturerService manufacturerService;
    private static final String MANUFACTURER_NAME = "Company";
    private static final String MANUFACTURER_COUNTRY = "Ukraine";
    private static final String FIRST_DRIVER_NAME = "Bob";
    private static final String SECOND_DRIVER_NAME = "Alice";
    private static final String THIRD_DRIVER_NAME = "Vlad";
    private static final String FOURTH_DRIVER_NAME = "Maksym";
    private static final String FIRST_DRIVER_LICENSE_NUMBER = "1234";
    private static final String SECOND_DRIVER_LICENSE_NUMBER = "5678";
    private static final String THIRD_DRIVER_LICENSE_NUMBER = "0110";
    private static final String FOURTH_DRIVER_LICENSE_NUMBER = "1684";
    private static final String FIRST_CAR_MODEL = "FirstModel";
    private static final String SECOND_CAR_MODEL = "SecondModel";
    private static final String THIRD_CAR_MODEL = "ThirdModel";
    private static final Long DELETION_INDEX = 5L;

    static {
        carService = (CarService) injector.getInstance(CarService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    }

    public static void main(String[] args) {
        Driver firstDriver = driverService.create(new Driver(FIRST_DRIVER_NAME,
                FIRST_DRIVER_LICENSE_NUMBER));
        Driver secondDriver = driverService.create(new Driver(SECOND_DRIVER_NAME,
                SECOND_DRIVER_LICENSE_NUMBER));
        List<Driver> drivers = List.of(firstDriver, secondDriver);
        Manufacturer manufacturer = manufacturerService
                .create(new Manufacturer(MANUFACTURER_NAME, MANUFACTURER_COUNTRY));
        Car car = new Car(FIRST_CAR_MODEL, manufacturer, drivers);
        carService.create(car);
        System.out.println("Created car:\n" + carService.get(car.getId()));
        System.out.println("All cars:");
        carService.getAll().forEach(System.out::println);
        car.setModel(SECOND_CAR_MODEL);
        List<Driver> newDrivers = new ArrayList<>(car.getDrivers());
        Driver newDriver = driverService
                .create(new Driver(THIRD_DRIVER_NAME, THIRD_DRIVER_LICENSE_NUMBER));
        newDrivers.add(newDriver);
        car.setDrivers(newDrivers);
        carService.update(car);
        System.out.println("Updated car:\n" + carService.get(car.getId()));
        System.out.println("Deletion: " + carService.delete(DELETION_INDEX));
        Driver fourthDriver = driverService.create(new Driver(FOURTH_DRIVER_NAME,
                FOURTH_DRIVER_LICENSE_NUMBER));
        carService.addDriverToCar(fourthDriver, car);
        System.out.println("Added driver [" + fourthDriver + "] to car:\n"
                + carService.get(car.getId()));
        carService.removeDriverFromCar(firstDriver, car);
        System.out.println("Removed driver [" + firstDriver + "] from car:\n"
                + carService.get(car.getId()));
        Car newCar = new Car(THIRD_CAR_MODEL, manufacturer, List.of(secondDriver));
        carService.create(newCar);
        System.out.println("All cars with driver [" + secondDriver + "] :"
                + carService.getAllByDriver(secondDriver.getId()));
    }
}
