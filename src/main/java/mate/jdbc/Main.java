package mate.jdbc;

import java.util.ArrayList;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver bestDriver = new Driver("Diesel", "DF43521");
        Driver worstDriver = new Driver("Shrek", "DК44321");
        driverService.create(bestDriver);
        driverService.create(worstDriver);
        ArrayList listOfDrivers = new ArrayList();
        listOfDrivers.add(bestDriver);
        listOfDrivers.add(worstDriver);
        Manufacturer toyotaManufacturer = manufacturerService.get(4L);
        Car specialCar = new Car("Hilux", toyotaManufacturer, listOfDrivers);
        carService.create(specialCar);
    }
}
