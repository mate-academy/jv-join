package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        DriverDao driverService = (DriverDao) injector.getInstance(DriverDao.class);
        ManufacturerDao manufacturerService =
                (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer manufacturer = manufacturerService.get(1L).get();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(3L).get());
        drivers.add(driverService.get(7L).get());
        Car car = new Car(1L, "XC90", manufacturer);
        car.setDrivers(drivers);
        System.out.println("**************** CREATE ********************");
        carService.create(car);
        carService.get(car.getId());
        carService.getAll().forEach(System.out::println);
        car.setId(6L);
        car.setModel("XC50");
        drivers.remove(1);
        System.out.println("**************** UPDATE ********************");
        System.out.println(carService.update(car));
        System.out.println(carService.get(15L));
        System.out.println("**************** GET ALL BY DRIVER ********************");
        System.out.println(carService.getAllByDriver(4L));
        System.out.println("**************** DELETE ********************");
        System.out.println(carService.delete(7L));
        Driver driver = driverService.get(4L).get();
        car.setId(8L);
        System.out.println("**************** REMOVE DIRVER FROM CAR ********************");
        carService.removeDriverFromCar(driver, car);
        System.out.println(car);
        System.out.println("**************** ADD DIRVER TO CAR ********************");
        carService.addDriverToCar(driver, car);
        System.out.println(car);
    }
}
