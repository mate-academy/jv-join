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
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Driver> drivers = driverService.getAll();
        Manufacturer manufacturerToyota = manufacturerService.get(1L);
        Car newCar = new Car(0L, "Land Cruiser 100", manufacturerToyota, drivers);
        newCar = carService.create(newCar);
        System.out.println(newCar);
        System.out.println(carService.getAll());
        Driver newDriver = new Driver(0L, "Adam Smith", "25483213433");
        newDriver = driverService.create(newDriver);
        carService.addDriverToCar(newDriver, newCar);
        System.out.println(carService.get(newCar.getId()));
        System.out.println(carService.getAllByDriver(newDriver.getId()));
        carService.removeDriverFromCar(newDriver, newCar);
        System.out.println(carService.get(newCar.getId()));
        carService.delete(newCar.getId());
        System.out.println(carService.getAll());
    }
}
