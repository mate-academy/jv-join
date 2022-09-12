package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(bmw);
        manufacturerService.create(toyota);

        System.out.println(bmw);
        System.out.println(toyota);
        System.out.println("\n Created 2 manufacturers \n");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver ivan = new Driver("Ivan", "UA999999");
        Driver vasyl = new Driver("Vasyl", "UA777777");
        Driver oleg = new Driver("Oleg", "UA888888");
        driverService.create(ivan);
        driverService.create(vasyl);
        driverService.create(oleg);

        List<Driver> bmwDrivers = new ArrayList<>();
        bmwDrivers.add(ivan);
        bmwDrivers.add(vasyl);
        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(oleg);

        System.out.println("\n Created 3 drivers \n");

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmwCar = new Car("x5",bmw, bmwDrivers);
        Car toyotaCar = new Car("camry",toyota,toyotaDrivers);
        carService.create(bmwCar);
        carService.create(toyotaCar);

        System.out.println("\n Created 2 cars \n");

        System.out.println(carService.getAll());

        carService.addDriverToCar(oleg, toyotaCar);
        System.out.println(carService.get(toyotaCar.getId()));
    }
}
