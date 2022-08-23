package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector.getInstance(ManufacturerDao.class);
//        Car car = new Car();
//        car.setModel("Megane");
//        car.setId(5L);
//        car.setManufacturer(manufacturerDao.get(3L).get());
//        carService.update(car);
//        car.setManufacturer(manufacturerDao.get(1L).get());
//        carDao.create(car);
//        System.out.println(carService.get(2L));
        carService.getAll().forEach(System.out::println);
    }
}
