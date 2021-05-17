package mate.jdbc;

import mate.jdbc.dao.*;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CarDao carDao = new CarDaoImpl();
        System.out.println(carDao.get(3L));
    }
}
