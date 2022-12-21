package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Car mustang = new Car();
        mustang.setModel("Horse");
        mustang.setManufacturer(new Manufacturer(2L, "Jeep", "USA"));
        List<Driver> priusDrivers = new ArrayList<>();
        priusDrivers.add(new Driver(7L, "Simon", "12345J"));
        mustang.setDrivers(priusDrivers);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        carService.create(mustang);
        System.out.println(carService.get(mustang.getId()));
        System.out.println(carService.getAll());
        mustang.setModel("Horse_Super");
        carService.update(mustang);
        Driver bobDriver = new Driver(9L, "Bob", "7890K");
        carService.addDriverToCar(bobDriver, mustang);
        System.out.println(carService.get(mustang.getId()));
        System.out.println(carService.getAllByDriver(bobDriver.getId()));
        carService.removeDriverFromCar(bobDriver, mustang);
        System.out.println(carService.get(mustang.getId()));
        carService.delete(mustang.getId());

    }
}
