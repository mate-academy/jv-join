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
    private static final Injector injector;
    private static final CarService carService;
    private static final ManufacturerService manufacturerService;
    private static final DriverService driverSevice;

    static {
        injector = Injector.getInstance("mate.jdbc");
        carService = (CarService) injector.getInstance(CarService.class);
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        driverSevice = (DriverService) injector.getInstance(DriverService.class);
    }

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer("Tesla", "USA");
        manufacturer.setId(11L);
        Car car = new Car("Model X", manufacturer);
        carService.create(car);
        car = new Car(2L, "Vectra", manufacturerService.get(11L));
        Driver driver1 = driverSevice.get(1L);
        Driver driver2 = driverSevice.get(2L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        car.setDrivers(drivers);
        carService.create(car);
        carService.update(car);
        carService.delete(3L);
        carService.getAllByDriver(2L).forEach(System.out::println);
        carService.addDriverToCar(new Driver(3L,"Sem", "333"), car);
        carService.removeDriverFromCar(new Driver(3L,"Sem", "333"), car);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(1L));
    }
}
