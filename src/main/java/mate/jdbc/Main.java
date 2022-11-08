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

    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        System.out.println("Create manufacturer Toyota");
        Manufacturer toyota = new Manufacturer();
        toyota.setCountry("Japan");
        toyota.setName("Toyota");
        manufacturerService.create(toyota);
        System.out.println(manufacturerService.get(toyota.getId()));

        System.out.println("Create manufacturer Audi");
        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);
        System.out.println(manufacturerService.get(audi.getId()));

        System.out.println("Create manufacturer Chevrolet");
        Manufacturer chevrolet = new Manufacturer();
        chevrolet.setCountry("USA");
        chevrolet.setName("Chevrolet");
        manufacturerService.create(chevrolet);
        System.out.println(manufacturerService.get(chevrolet.getId()));

        System.out.println("Create driver Andrii");
        Driver andrii = new Driver();
        andrii.setName("Andrii");
        andrii.setLicenseNumber("123456");
        driverService.create(andrii);
        System.out.println(driverService.get(andrii.getId()));

        System.out.println("Create driver Alex");
        Driver alex = new Driver();
        alex.setName("Alex");
        alex.setLicenseNumber("456789");
        driverService.create(alex);
        System.out.println(driverService.get(alex.getId()));

        System.out.println("Create driver Volodymyr");
        Driver volodymyr = new Driver();
        volodymyr.setName("Volodymyr");
        volodymyr.setLicenseNumber("753159");
        driverService.create(volodymyr);
        System.out.println(driverService.get(volodymyr.getId()));

        System.out.println("Create car Toyota Yaris");
        Car carToyotaYaris = new Car();
        carToyotaYaris.setManufacturer(toyota);
        carToyotaYaris.setModel("Yaris");
        carToyotaYaris.setDrivers(List.of(volodymyr, andrii));
        carService.create(carToyotaYaris);
        System.out.println(carService.get(carToyotaYaris.getId()));

        System.out.println("Create car Audi A7");
        Car carAudiA7 = new Car();
        carAudiA7.setManufacturer(audi);
        carAudiA7.setModel("A7");
        carAudiA7.setDrivers(List.of(alex));
        carService.create(carAudiA7);
        System.out.println(carService.get(carAudiA7.getId()));

        System.out.println("Create car Chevrolet Blazer");
        Car carChevroletBlazer = new Car();
        carChevroletBlazer.setManufacturer(chevrolet);
        carChevroletBlazer.setModel("Blazer");
        carChevroletBlazer.setDrivers(List.of(alex, andrii));
        carService.create(carChevroletBlazer);
        System.out.println(carService.get(carChevroletBlazer.getId()));

        System.out.println("Toyota Yaris drivers: ");
        carToyotaYaris.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Audi A7 drivers: ");
        carAudiA7.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Chevrolet Blazer drivers: ");
        carChevroletBlazer.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Add driver Alex to Toyota Yaris: ");
        carService.addDriverToCar(alex, carToyotaYaris);
        carService.update(carToyotaYaris);
        System.out.println(carService.get(carToyotaYaris.getId()));

        System.out.println("Get car by id: ");
        carService.get(carToyotaYaris.getId());
        System.out.println();

        System.out.println("Get all cars: ");
        carService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("Delete car Audi A7: ");
        carService.delete(carAudiA7.getId());
        System.out.println();

        System.out.println("Get all cars: ");
        carService.getAll().forEach(System.out::println);
        System.out.println();
    }
}
