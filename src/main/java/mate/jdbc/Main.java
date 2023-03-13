package mate.jdbc;

import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarServic;
import mate.jdbc.service.CarServiceImpl;

public class Main {
    public static void main(String[] args) {
        CarServic carServic = new CarServiceImpl(new CarDaoImpl(), new DriverDaoImpl());
        Car car = carServic.get(1L);
        carServic.getAll().forEach(System.out::println);
        carServic.getAllByDriver(2L).forEach(System.out::println);
    }
}
