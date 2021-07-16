package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static CarDao carDao =
            (CarDao) injector.getInstance(CarDao.class);
    private static DriverDao driverDao =
            (DriverDao) injector.getInstance(DriverDao.class);
    private static ManufacturerDao manufacturerDao =
            (ManufacturerDao) injector.getInstance(ManufacturerDao.class);

    private static CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {

        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
