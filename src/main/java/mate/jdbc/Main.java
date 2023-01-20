package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector ingector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) ingector.getInstance(CarService.class);
        ManufacturerService manufacturer = (ManufacturerService) ingector
                .getInstance(ManufacturerService.class);
        DriverService driver = (DriverService) ingector.getInstance(DriverService.class);

        List<Driver> drivers = List.of(driver.get(2L));
        Car car = new Car();
        car.setModel("Focus");
        car.setManufacturer(manufacturer.get(4L));
        car.setDrivers(drivers);
        System.out.println(carService.create(car));

        List<Driver> drivers1 = List.of(driver.get(1L));
        final Car car1 = new Car();
        car1.setId(8L);
        car1.setModel("Focus1");
        car1.setManufacturer(manufacturer.get(3L));
        car1.setDrivers(drivers);
        System.out.println(carService.update(car1));

        System.out.println(carService.get(8L));
        Car car2 = carService.get(8L);

        Driver driver1 = driver.get(1L);
        carService.addDriverToCar(driver1, car2);
        System.out.println(carService.get(8L));
        car2 = carService.get(8L);
        carService.removeDriverFromCar(driver1, car2);
        System.out.println(carService.get(8L));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(4L).forEach(System.out::println);
        carService.delete(3L);
    }
}
