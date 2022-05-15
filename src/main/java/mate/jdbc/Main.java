package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerHonda = new Manufacturer("Honda", "Japan");
        Manufacturer manufacturerBMW = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerHonda);
        manufacturerService.create(manufacturerBMW);

        Driver driverRita = new Driver("Rita", "534523253");
        Driver driverKriss = new Driver("Kriss", "23532532");
        Driver driverJon = new Driver("Jon", "23532");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverRita);
        driverService.create(driverKriss);
        driverService.create(driverJon);

        List<Driver> driversTesla = new ArrayList<>();
        driversTesla.add(driverRita);
        driversTesla.add(driverKriss);
        Car carTeslaS = new Car("Sonata", manufacturerHonda, driversTesla);

        List<Driver> driversAudi = new ArrayList<>();
        driversAudi.add(driverJon);
        Car carAudiA5 = new Car("X7", manufacturerBMW, driversAudi);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(carTeslaS);
        carService.create(carAudiA5);

    }
}
