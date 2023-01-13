package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ResetDbUtil;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        System.out.println("Magic STARTS..." + System.lineSeparator());

        //WARNING!!! Only for tests. This reset all schema to initial (empty tables) state.
        ResetDbUtil.resetDbToInitialState();
        //WARNING!!!

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(new Driver(null, "Abdul", "AA-479974"));
        driverService.create(new Driver(null, "Hassan", "AH-272727"));
        driverService.create(new Driver(null, "Mahmud", "AM-386921"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(new Manufacturer(null, "Bentley", "England"));
        manufacturerService.create(new Manufacturer(null, "Lexus", "Japan"));
        manufacturerService.create(new Manufacturer(null, "Mercedes-Benz", "Germany"));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car testCarFirst = new Car();
        testCarFirst.setManufacturer(manufacturerService.get(3L));
        testCarFirst.setModel("C 300");
        carService.create(testCarFirst);
        Car testCarSecond = new Car();
        testCarSecond.setManufacturer(manufacturerService.get(1L));
        testCarSecond.setModel("Continental GT Coupe V8");
        carService.create(testCarSecond);
        Car testCarThird = new Car();
        testCarThird.setManufacturer(manufacturerService.get(2L));
        testCarThird.setModel("Lexus LC Convertible");
        carService.create(testCarThird);

        carService.addDriverToCar(driverService.get(2L), carService.get(1L));
        carService.addDriverToCar(driverService.get(3L), carService.get(1L));
        carService.addDriverToCar(driverService.get(1L), carService.get(2L));
        carService.addDriverToCar(driverService.get(3L), carService.get(2L));
        carService.addDriverToCar(driverService.get(1L), carService.get(3L));
        carService.addDriverToCar(driverService.get(2L), carService.get(3L));

        testCarFirst = carService.get(1L);
        System.out.println("Get 1st car from DB: " + testCarFirst);
        testCarSecond = carService.get(2L);
        System.out.println("Get 2nd car from DB: " + testCarSecond);
        testCarThird = carService.get(3L);
        System.out.println("Get 3rd car from DB: " + testCarThird);

        testCarFirst.setModel("F150");
        testCarFirst.getManufacturer().setName("Ford");
        testCarFirst.getManufacturer().setCountry("USA");
        manufacturerService.update(testCarFirst.getManufacturer());
        carService.update(testCarFirst);
        carService.removeDriverFromCar(driverService.get(2L), testCarFirst);
        carService.removeDriverFromCar(driverService.get(3L), testCarFirst);
        driverService.create(new Driver(null, "Bohdan Chupika", "BC-463728"));
        carService.addDriverToCar(driverService.get(4L), testCarFirst);
        carService.addDriverToCar(driverService.get(4L), carService.get(3L));

        carService.delete(2L);
        carService.addDriverToCar(driverService.get(3L), carService.get(1L));
        List<Car> cars = carService.getAll();
        System.out.println(System.lineSeparator() + "Get all cars from DB:");
        cars.forEach(System.out::println);

        Driver testDriver = driverService.get(4L);
        System.out.println(System.lineSeparator()
                + "Get all cars from DB driven by " + testDriver.getName() + ":");
        carService.getAllByDriver(testDriver.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator()
                + "Magic is over for now. Stay tuned for new episodes ;)");
    }
}
