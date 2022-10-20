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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> driversForTesla = new ArrayList<>();
        driversForTesla.add(driverService.get(1L));
        driversForTesla.add(driverService.get(3L));
        Car tesla = new Car("Model 3", manufacturerService.get(1L), driversForTesla);
        carService.create(tesla);
        System.out.println(carService.get(2L));
        carService.update(carService.get(3L));
        carService.delete(4L);
        carService.addDriverToCar(driverService.get(5L), carService.get(5L));
        carService.removeDriverFromCar(driverService.get(5L), carService.get(5L));
        System.out.println(carService.getAllByDriver(3L));
    }
}
