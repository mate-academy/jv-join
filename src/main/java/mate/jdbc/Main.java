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
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
//        Car car = new Car();
//        car.setModel("Megane");
//        car.setManufacturer(manufacturerDao.get(1L).get());
//        carDao.create(car);
        System.out.println(carDao.get(5L));
    }
}
