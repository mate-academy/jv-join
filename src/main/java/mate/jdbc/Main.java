package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Manufacturer bmwManufacturer = new Manufacturer(2L, "BMW", "GERMANY");
        Driver driverPeter = new Driver("Peter", "1234567");
        driverService.create(driverPeter);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverPeter);
        Car bmw525 = new Car(bmwManufacturer, "525", drivers);
        System.out.println("Create car with model=525, manufacturer=BMW, driver=Peter: "
                + carService.create(bmw525));

        System.out.println("Get car bmw525: " + carService
                .get(bmw525.getId()));;
        System.out.println("Get all cars: " + carService.getAll());;
        Driver driverMark = new Driver("Mark", "1234567");
        driverService.create(driverMark);
        Driver driverBob = new Driver("Bob", "98765432");
        driverService.create(driverBob);
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
