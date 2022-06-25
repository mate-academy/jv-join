package mate.jdbc;

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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driverList = driverService.getAll();

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(17L);

        Car audi = new Car();
        audi.setModel("A6");
        audi.setDrivers(driverList);
        audi.setManufacturer(manufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(audi);

        System.out.println(carService.getAll());

        manufacturer.setId(18L);
        audi.setManufacturer(manufacturer);
        carService.update(audi);
        System.out.println(carService.get(audi.getId()));
        System.out.println(carService.getAllByDriver(driverList.get(1).getId()));
    }
}
