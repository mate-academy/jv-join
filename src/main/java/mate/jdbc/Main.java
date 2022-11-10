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
        Manufacturer manufacturer = new Manufacturer("FinalName", "FinalCountry");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Driver1", "231final1");
        Driver driver2 = new Driver("Driver2", "231final2");
        driverService.create(driver1);
        driverService.create(driver2);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("FinalModel", manufacturer, drivers);
        carService.create(car);
        carService.getAllByDriver(driver1.getId()).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
        car.setModel("NewFinalModel");
        System.out.println(carService.update(car));
        carService.removeDriverFromCar(driver1, car);
        System.out.println(carService.get(car.getId()));
        carService.addDriverToCar(driver1, car);
        System.out.println(carService.get(car.getId()));
        carService.delete(car.getId());
    }
}
