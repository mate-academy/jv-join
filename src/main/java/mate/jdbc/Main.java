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
        Manufacturer dacia = new Manufacturer("Dacia", "Romania");
        manufacturerService.create(dacia);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        manufacturerService.create(nissan);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> nissanDrivers = new ArrayList<>();
        Driver dan = new Driver("Dan", "009533");
        driverService.create(dan);
        nissanDrivers.add(dan);
        Driver oleg = new Driver("Oleg", "673312");
        driverService.create(oleg);
        nissanDrivers.add(oleg);

        List<Driver> daciaDrivers = new ArrayList<>();
        Driver mary = new Driver("Mary", "864512");
        driverService.create(mary);
        daciaDrivers.add(mary);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car nissanCar = new Car("JUKE", nissan, nissanDrivers);
        carService.create(nissanCar);
        Car daciaCar = new Car("Dokker", dacia, daciaDrivers);
        carService.create(daciaCar);

        System.out.println(carService.getAll());

        carService.addDriverToCar(mary, nissanCar);
        System.out.println(carService.get(nissanCar.getId()));
    }
}
