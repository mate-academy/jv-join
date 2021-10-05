package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

public class Main {
    public static void main(String[] args) {
        ManufacturerService manufacturerService = new ManufacturerServiceImpl();
        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        CarDao carDao = new CarDaoImpl();
        Car car = new Car();
        car.setModel("transporter");
        car.setManufacturer(manufacturerDao.get(33L).get());
        //System.out.println(carDao.get(1L).toString());
        carDao.getAll().forEach(System.out::println);
    }
}
