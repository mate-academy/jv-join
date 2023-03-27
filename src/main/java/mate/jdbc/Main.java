package mate.jdbc;

import java.util.List;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.impl.DriverDaoImpl;
import mate.jdbc.dao.impl.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        ManufacturerDao manufacturer = new ManufacturerDaoImpl();
        Car car = new Car("Model X", manufacturer.get(1L).get(),
                List.of(new Driver(1L, "Artem", "123456"),
                        new Driver(2L, "Bob", "654321")));
        Car carCreate = carService.create(car);
        System.out.println("Create cars: " + carCreate);
        Car carGet = carService.get(3L);
        System.out.println("Get car: " + carGet);
        System.out.println("Get all cars:");
        List<Car> allCars = carService.getAll();
        allCars.stream()
                .forEach(System.out::println);
        System.out.println("Get all cars by driver:");
        List<Car> allCarsByDriver = carService.getAllByDriver(1L);
        allCarsByDriver.stream()
                .forEach(System.out::println);
        Car carUpdate = new Car(7L, "Reno", manufacturer.get(10L).get());
        carUpdate = carService.update(carUpdate);
        System.out.println("Car after update " + carUpdate);
        boolean isDeleteCar = carService.delete(8L);
        System.out.println(isDeleteCar);
        DriverDao driverDao = new DriverDaoImpl();
        Driver driver = driverDao.get(2L).get();
        carService.addDriverToCar(driver, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
        carService.removeDriverFromCar(driver, carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
    }
}
