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
        Driver vova = new Driver("Vova", "1234");
        Driver bob = new Driver("Bob", "5678");
        Driver anton = new Driver("Anton", "2222");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(vova);
        driverService.create(bob);
        driverService.create(anton);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(vova.getId()));
        drivers.add(driverService.get(bob.getId()));
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(audi);
        Car car = new Car();
        car.setDrivers(drivers);
        car.setManufacturer(audi);
        car.setModel("A5");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(car.getId()));
        carService.addDriverToCar(anton, car);
        carService.removeDriverFromCar(bob, car);
        car.setModel("A6");
        carService.update(car);
        System.out.println(car);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(vova.getId()));
        System.out.println(carService.delete(car.getId()));
    }
}
