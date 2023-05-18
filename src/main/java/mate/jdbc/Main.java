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
    private static final String DAEWOO = "DAEWOO";
    private static final String TOYOTA = "TOYOTA";
    private static final String MERCEDES = "MERCEDES";
    private static final String UKRAINE = "Ukraine";
    private static final String JAPAN = "Japan";
    private static final String GERMANY = "Germany";
    private static final String ANDREW = "Andrew";
    private static final String SASHA = "Sasha";
    private static final String LICENSE_NUMBER_1 = "1";
    private static final String LICENSE_NUMBER_123 = "123";
    private static final String LAND_ROVER = "Land Rover";
    private static final String NEW_MODEL = "WRX";
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
        // creating manufactures
        List<Manufacturer> manufacturerList =
                List.of(new Manufacturer(DAEWOO, UKRAINE),
                        new Manufacturer(TOYOTA, JAPAN),
                        new Manufacturer(MERCEDES, GERMANY));
        for (Manufacturer manufacturer : manufacturerList) {
            System.out.println(manufacturerService.create(manufacturer));
        }
        //creating drivers
        List<Driver> drivers =
                List.of(new Driver(ANDREW, LICENSE_NUMBER_1),
                        new Driver(SASHA, LICENSE_NUMBER_123));
        for (Driver driver : drivers) {
            System.out.println(driverService.create(driver));
        }
        System.out.println("creating car");
        Car car = new Car(LAND_ROVER, manufacturerService.get(INDEX_1));
        car.setDrivers(List.of(driverService.get(INDEX_1)));
        System.out.println(carService.create(car));

        System.out.println("get car from database");
        System.out.println(carService.get(INDEX_1));

        System.out.println("get all cars from database");
        System.out.println(carService.getAll());

        System.out.println("update car");
        System.out.println(carService.update(new Car(INDEX_1,NEW_MODEL,
                manufacturerService.get(INDEX_2))));
        System.out.println(carService.get(INDEX_1));

        System.out.println("delete car");
        carService.delete(INDEX_1);
        car.setId(INDEX_2);
        carService.create(car);

        System.out.println("add driver to car");
        carService.addDriverToCar(driverService.get(INDEX_2), carService.get(INDEX_1));
        System.out.println(carService.get(INDEX_1));

        System.out.println("delete driver from car");
        carService.removeDriverFromCar(driverService.get(INDEX_2), carService.get(INDEX_1));
        System.out.println(carService.get(INDEX_1));
    }
}
