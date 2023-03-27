package mate.jdbc;

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

    public static void main(String[] args) {
        // Manufacturers --------
        Manufacturer fordManufacturer = manufacturerService.create(
                new Manufacturer("Ford", "USA"));
        System.out.println(manufacturerService.get(fordManufacturer.getId()));
        Manufacturer audiManufacturer = manufacturerService.create(
                new Manufacturer("Audi", "Germany"));
        System.out.println(manufacturerService.get(audiManufacturer.getId()));
        Manufacturer teslaManufacturer = manufacturerService.create(
                new Manufacturer("Tesla", "USA"));
        System.out.println(manufacturerService.get(teslaManufacturer.getId()));

        manufacturerService.getAll().forEach(System.out::println);
        fordManufacturer.setCountry("Italy");
        manufacturerService.update(fordManufacturer);

        // Drivers --------
        Driver carl = driverService.create(
                new Driver("Carl", "KGN62178MJI0"));
        System.out.println(driverService.get(carl.getId()));
        Driver tommy = driverService.create(
                new Driver("Tommy", "JKG64573LOP1"));
        System.out.println(driverService.get(tommy.getId()));
        Driver john = driverService.create(
                new Driver("John", "RET43508MHI3"));
        System.out.println(driverService.get(john.getId()));
        Driver mike = driverService.create(
                new Driver("Mike", "DFB63211MNI6"));
        System.out.println(driverService.get(mike.getId()));

        driverService.getAll().forEach(System.out::println);
        mike.setLicenceNumber("123123123123");
        driverService.update(mike);

        // Cars --------
        Car ford = carService.create(new Car("Fusion", fordManufacturer));
        System.out.println(carService.get(ford.getId()));
        carService.addDriverToCar(tommy, ford);
        carService.addDriverToCar(john, ford);
        carService.removeDriverFromCar(john, ford);

        Car audi = carService.create(new Car("A4", audiManufacturer));
        System.out.println(carService.get(audi.getId()));
        carService.addDriverToCar(mike, audi);
        carService.addDriverToCar(john, audi);
        carService.removeDriverFromCar(john, audi);
        audi.setModel("RS7");
        carService.update(audi);

        Car tesla = carService.create(new Car("ModelY", teslaManufacturer));
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
