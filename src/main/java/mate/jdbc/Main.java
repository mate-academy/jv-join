package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        DriverDao driverDao = (DriverDao) injector.getInstance(DriverDao.class);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverDao.get(2L).get());
        drivers.add(new DriverDaoImpl().get(3L).get());

        Car car = new Car();
        car.setId(31L);
        car.setModel("PORSCHE");
        car.setManufacturer(new ManufacturerDaoImpl().get(1L).get());
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(car);
        carService.get(1L);
        carService.getAll();
        carService.getAllByDriver(2L);
        carService.update(car);
        carService.delete(31L);
        carService.addDriverToCar(driverDao.get(2L).get(), carService.get(3L));
        carService.removeDriverFromCar(driverDao.get(2L).get(), carService.get(6L));
        carService.get(5L);
    }
}
