package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final long HONDA_MANUFACTURER_ID = 16L;
    private static final long BRIO_DRIVER_ID = 5L;
    private static final long SECOND_BRIO_DRIVER_ID = 6L;
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println(carService.getAll());
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car brio = new Car();
        brio.setModel("Honda Brio");
        brio.setManufacturer(manufacturerService.get(HONDA_MANUFACTURER_ID));
        List<Driver> hondaDrivers = List.of(driverService.get(BRIO_DRIVER_ID));
        brio.setDrivers(hondaDrivers);
        System.out.println(carService.create(brio));
        System.out.println(carService.getAll());
        brio.setModel("Brio");
        carService.addDriverToCar(driverService
                .get(SECOND_BRIO_DRIVER_ID), carService.get(brio.getId()));
        System.out.println(carService.update(brio));
        System.out.println(carService.getAll());
        System.out.println(carService.get(brio.getId()));
        carService.removeDriverFromCar(driverService
                .get(SECOND_BRIO_DRIVER_ID), carService.get(brio.getId()));
        System.out.println(carService.delete(brio.getId()));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(SECOND_BRIO_DRIVER_ID));
    }
}
