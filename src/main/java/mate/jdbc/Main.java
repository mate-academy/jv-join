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
    private static final Injector injector =
            Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManufacturer = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmwManufacturer);

        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");
        manufacturerService.create(fordManufacturer);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> bmwDrivers = new ArrayList<>();
        Driver sergiyDriver = new Driver("Sergiy", "MK092354");
        driverService.create(sergiyDriver);
        bmwDrivers.add(sergiyDriver);

        Driver vasylDriver = new Driver("Vasyl", "RE143686");
        driverService.create(vasylDriver);
        bmwDrivers.add(vasylDriver);

        List<Driver> fordDrivers = new ArrayList<>();
        Driver alinaDriver = new Driver("Alina", "KL983456");
        driverService.create(alinaDriver);
        fordDrivers.add(alinaDriver);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car bmwCar = new Car("M5", bmwManufacturer, bmwDrivers);
        carService.create(bmwCar);

        Car fordCar = new Car("Focus", fordManufacturer, fordDrivers);
        carService.create(fordCar);

        System.out.println(carService.getAll());

        carService.removeDriverFromCar(vasylDriver, bmwCar);
        System.out.println(carService.get(bmwCar.getId()));

        System.out.println(carService.getAllByDriver(alinaDriver.getId()));

        carService.addDriverToCar(vasylDriver, fordCar);
        System.out.println(carService.get(fordCar.getId()));

        bmwCar.setModel("X6");
        carService.update(bmwCar);
        System.out.println(carService.get(bmwCar.getId()));

        carService.delete(bmwCar.getId());
        System.out.println(carService.getAll());
    }
}
