package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;

public class Main {

    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        /*
        Manufacturer manufacturer1 = new Manufacturer(null, "Renault", "France");
        Manufacturer manufacturer2 = new Manufacturer(null, "Skoda", "Czech Republic");
        Manufacturer manufacturer3 = new Manufacturer(null, "Hyundai", "South Korea");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);
        Driver driver1 = new Driver(null, "Johnny Jonson","3094410014");
        Driver driver2 = new Driver(null, "Johnny Depp","3094410015");
        Driver driver3 = new Driver(null, "Peter Griffin","3094410016");
        Driver driver4 = new Driver(null, "Sarah Connor","3094410017");
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        driverService.create(driver4);
        Car car1 = new Car(null, "Logan", manufacturer1, List.of(driver1, driver2));
        Car car2 = new Car(null, "Scala", manufacturer2, List.of(driver3, driver4));
        Car car3 = new Car(null, "Accent", manufacturer3, List.of(driver1, driver4));
        */
        CarService carService = (CarService) injector.getInstance(CarService.class);
        /*
        carDao.create(car1);
        carDao.create(car2);
        carDao.create(car3);
        System.out.println(carService.get(3L));
        System.out.println(carService.get(2L));
        System.out.println(carService.get(1L));
        */
        System.out.println(carService.getAll());
    }
}
