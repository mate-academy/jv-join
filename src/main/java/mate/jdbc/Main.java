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
        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver driver = new Driver(null, "Oleksandr", "5555");
        Driver driver1 = new Driver(null, "Oleksandr1", "55551");
        Driver driver2 = new Driver(null, "Oleksandr2", "55552");
        Driver driver3 = new Driver(null, "Oleksandr3", "55553");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver1);
        drivers.add(driver2);
        drivers.add(driver3);
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driver);
        drivers2.add(driver2);
        drivers.forEach(driverService::create);
        drivers2.forEach(driverService::create);
        Manufacturer manufacturer = new Manufacturer(null, "Mercedes", "Germany");
        Manufacturer manufacturer2 = new Manufacturer(null, "BMW", "Germany");
        manufacturerService.create(manufacturer);
        manufacturerService.create(manufacturer2);
        Car car = new Car(null, "w140", manufacturer, drivers);
        Car car1 = new Car(null, "e36", manufacturer2, drivers2);
        Car car2 = new Car(car1.getId(), "e36", manufacturer, drivers2);
        carService.create(car);
        carService.create(car1);
        carService.create(car2);
        System.out.println(carService.get(car.getId()));
        System.out.println(carService.getAllByDriver(driver2.getId()));

        Car update = carService.update(car2);
        System.out.println(update);
        System.out.println(carService.get(car2.getId()));
        System.out.println(carService.getAll());

        carService.delete(car2.getId());
        System.out.println(carService.getAll());

        List<Car> allCarsByDriver2 = carService.getAllByDriver(driver2.getId());
        System.out.println(allCarsByDriver2);

        carService.addDriverToCar(driver3, car);
        System.out.println(carService.get(car.getId()));

        carService.removeDriverFromCar(driver3, car);
        System.out.println(carService.get(car.getId()));
    }
}
