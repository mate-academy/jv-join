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
        Manufacturer volvo = new Manufacturer();
        volvo.setCountry("Sweden");
        volvo.setName("Volvo");
        manufacturerService.create(volvo);

        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);

        Manufacturer tesla = new Manufacturer();
        tesla.setCountry("USA");
        tesla.setName("Tesla");
        manufacturerService.create(tesla);

        Driver molly = new Driver();
        molly.setName("Molly");
        molly.setLicenseNumber("89AD");
        driverService.create(molly);

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("12SD");
        driverService.create(bob);

        Driver travis = new Driver();
        travis.setName("Travis");
        travis.setLicenseNumber("90HJ");
        driverService.create(travis);

        Car carAudi = new Car();
        carAudi.setManufacturer(audi);
        carAudi.setModel("audi");
        carAudi.setDrivers(List.of(travis, molly));
        carService.create(carAudi);

        Car carTesla = new Car();
        carTesla.setManufacturer(tesla);
        carTesla.setModel("tesla");
        carTesla.setDrivers(List.of(travis));
        carService.create(carTesla);

        Car carVolvo = new Car();
        carVolvo.setManufacturer(volvo);
        carVolvo.setModel("volvo");
        carVolvo.setDrivers(List.of(bob, molly));

        System.out.println("Tesla drivers: ");
        carTesla.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Audi drivers: ");
        carAudi.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Volvo drivers: ");
        carVolvo.getDrivers().forEach(System.out::println);
        System.out.println();

        System.out.println("Get all cars: ");
        carService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("Get by id: ");
        carService.get(tesla.getId());
        System.out.println();

        System.out.println("Added driver Bob to Audi: ");
        carService.addDriverToCar(bob, carAudi);
        carService.update(carAudi);
        System.out.println(carService.get(carAudi.getId()));
    }
}
