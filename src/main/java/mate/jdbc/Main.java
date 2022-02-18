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
        CarService carService = (CarService) injector.getInstance(CarService.class);

        checkCreateNewCarMethod(carService);

        getCarById(carService, 1L);

        getAllCars(carService);

        updateCar(carService);

        deleteCarById(carService, 4L);

        getAllCarsByDriver(carService, 3L);

        addNewDriverToCar(carService);

        removeDriverFromCar(carService);
    }

    private static void checkCreateNewCarMethod(CarService carService) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(2L);

        Driver driver1 = new Driver();
        driver1.setId(1L);
        Driver driver2 = new Driver();
        driver2.setId(2L);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        Car car = new Car("Tavria", manufacturer, drivers);
        Car createdCar = carService.create(car);
    }

    private static void getCarById(CarService carService, Long id) {
        Car car = carService.get(id);
        System.out.println(car);
    }

    private static void getAllCars(CarService carService) {
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
    }

    private static void updateCar(CarService carService) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);

        Driver driver1 = new Driver();
        driver1.setId(3L);
        Driver driver2 = new Driver();
        driver2.setId(4L);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        Car car = new Car();
        car.setId(1L);
        car.setModel("Kopeika");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);

        System.out.println(carService.update(car));
    }

    private static void deleteCarById(CarService carService, Long id) {
        System.out.println(carService.delete(id));
    }

    private static void getAllCarsByDriver(CarService carService, Long driverId) {
        carService.getAllByDriver(driverId).forEach(System.out::println);
    }

    private static void addNewDriverToCar(CarService carService) {
        Car car = new Car();
        car.setId(3L);
        Driver driver = new Driver();
        driver.setId(4L);
        carService.addDriverToCar(driver, car);
    }

    private static void removeDriverFromCar(CarService carService) {
        Car car = new Car();
        car.setId(1L);
        Driver driver = new Driver();
        driver.setId(3L);
        carService.removeDriverFromCar(driver, car);
    }
}
