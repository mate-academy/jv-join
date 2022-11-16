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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService
                .create(new Manufacturer(null, "Ford", "USA"));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService
                .create(new Driver(null, "VLad", "3343JJI"));
        Driver driver1 = driverService
                .create(new Driver(null, "Bohdan", "111111Q"));
        Driver driver2 = driverService
                .create(new Driver(null, "Katerina", "4344F3F"));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver1);
        Car car = new Car(null, "model0", manufacturer,drivers);
        drivers.add(driver2);
        Car car1 = new Car(null, "model1", manufacturer,drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car2 = carService.create(car);
        Car car3 = carService.create(car1);
        Car car4 = carService.get(1L);
        carService.addDriverToCar(driver2, car2);
        Car update = carService.update(car2);
        boolean delete = carService.delete(1L);
        List<Car> all = carService.getAll();
        List<Car> allByDriver = carService.getAllByDriver(1L);
        carService.removeDriverFromCar(driver2, car3);
    }
}
