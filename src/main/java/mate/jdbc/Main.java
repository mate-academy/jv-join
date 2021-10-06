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

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer("Mercedes-Benz", "Germany");
        manufacturerService.create(mercedes);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver veronika = new Driver("Veronika", "THN 123456");
        driverService.create(veronika);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(veronika);
        drivers.add(driverService.get(1L));
        drivers.add(driverService.get(2L));
        drivers.add(driverService.get(5L));
        drivers.add(driverService.get(8L));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car elit = new Car("S500", mercedes);
        elit.setDrivers(drivers);
        carService.create(elit);

        drivers.forEach(System.out::println);
        carService.addDriverToCar(driverService.get(5L), elit);
        carService.removeDriverFromCar(driverService.get(1L), elit);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        List<Car> allByDriver = carService.getAllByDriver(1L);
        allByDriver.forEach(System.out::println);
    }
}
