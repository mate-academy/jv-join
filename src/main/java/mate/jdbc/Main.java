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
        Car car1 = addTwoCar.get(1L);
        addTwoCar.addDriverToCar(driver, car1);
        Car car2 = addTwoCar.get(2L);
        addTwoCar.addDriverToCar(driver, car2);
        CarServic carPrint = new CarServiceImpl(new CarDaoImpl());
        for (Car car3 : carPrint.getAll()) {
            System.out.println(car3);
        }
        CarServic carServic = new CarServiceImpl(new CarDaoImpl());
        carServic.delete(1L);
        for (Car car3 : carPrint.getAll()) {
            System.out.println(car3);
        }
    }
}
