package mate.jdbc;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.impl.DriverDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        DriverDao driverDao = new DriverDaoImpl();

        Driver driverOne = new Driver();
        driverOne.setId(11L);
        driverOne.setLicenseNumber("334");
        driverOne.setName("One");

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = carService.get(9L);
        System.out.println(carService.get(6L));
    }
}
