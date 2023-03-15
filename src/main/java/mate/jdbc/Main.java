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
        CarServic carServic = new CarServiceImpl(new CarDaoImpl());
        carServic.create(car);
        Car car1 = carServic.get(1L);
        carServic.addDriverToCar(driver, car1);
        Car car2 = carServic.get(2L);
        carServic.addDriverToCar(driver, car2);
        CarServic carPrint = new CarServiceImpl(new CarDaoImpl());
        for (Car car3 : carPrint.getAll()) {
            System.out.println(car3);
        }
        carServic.delete(1L);
        for (Car car3 : carPrint.getAll()) {
            System.out.println(car3);
        }
    }
}
