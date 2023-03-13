package mate.jdbc;

import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.DriverDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarServic;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        CarServic carServic = new CarServiceImpl(new CarDaoImpl(), new DriverDaoImpl());
        System.out.println("carServic.get(1L) = " + carServic.get(1L));
        carServic.getAll().forEach(System.out::println);
        carServic.getAllByDriver(2L).forEach(System.out::println);
        System.out.println("carServic.get(1L) = " + carServic.get(1L));
    }
}
