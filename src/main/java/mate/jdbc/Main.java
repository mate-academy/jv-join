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
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Manufacturer bmwManufacturer = new Manufacturer(2L, "BMW", "GERMANY");
        Driver driverPeter = new Driver(7L, "Peter", "1234567");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverPeter);
        Car bmw525 = new Car(4L, bmwManufacturer, "525", drivers);
        System.out.println("Create car with model=525, manufacturer=BMW, driver=Peter: "
                + carService.create(bmw525));

        System.out.println("Get car bmw525: " + carService
                .get(bmw525.getId()));;
        System.out.println("Get all cars: " + carService.getAll());;
        Driver driverMark = new Driver(9L, "Mark", "1234567");
        Driver driverBob = new Driver(10L, "Bob", "98765432");
        drivers.add(driverMark);
        drivers.add(driverBob);
        bmw525.setDriver(drivers);
        bmw525.setModel("535");
        System.out.println("Update car with driver=Mark, model=535: "
                + carService.update(bmw525));;
        System.out.println("Delete car bmw525: "
                + carService.delete(bmw525.getId()));
        List<Car> cars = new ArrayList<>();
        cars = carService.getAllByDriver(driverMark.getId());
        System.out.println("Get all cars for driver driverMark = " + cars);

    }
}
