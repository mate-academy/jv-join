package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ResetTablesUtil;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        System.out.println("Magic STARTS...");

        ResetTablesUtil.resetTablesToInitialState();

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

        /*
        Manufacturer manufacturer = manufacturerService.get(1L);
        System.out.println("Get 1st manufacturer from DB: " + manufacturer);
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        System.out.println("Get all manufacturers from DB: " + manufacturers);
        Driver driver = driverService.get(2L);
        System.out.println("Get 2nd driver from DB: " + driver);
        List<Driver> drivers = driverService.getAll();
        System.out.println("Get all drivers from DB: " + drivers);

        manufacturers.get(1).setCountry("Japan");
        manufacturerService.update(manufacturers.get(1));
        drivers.get(0).setName("Abdul");
        driverService.update(drivers.get(0));

        manufacturerService.delete(manufacturers.get(0).getId());
        manufacturers = manufacturerService.getAll();
        System.out.println("Get all manufacturers from DB after updating 2nd one "
                + "and deleting 1st one: " + manufacturers);
        driverService.delete(drivers.get(1).getId());
        drivers = driverService.getAll();
        System.out.println("Get all drivers from DB after updating 1st one "
                + "and deleting 2nd one: " + drivers);
         */

        System.out.println("Magic is over for now. Stay tuned for new episodes ;)");
    }
}
