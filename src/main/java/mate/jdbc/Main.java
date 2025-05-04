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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("Ford", "USA");
        System.out.println(manufacturerService.create(manufacturer));
        Driver jack = new Driver("Jack", "112109");
        System.out.println(driverService.create(jack));
        Car car = new Car();
        car.setModel("Focus");
        car.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(jack);
        car.setDriverList(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(car.getId()));
        car.setModel("Mustang");
        System.out.println(carService.update(car));
        System.out.println(carService.get(car.getId()));
        for (Car carToIter : carService.getAll()) {
            System.out.println(carToIter);
        }
        for (Car carToIter : carService.getAllByDriver(jack.getId())) {
            System.out.println(carToIter);
        }
        Driver mary = new Driver("Mary", "121122");
        driverService.create(mary);
        carService.addDriverToCar(mary, car);
        carService.removeDriverFromCar(jack, car);
        System.out.println(car);
    }
}
