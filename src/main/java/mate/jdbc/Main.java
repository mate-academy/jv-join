package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");
        manufacturer.setId(4L);
        List<Driver> drivers = new ArrayList<>();
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("tesla!");
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        Car car1 = carService.get(3L);
        System.out.println(car1);

        car1.setModel("BadTesla");
        carService.update(car1);
        System.out.println(carService.get(car1.getId()));

        System.out.println(carService.delete(2L));

        Driver driver = new Driver();
        driver.setName("Jane");
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));

        List<Car> allByDriver = carService.getAllByDriver(3L);
        System.out.println(allByDriver);
        carService.getAll().forEach(System.out::println);
    }
}
