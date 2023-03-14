package mate.jdbc;

import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarServic;
import mate.jdbc.service.CarServiceImpl;

public class Main {
    public static void main(String[] args) {
        Driver driver = new Driver("Jon", "332");
        Car car = new Car();
        CarServic createCar = new CarServiceImpl(new CarDaoImpl());
        createCar.create(car);
        CarServic addTwoCar = new CarServiceImpl(new CarDaoImpl());
        addTwoCar.addDriverToCar(driver, addTwoCar.get(1L));
        addTwoCar.addDriverToCar(driver, addTwoCar.get(2L));
        CarServic carPrint = new CarServiceImpl(new CarDaoImpl());
        carPrint.getAll().forEach(System.out::println);
        CarServic carServic = new CarServiceImpl(new CarDaoImpl());
        carServic.delete(1L);
        carPrint.getAll().forEach(System.out::println);
    }
}
