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
    private static final Injector injector
            = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        System.out.println(carService.getAll());
        System.out.println(carService.get(1L));
        Manufacturer manufacturerNissan = new Manufacturer(5L, "Nissan", "Japon");
        manufacturerService.create(manufacturerNissan);
        List<Driver> driverList = new ArrayList<>();
        Driver driverTom = new Driver(4L, "Tom", "SP38917");
        Driver driverSuzana = new Driver(5L, "Suzana", "PR22619");
        driverService.create(driverSuzana);
        driverList.add(driverTom);
        Car car = carService.get(9L);
        driverList = car.getDrivers();
        driverList.add(driverSuzana);
        System.out.println(carService.update(car));
        System.out.println(carService.create(car));
        System.out.println(carService.getAllByDriver(driverTom.getId()));
        System.out.println(carService.delete(car.getId()));
    }
}
