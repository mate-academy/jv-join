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
        Car bmw = new Car();
        bmw.setModel("BMW");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        bmw.setManufacturer(manufacturerService.get(8L));
        bmw.setDrivers(new ArrayList<>());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(bmw);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        carService.addDriverToCar(driverService.get(3L), carService.get(2L));
        carService.addDriverToCar(driverService.get(9L), carService.get(2L));
        System.out.println(carService.get(1L));
        carService.removeDriverFromCar(driverService.get(4L), carService.get(6L));
        System.out.println(carService.getAllByDriver(3L));
        Car chevrolet = new Car();
        chevrolet.setModel("Chevrolet");
        chevrolet.setManufacturer(manufacturerService.get(9L));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(5L));
        chevrolet.setDrivers(drivers);
        chevrolet.setId(1L);
        carService.update(chevrolet);
        System.out.println(carService.getAll());
    }
}
