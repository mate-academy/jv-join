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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final CarService carService = (CarService)
            injector.getInstance(CarService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer bmw = new Manufacturer("bmw", "Germany");
        bmw = manufacturerService.create(bmw);
        Manufacturer mazda = new Manufacturer("mazda", "japan");
        bmw = manufacturerService.create(bmw);
        Car m5 = new Car("m5", bmw);
        Car cx7 = new Car("cx7", bmw);
        System.out.println(carService.create(m5));
        System.out.println(carService.create(cx7));
        Driver ivan = new Driver("Ivan", "iv123");
        System.out.println(driverService.create(ivan));
        Driver pavel = new Driver("Pavel", "pv1234");
        System.out.println(driverService.create(pavel));
        System.out.println(carService.get(1L));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(ivan);
        drivers.add(pavel);
        m5.setDriverList(drivers);
        System.out.println(carService.update(m5));
        carService.addDriverToCar(pavel, m5);
        System.out.println(System.lineSeparator());
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L);
        carService.removeDriverFromCar(pavel, m5);
    }
}
