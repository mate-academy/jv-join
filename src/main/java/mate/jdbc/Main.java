package mate.jdbc;

import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;

public class Main {

    public static void main(String[] args) {
        // test your code here
        CarService carService = new CarServiceImpl();
        System.out.println("------getAllByDriver(driverId)------");
        System.out.println(carService.getAllByDriver(1L));
        System.out.println("------getAllCar()------");
        System.out.println(carService.getAll());
        System.out.println("------getCar(by id)------");
        System.out.println(carService.get(1L));

    }
}
