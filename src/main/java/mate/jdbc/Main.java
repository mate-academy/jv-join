package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        System.out.println(carDao.get(2L));
        //System.out.println(manufacturerDao.get(1L));
    }
}
