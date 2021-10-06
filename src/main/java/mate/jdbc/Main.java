package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        Car car = new Car();
        DriverDao driverDao = (DriverDao) injector.getInstance(DriverDao.class);
        car.setDrivers(driverDao.getAll());
        ManufacturerDao manufacturerDao =
                (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        car.setManufacturer(manufacturerDao.get(1L).get());
        car.setModel("Benz");
        Car savedCar = carDao.create(car);
        System.out.println(savedCar);
        System.out.println(carDao.getAllByDrivers(1L));
    }
}
