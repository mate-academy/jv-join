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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);

        Car honda = new Car();
        honda.setModel("RCV");

        honda.setManufacturer(manufacturerService.get(26L));

        List<Driver> driverList = new ArrayList<>();
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        driverList.add(driverService.get(2L));
        honda.setDrivers(driverList);

        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        Car car = carService.create(honda);

        carService.addDriverToCar(driverService.get(3L), car);
        carService.removeDriverFromCar(driverService.get(3L), car);

        Car car2 = carService.get(2L);
        carService.addDriverToCar(driverService.get(3L), car2);
        carService.removeDriverFromCar(driverService.get(3L), car2);

        carService.getAllByDriver(3L);
    }
}
