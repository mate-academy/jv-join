package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Car car = new Car();
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        car.setModel("jakarta");
        car.setManufacturer(manufacturerService.get(1L));
        Driver driver = driverService.get(2L);
        carService.addDriverToCar(driver, car);
        car.setId(5L);
        carService.update(car);
        carService.delete(13L);
        car = carService.get(11L);
        System.out.println(car);
        System.out.println(carService.getAllByDriver(3L));
        carService.getAll().stream().forEach(System.out::println);

    }
}
