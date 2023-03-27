package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    private static final Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
    private static final Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
    private static final Manufacturer teslaManufacturer = new Manufacturer("Tesla", "USA");

    private static final Driver carl = new Driver("Carl", "KGN62178MJI0");
    private static final Driver tommy = new Driver("Tommy", "JKG64573LOP1");
    private static final Driver john = new Driver("John", "RET43508MHI3");
    private static final Driver mike = new Driver("Mike", "DFB63211MNI6");

    private static final Car ford = new Car("Fusion", fordManufacturer, new ArrayList<>());
    private static final Car audi = new Car("A4", audiManufacturer, new ArrayList<>());
    private static final Car tesla = new Car("ModelY", teslaManufacturer, new ArrayList<>());

    public static void main(String[] args) {
        // Manufacturers --------
        manufacturerService.create(fordManufacturer);
        System.out.println(manufacturerService.get(fordManufacturer.getId()));
        manufacturerService.create(audiManufacturer);
        System.out.println(manufacturerService.get(audiManufacturer.getId()));
        manufacturerService.create(teslaManufacturer);
        System.out.println(manufacturerService.get(teslaManufacturer.getId()));
        manufacturerService.getAll().forEach(System.out::println);
        fordManufacturer.setCountry("Italy");
        manufacturerService.update(fordManufacturer);

        // Drivers --------
        driverService.create(carl);
        System.out.println(driverService.get(carl.getId()));
        driverService.create(tommy);
        System.out.println(driverService.get(tommy.getId()));
        driverService.create(john);
        System.out.println(driverService.get(john.getId()));
        driverService.create(mike);
        System.out.println(driverService.get(mike.getId()));
        driverService.getAll().forEach(System.out::println);
        mike.setLicenceNumber("123123123123");
        driverService.update(mike);

        // Cars --------
        carService.create(ford);
        System.out.println(carService.get(ford.getId()));
        carService.addDriverToCar(tommy, ford);
        carService.addDriverToCar(john, ford);
        carService.removeDriverFromCar(john, ford);

        carService.create(audi);
        System.out.println(carService.get(audi.getId()));
        carService.addDriverToCar(mike, audi);
        carService.addDriverToCar(john, audi);
        carService.removeDriverFromCar(john, audi);
        audi.setModel("RS7");
        carService.update(audi);

        carService.create(tesla);
        System.out.println(carService.get(tesla.getId()));
        carService.addDriverToCar(carl, tesla);
        carService.addDriverToCar(tommy, tesla);
        carService.addDriverToCar(john, tesla);
        carService.removeDriverFromCar(tommy, tesla);

        carService.getCarsForDriver(tommy.getId()).forEach(System.out::println);
        carService.getAll()
                .forEach(car -> System.out.println(carService.getDriversForCar(car.getId())));

        carService.getAll().forEach(car -> carService.delete(car.getId()));
    }
}
