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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer teslaManufacturer =  manufacturerService.get(1L);
        //manufacturerService.create(teslaManufacturer);
        System.out.println(manufacturerService.getAll());

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        //Driver firstDriver = driverService.create(new Driver("test", "123123"));
        //driverService.create(firstDriver);
        System.out.println(driverService.getAll());

        List<Driver> drivers = new ArrayList<>();
        //drivers.add(driverService.get(1l));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Model X", teslaManufacturer, drivers);
        //carService.create(firstCar);

        System.out.println(carService.getAll());

    }
}
