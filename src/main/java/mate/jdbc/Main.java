package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        System.out.println(carDao.getAllByDriver(5L));
        System.out.println(carDao.getAll());
        Manufacturer manufacturer = new Manufacturer("name", "ukraine");
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector
                .getInstance(ManufacturerDao.class);
        manufacturerDao.create(manufacturer);
        Car car = new Car("TestNAME", manufacturer);
        car.setId(3L);
        carDao.update(car);
        carDao.create(new Car("okey", manufacturer));
    }
}
