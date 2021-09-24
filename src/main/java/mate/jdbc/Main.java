package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        Car crossCountry = new Car();
        crossCountry.setModel("v60 cross country");
        crossCountry.setManufacturer(manufacturerService.get(22L));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(21L));
        drivers.add(driverService.get(23L));
        crossCountry.setDrivers(drivers);
        carService.create(crossCountry);
        System.out.println(carService.getAll());
        Car giulia = carService.get(23L);
        giulia.setModel("Giulia");
        giulia.setManufacturer(manufacturerService.get(24L));
        Driver driver = driverService.get(22L);
        List<Driver> driversGiulia = new ArrayList<>();
        driversGiulia.add(driver);
        giulia.setDrivers(driversGiulia);
        carService.update(giulia);
        carService.delete(23L);
        System.out.println(carService.getAll());
        carService.addDriverToCar(driverService.get(23L), carService.get(19L));
        System.out.println(carService.get(19L));
        carService.removeDriverFromCar(driverService.get(23L),carService.get(19L));
        System.out.println(carService.get(19L));
        List<Car> allByDriver = carService.getAllByDriver(21L);
        System.out.println(allByDriver);
    }
}
