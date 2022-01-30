package mate.jdbc;

import java.util.List;
import java.util.stream.IntStream;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        List<Manufacturer> manufacturers = createManufacturers();
        List<Driver> drivers = createDrivers();
        CarService service = (CarService) injector.getInstance(CarService.class);
        //create
        if (service.getAll().size() == 0) {
            IntStream.range(0, 10)
                    .mapToObj(i -> getNewCar("model", manufacturers.get(i)))
                    .forEach(service::create);
        }
        System.out.println("***********CREATE BLOCK RESULTS***********");
        service.getAll().forEach(System.out::println);
        service.getAll().stream()
                .map(Main::updateCar)
                .forEach(service::update);
        System.out.println("***********UPDATE BLOCK RESULTS***********");
        service.getAll().forEach(System.out::println);
        for (Car car : service.getAll()) {
            for (int i = 0; i < car.getId(); i++) {
                service.addDriverToCar(drivers.get(i), car);
            }
        }
        System.out.println("***********ADD DRIVER BLOCK RESULTS***********");
        service.getAll().forEach(System.out::println);
        for (Car car : service.getAll()) {
            for (Driver driver : car.getDrivers()) {
                if (driver.getId() % 2 != car.getId() % 2) {
                    service.removeDriverFromCar(driver, car);
                }
            }
        }
        System.out.println("***********REMOVE DRIVER BLOCK RESULTS***********");
        service.getAll().forEach(System.out::println);
        System.out.println("******************DRIVERS BY CAR*****************");
        drivers.forEach(driver -> {
            System.out.println(driver);
            service.getAllByDriver(driver.getId()).forEach(car ->
                    System.out.println("\t" + car.getModel()));
        });
    }

    private static List<Driver> createDrivers() {
        DriverService service = (DriverService) injector.getInstance(DriverService.class);
        if (service.getAll().size() == 0) {
            IntStream.range(0, 10)
                    .mapToObj(i -> getNewDriver("name", "license"))
                    .forEach(service::create);
            service.getAll().stream()
                    .map(Main::updateDriver)
                    .forEach(service::update);
        }
        return service.getAll();
    }

    private static List<Manufacturer> createManufacturers() {
        ManufacturerService service =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        if (service.getAll().size() == 0) {
            IntStream.range(0, 10)
                    .mapToObj(i -> getNewManufacturer("name", "country"))
                    .forEach(service::create);
            service.getAll().stream()
                    .map(Main::updateManufacturer)
                    .forEach(service::update);
        }
        return service.getAll();
    }

    private static Car getNewCar(String model, Manufacturer manufacturer) {
        Car car = new Car();
        car.setModel(model);
        car.setManufacturer(manufacturer);
        return car;
    }

    private static Car updateCar(Car car) {
        String number = car.getId().toString();
        car.setModel("Model_" + number);
        return car;
    }

    private static Driver getNewDriver(String name, String licenseNumber) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private static Driver updateDriver(Driver driver) {
        String number = driver.getId().toString();
        driver.setName("Driver_" + number);
        driver.setLicenseNumber("0".repeat(12 - number.length()) + number);
        return driver;
    }

    private static Manufacturer getNewManufacturer(String name, String country) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name);
        manufacturer.setCountry(country);
        return manufacturer;
    }

    private static Manufacturer updateManufacturer(Manufacturer manufacturer) {
        manufacturer.setName("ZAZ_" + manufacturer.getId());
        manufacturer.setCountry("Ukraine");
        return manufacturer;
    }

}
