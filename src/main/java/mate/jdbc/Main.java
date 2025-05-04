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
        Car prado = new Car();
        prado.setModel("Prado");
        prado.setManufacturer(new Manufacturer(2L, "Toyota", "Japan"));
        List<Driver> priusDrivers = new ArrayList<>();
        priusDrivers.add(new Driver(5L, "Viktor", "55555555"));
        prado.setDrivers(priusDrivers);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        carService.create(prado);
        System.out.println(carService.get(prado.getId()));
        System.out.println(carService.getAll());
        prado.setModel("Prado 2.0");
        carService.update(prado);
        Driver aliceDriver = new Driver(6L, "Alice", "22222222");
        carService.addDriverToCar(aliceDriver, prado);
        System.out.println(carService.get(prado.getId()));
        System.out.println(carService.getAllByDriver(aliceDriver.getId()));
        carService.removeDriverFromCar(aliceDriver, prado);
        System.out.println(carService.get(prado.getId()));
        carService.delete(prado.getId());
    }
}
