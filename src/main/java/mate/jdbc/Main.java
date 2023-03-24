package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService
            = (ManufacturerServiceImpl) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverServiceImpl) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarServiceImpl) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        final Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        final Manufacturer manufacturerLexus = new Manufacturer("Lexus", "Japan");
        final Manufacturer manufacturerTesla = new Manufacturer("Tesla", "USA");

        final Driver driverKolya = new Driver("Kolya", "777KOLYAMBUS000");
        final Driver driverVitalik = new Driver("Vitalik", "333VITALYA111");
        final Driver driverMisha = new Driver("Misha", "777MEDVEDPRO");
        final Driver driverYurik = new Driver("Yurik", "0001YURCHELA0001");
        final Driver driverAlina = new Driver("Alina", "PINKPANTERA666");
        final Driver driverKsusha = new Driver("Ksusha", "KISKA2005");
        final Driver driverMashka = new Driver("Masha", "TRUHLYA444");

        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerLexus);
        manufacturerService.create(manufacturerTesla);
        System.out.println("Insert some manufacturers into DB");

        driverService.create(driverKolya);
        driverService.create(driverVitalik);
        driverService.create(driverMisha);
        driverService.create(driverYurik);
        driverService.create(driverAlina);
        driverService.create(driverKsusha);
        driverService.create(driverMashka);
        System.out.println("Insert some drivers into DB");

        Car tesla = new Car("Model3", manufacturerTesla, new ArrayList<>());
        tesla.getDrivers().add(driverAlina);
        tesla.getDrivers().add(driverKolya);

        Car lexus = new Car("LX570", manufacturerLexus, new ArrayList<>());
        lexus.getDrivers().add(driverMisha);
        lexus.getDrivers().add(driverYurik);
        lexus.getDrivers().add(driverKsusha);

        Car ford = new Car("Focus", manufacturerFord, new ArrayList<>());
        ford.getDrivers().add(driverMashka);

        carService.create(tesla);
        carService.create(lexus);
        carService.create(ford);
        System.out.println("Create car and add some drivers");

        System.out.println(carService.getAll());
        System.out.println("All cars what we have");

        carService.addDriverToCar(driverVitalik, ford);
        carService.removeDriverFromCar(driverKolya, tesla);
        System.out.println("Remove drivers from cars");

        System.out.println(carService.getAll());
        System.out.println("Result of removing");

        tesla.setModel("Model X");
        carService.update(tesla);
        System.out.println("Update model of car Tesla");

        System.out.println(carService.getAllByDriver(driverKsusha.getId()));
        System.out.println("Get all cars by driver id");

        carService.delete(ford.getId());
        System.out.println("Delete car ford by id");

        System.out.println(carService.get(tesla.getId()));
        System.out.println("Get car tesla by id");
    }
}
