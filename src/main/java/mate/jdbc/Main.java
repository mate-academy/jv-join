package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("-----------------create-------------------");
        List<Driver> list = new ArrayList<>();
        list.add(driverService.get(1L));
        Car car = new Car(null, "m8", manufacturerService.get(2L), list);
        System.out.println(carService.create(car) + System.lineSeparator());

        System.out.println("----------------get---------------------");
        System.out.println(carService.get(car.getId()) + System.lineSeparator());

        System.out.println("------------------getAll-------------------");
        carService.getAll().stream()
                .forEach(System.out::println);
        System.out.println("");

        System.out.println("------------------update---------------------");
        car.setModel("x7");
        System.out.println(carService.update(car) + System.lineSeparator());

        System.out.println("----------------addDriverToCar-----------------");
        Driver driver = new Driver(null, "Anton", "1234");
        driverService.create(driver);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()) + System.lineSeparator());

        System.out.println("---------------getAllByDriver-------------------");
        System.out.println(carService.getAllByDriver(driver.getId()) + System.lineSeparator());

        System.out.println("---------------removeDriverFromCar--------------");
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()) + System.lineSeparator());

        System.out.println("----------------delete----------------------");
        carService.delete(car.getId());
        carService.getAll().stream()
                .forEach(System.out::println);
    }
}
