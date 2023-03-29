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
    private static final String BMW = "BMW";
    private static final String TOYOTA = "Toyota";
    private static final String VOLVO = "Volvo";
    private static final String FORD = "Ford";
    private static final String GERMANY = "Germany";
    private static final String JAPAN = "Japan";
    private static final String SWEDEN = "Sweden";
    private static final String USA = "USA";

    private static final String TARAS = "Taras";
    private static final String PETRO = "Petro";
    private static final String LICENSE_NUMBER_1 = "12121212";
    private static final String LICENSE_NUMBER_2 = "13131313";
    private static final String SEAT = "Seat";
    private static final String NEW_MODEL = "Sport coupe";
    private static final long INDEX_1 = 1L;
    private static final long INDEX_2 = 2L;
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        List<Manufacturer> manufacturerList =
                List.of(new Manufacturer(BMW, GERMANY),
                        new Manufacturer(TOYOTA, JAPAN),
                        new Manufacturer(VOLVO, SWEDEN),
                        new Manufacturer(FORD, USA));
        for (Manufacturer manufacturer : manufacturerList) {
            System.out.println(manufacturerService.create(manufacturer));
        }

        List<Driver> driversList =
                List.of(new Driver(TARAS, LICENSE_NUMBER_1),
                        new Driver(PETRO, LICENSE_NUMBER_2));
        for (Driver driver : driversList) {
            System.out.println(driverService.create(driver));
        }

        System.out.println("Test creating new car");
        Car car = new Car(SEAT, manufacturerService.get(INDEX_1));
        car.setDrivers(List.of(driverService.get(INDEX_1)));
        System.out.println(carService.create(car));

        System.out.println("Test getting car from database");
        System.out.println(carService.get(INDEX_2));

        System.out.println("Test getting all cars from database");
        System.out.println(carService.getAll());

        System.out.println("Test updating car");
        System.out.println(carService.update(new Car(INDEX_1, NEW_MODEL,
                manufacturerService.get(INDEX_2))));
        System.out.println(carService.get(INDEX_1));

        System.out.println("Test deleting car");
        carService.delete(INDEX_1);
        System.out.println(carService.getAll());

        System.out.println("Test adding driver to car");
        carService.addDriverToCar(driverService.get(INDEX_2), carService.get(INDEX_1));
        System.out.println(carService.get(INDEX_1));

        System.out.println("Test deleting driver from car");
        carService.removeDriverFromCar(driverService.get(INDEX_2), carService.get(INDEX_1));
        System.out.println(carService.get(INDEX_1));
    }
}
