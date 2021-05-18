package mate.jdbc;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector
                .getInstance(ManufacturerDao.class);
        Car car = new Car("coolBMW", manufacturerDao.get(2L).get());
        DriverDao driverDao = (DriverDao) injector.getInstance(DriverDao.class);
        car.setDrivers(driverDao.getAll());
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        System.out.println(carService.get(9L));

        System.out.println(carService.delete(9L));

        System.out.println(carService.getAll());

        Car updatedCar = new Car("notCoolAudi", manufacturerDao.get(1L).get());
        updatedCar.setId(9L);
        System.out.println(carService.update(updatedCar));

        System.out.println(carService.getAllByDriver(3L));
    }
}
