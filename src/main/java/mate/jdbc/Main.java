package mate.jdbc;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("Chev");
        car.setId(1L);

        car.setDrivers(List.of());

        carService.update(car);
//        carService.create(car);
//        System.out.println(carService.delete(1L));

        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getModel()));
        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getManufacturer().getName()));

        System.out.println();

        carService.getAll().forEach(c -> System.out.println(c.getModel()));

        System.out.println();

        System.out.println(carService.get(2L).get().getManufacturer().getName());
    }
}
