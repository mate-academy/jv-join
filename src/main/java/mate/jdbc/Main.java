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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Car cullinan = new Car();
        cullinan.setModel("Cullinan");
        cullinan.setManufacturer(manufacturerService.get(3L));
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(6L));
        drivers.add(driverService.get(7L));
        cullinan.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("----Create car------");
        System.out.println(carService.create(cullinan));
        Car updateCullinan = carService.get(4L);
        updateCullinan.setModel("Cullinan gts");
        List<Driver> driversForCullinan = new ArrayList<>();
        driversForCullinan.add(driverService.get(7L));
        updateCullinan.setDrivers(driversForCullinan);
        System.out.println("-----update cullinan----");
        System.out.println(carService.update(updateCullinan));
        carService.delete(3L);
        carService.addDriverToCar(driverService.get(7L), carService.get(1L));
        carService.removeDriverFromCar(driverService.get(7L), carService.get(1L));
        System.out.println("------get all cars------");
        System.out.println(carService.getAll());
        System.out.println("-----get all cars by driver-----");
        System.out.println(carService.getAllByDriver(2L));
        System.out.println("------getCar(by id)------");
        System.out.println(carService.get(1L));
    }
}
