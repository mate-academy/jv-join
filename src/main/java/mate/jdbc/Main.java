package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerDao manufacturerDao =
                (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        Car car = new Car();
        car.setModel("JettaGLI");
        car.setId(1L);
        car.setManufacturer((manufacturerDao.get(3L)).get());

        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        System.out.println(carDao.create(car));
        System.out.println(carDao.get(1L));
        carDao.getAll().forEach(System.out::println);
        System.out.println(carDao.update(car));
        System.out.println(carDao.delete(2L));
        System.out.println(carDao.getAllByDriver(1L));

        Driver driver = new Driver(2L, "Ihor", "QW000");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
    }
}
