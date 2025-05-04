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

    public static void main(String[] args) {
        Car golf = new Car();
        golf.setModel("Golf");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        golf.setManufacturer(manufacturerService.get(3L));
        golf.setDrivers(new ArrayList<>());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(golf);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        carService.addDriverToCar(driverService.get(2L), carService.get(18L));
        carService.addDriverToCar(driverService.get(5L), carService.get(18L));
        System.out.println(carService.get(16L));
        carService.removeDriverFromCar(driverService.get(2L), carService.get(6L));
        System.out.println(carService.getAllByDriver(2L));
        carService.delete(16L);
        Car vectra = new Car();
        vectra.setModel("Vectra");
        vectra.setManufacturer(manufacturerService.get(9L));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(5L));
        vectra.setDrivers(drivers);
        vectra.setId(17L);
        carService.update(vectra);
        System.out.println(carService.getAll());

    }
}
