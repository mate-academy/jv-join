package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver anton = new Driver("Anton", "1111");
        Driver alex = new Driver("Alex", "9999");
        List<Driver> driverList = new ArrayList<>();
        driverList.add(anton);
        driverList.add(alex);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        driverList.forEach(driverService::create);

        System.out.println(driverService.getAll());

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Audi", "Germany");

        manufacturerService.create(manufacturer);
    }
}
