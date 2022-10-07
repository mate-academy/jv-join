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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(4L));
        Manufacturer manufacturer = manufacturerService.get(2L);
        Driver driver = driverService.get(2L);
        Car car = new Car(6L,"A55",manufacturer,List.of(driver));
        carService.create(car);
        carService.update(new Car(4L,"A55",manufacturer,List.of(driver)));
        carService.delete(5L);
        carService.addDriverToCar(driver,car);
        carService.removeDriverFromCar(driver,car);
    }
}
