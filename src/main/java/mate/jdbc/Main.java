package mate.jdbc;

import java.util.List;
import java.util.Random;
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
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Magic STARTS...");

        //WARNING!!!
        // Only for tests. This reset all schema to initial (empty tables) state.
        ResetDbUtil.resetDbToInitialState();
        //WARNING!!!

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.create(new Driver("Abdul", "AA-479974"));
        driverService.create(new Driver("Hassan", "AH-272727"));
        driverService.create(new Driver("Mahmud", "AM-386921"));
        driverService.create(new Driver("Bohdan Chupika", "BC-463728"));
        List<Driver> drivers = driverService.getAll();
        System.out.println(System.lineSeparator() + "Get all drivers from DB:");
        drivers.forEach(System.out::println);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car mercedes = new Car();
        Manufacturer mercedesBenzManufacturer =
                manufacturerService.create(new Manufacturer("Mercedes-Benz", "Germany"));
        mercedes.setManufacturer(mercedesBenzManufacturer);
        mercedes.setModel("C 300");
        mercedes = carService.create(mercedes);

        Car bentley = new Car();
        Manufacturer bentleyManufacturer =
                manufacturerService.create(new Manufacturer("Bentley", "England"));
        bentley.setManufacturer(bentleyManufacturer);
        bentley.setModel("Continental GT Coupe V8");
        bentley = carService.create(bentley);

        Car lexus = new Car();
        Manufacturer lexusManufacturer =
                manufacturerService.create(new Manufacturer("Lexus", "Japan"));
        lexus.setManufacturer(lexusManufacturer);
        lexus.setModel("Lexus LC Convertible");
        lexus = carService.create(lexus);

        Car ford = new Car();
        Manufacturer fordManufacturer =
                manufacturerService.create(new Manufacturer("Ford", "USA"));
        ford.setManufacturer(fordManufacturer);
        ford.setModel("F-150");
        ford = carService.create(ford);

        List<Manufacturer> manufacturers = manufacturerService.getAll();
        System.out.println(System.lineSeparator() + "Get all manufacturers from DB:");
        manufacturers.forEach(System.out::println);

        carService.addDriverToCar(drivers.get(1), mercedes);
        carService.addDriverToCar(drivers.get(2), mercedes);
        carService.addDriverToCar(drivers.get(0), bentley);
        carService.addDriverToCar(drivers.get(2), bentley);
        carService.addDriverToCar(drivers.get(0), lexus);
        carService.addDriverToCar(drivers.get(1), lexus);
        carService.addDriverToCar(drivers.get(3), ford);

        Long randomCarId = random.longs(1, 5)
                .limit(1)
                .findFirst()
                .getAsLong();
        Car randomCarFromDb = carService.get(randomCarId);
        System.out.println(System.lineSeparator()
                + "Get random car from DB: " + System.lineSeparator()
                + randomCarFromDb);

        randomCarFromDb.getManufacturer().setName("Roman Yuskevych");
        randomCarFromDb.getManufacturer().setCountry("Ukraine");
        randomCarFromDb.setModel("BestModelEver");
        manufacturerService.update(randomCarFromDb.getManufacturer());
        carService.update(randomCarFromDb);
        while (randomCarFromDb.getDrivers().size() > 0) {
            carService.removeDriverFromCar(randomCarFromDb.getDrivers().get(0), randomCarFromDb);
        }
        Driver bestDriver = driverService.create(new Driver("Michael Schumacher", "MS-111111"));
        carService.addDriverToCar(bestDriver, randomCarFromDb);
        System.out.println("Same car from DB after update: " + System.lineSeparator()
                + randomCarFromDb);
        carService.delete(randomCarFromDb.getId());

        List<Car> cars = carService.getAll();
        System.out.println(System.lineSeparator()
                + "Get all cars from DB after deleting that random car:");
        cars.forEach(System.out::println);

        Driver randomDriver = drivers.get(random.nextInt(drivers.size()));
        System.out.println(System.lineSeparator()
                + "Get all cars from DB driven by " + randomDriver.getName() + ":");
        carService.getAllByDriver(randomDriver.getId()).forEach(System.out::println);

        System.out.println(System.lineSeparator()
                + "Magic is over for now. Stay tuned for new episodes ;)");
    }
}
