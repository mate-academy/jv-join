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

    public static void main(String[] args) {
        // create drivers
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverDima = new Driver(null, "Dima", "12345");
        Driver driverOleg = new Driver(null, "Oleg", "56789");
        driverService.create(driverDima);
        driverService.create(driverOleg);
        // create manufacturers
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer hyundai = new Manufacturer(null, "hyundai", "Korea");
        Manufacturer kia = new Manufacturer(null, "KIA", "Korea");
        manufacturerService.create(hyundai);
        manufacturerService.create(kia);
        //create a car
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car sonata = new Car(null, "Sonata", hyundai, null);
        carService.create(sonata);
    }
}
