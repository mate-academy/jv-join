package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.model.Car;

public class Main {
    public static void main(String[] args) {
        // test your code here
        CarDao carDao = new CarDaoImpl();
        System.out.println(carDao.get(1L));
    }
}
