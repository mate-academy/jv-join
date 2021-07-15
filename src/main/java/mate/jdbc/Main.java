package mate.jdbc;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static CarService carService = (CarService) injector.getInstance(CarService.class);
    private static DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static CarDao carDao = (CarDao) injector.getInstance(CarDao.class);
    private static ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(
                    ManufacturerService.class);

    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.setName("ABCD");
        driver.setLicenseNumber("A-B-C-D");
        driverService.create(driver);
        Car car1 = carDao.get(1L);
        carService.addDriverToCar(driver, car1);
        carDao.getAll().forEach(System.out::println);

        Car car2 = carDao.get(1L);
        car2.setModel("MODEL");
        car2.setManufacturer(manufacturerService.get(1L));
        car2.setDrivers(List.of(
                driverService.get(1L),
                driverService.get(2L)
        ));
        carDao.create(car2);
        car2.setModel("modelTest");
        carDao.update(car2);
    }
}
