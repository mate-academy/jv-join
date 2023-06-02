package mate.jdbc;

import java.util.List;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        // CarService create(Car car) test
        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        Car car = new Car("Cybertruck", manufacturerDao.get(2L).get());
        Car car1 = carService.create(car);
        System.out.println(car1);

        // CarService get(Long id) test
        Car car2 = carService.get(2L);
        System.out.println(car2);

        //CarService getAll() test
        List<Car> allCars = carService.getAll();
        allCars.stream().forEach(System.out::println);

        //CarService update(Car car) test
        Car car3 = new Car(8L, "Model S", manufacturerDao.get(2L).get());
        car3 = carService.update(car3);
        System.out.println("Car after update " + car3);

        //CarService boolean delete(Long id) test
        boolean isDelete = carService.delete(9L);
        System.out.println(isDelete);

        //CacService addDriverToCar(Driver driver, Car car) test
        DriverDao driverDao = new DriverDaoImpl();
        Driver driver = driverDao.get(2L).get();
        carService.addDriverToCar(driver, car3);
        System.out.println(carService.get(car3.getId()));

        //CarService void removeDriverFromCar(Driver driver, Car car)  test
        carService.removeDriverFromCar(driver, car3);
        System.out.println(carService.get(car3.getId()));

        //CarService List<Car> getAllByDriver(Long driverId);
        List<Car> allByDriver = carService.getAllByDriver(1L);
        allByDriver.stream().forEach(System.out::println);
    }
}
