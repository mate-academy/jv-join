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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(manufacturerService.getAll());
        System.out.println(driverService.getAll());
        System.out.println(carService.getAll());
        Driver ivan = driverService.create(new Driver(null, "Ivan", "11233"));
        Driver sevan = driverService.create(new Driver(null, "Sevan", "87654"));
        Car chiron = new Car();
        chiron.setModel("Chiron");
        chiron.setManufacturer(manufacturerService.create(new Manufacturer(null,
                "Bugatti", "France")));
        chiron.setDrivers(List.of(ivan,sevan));
        System.out.println(manufacturerService.getAll());
        System.out.println(driverService.getAll());
        System.out.println(carService.getAll());
        System.out.println(carService.getAll());
    }
}
