package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Injector;

public class Main {
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        CarDao carDao = (CarDao) INJECTOR.getInstance(CarDao.class);
      //  System.out.println(carDao.get(1l));
        carDao.getAll().forEach(System.out::println);

    }
}
