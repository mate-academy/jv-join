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
        Manufacturer manufacturer = new Manufacturer(1L,"Mercedes", "Germany");
        List<Driver> drivers = new ArrayList<>();
        Car car = new Car();
        car.setModel("model");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car = carService.create(car);
        Car createdCar = carService.create(car);
        Car car2 = carService.get(createdCar.getId());
        car.setModel("qwerty");
        carService.update(car);
        Driver driver = new Driver(1L, "Mark","xxxx");
        carService.addDriverToCar(driver, car);
        List<Car> carsByDriver = carService.getAllByDriver(1L);
    }
}
