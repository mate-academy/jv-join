package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final String PACKAGE_NAME = "mate.jdbc";
    private static final Injector injector = Injector.getInstance(PACKAGE_NAME);

    public static void main(String[] args) {
        driverTest();
        manufacturerTest();
    }

    private static void driverTest() {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println("\t\tTESTS FOR DRIVERS");

        System.out.println("\nTest for create");
        Driver andrey = driverService.create(new Driver("Andrey", "1234567"));
        Driver anna = driverService.create(new Driver("Anna", "6666666"));
        Driver lena = driverService.create(new Driver("Lena", "121212121"));
        Driver anton = driverService.create(new Driver("Anton", "111"));
        System.out.println(andrey);
        System.out.println(anna);
        System.out.println(lena);
        System.out.println(anton);

        System.out.println("\nTest for getAll()");
        System.out.println("DriverList: " + driverService.getAll());

        System.out.println("\nTest for update");
        System.out.println("Before update: " + anna);
        anna.setLicenseNumber("666");
        System.out.println(driverService.update(anna));
        System.out.println("After update: " + anna);

        System.out.println("\nTest for get");
        System.out.println(driverService.get(andrey.getId()));
        System.out.println(driverService.get(lena.getId()));

        System.out.println("\nTest for delete");
        System.out.println("deleting " + anton);
        driverService.delete(anton.getId());
        System.out.println("DriverList: " + driverService.getAll());
    }

    private static void manufacturerTest() {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println("\t\tTESTS FOR MANUFACTURERS");

        System.out.println("\nTest for create");
        Manufacturer bentley
                = manufacturerService.create(new Manufacturer("Bentley", "Great Britain"));
        Manufacturer ferrari
                = manufacturerService.create(new Manufacturer("Ferrari", "Italy"));
        Manufacturer mercedes
                = manufacturerService.create(new Manufacturer("Mercedes", "Germany"));
        Manufacturer hyundai
                = manufacturerService.create(new Manufacturer("Hyundai", "South Korea"));
        System.out.println(bentley);
        System.out.println(ferrari);
        System.out.println(mercedes);
        System.out.println(hyundai);

        System.out.println("\nTest for getAll()");
        System.out.println("Manufacturer List: " + manufacturerService.getAll());

        System.out.println("\nTest for update");
        System.out.println("Before update: " + hyundai);
        hyundai.setName("Hyundai Motor Company");
        System.out.println(manufacturerService.update(hyundai));
        System.out.println("After update: " + hyundai);

        System.out.println("\nTest for get");
        System.out.println(manufacturerService.get(ferrari.getId()));
        System.out.println(manufacturerService.get(bentley.getId()));

        System.out.println("\nTest for delete");
        System.out.println("deleting " + ferrari);
        manufacturerService.delete(ferrari.getId());
        System.out.println("Manufacturer List: " + manufacturerService.getAll());
    }
}

