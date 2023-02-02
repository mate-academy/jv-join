package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector
            .getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector
                        .getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector
                        .getInstance(DriverService.class);
        Manufacturer manufacturer = manufacturerService
                .create(new Manufacturer(null, "Toyota", "Japan"));
        final Driver driver = driverService.create(new Driver(null, "Ivan", "AC9952355"));
        Car car = carService.create(new Car("Prius", manufacturer));
        System.out.println("carService methods tests:\nMethod create was called");
        System.out.println("\nMethod \"get\" by id:" + car.getId() + " was called");
        System.out.println(carService.get(car.getId()));
        System.out.println("\nMethod getAll was called:");
        List<Car> cars = carService.getAll();
        for (Car c: cars) {
            System.out.println(c);
        }
        car.setModel("Supra");
        carService.update(car);
        System.out.println("\nMethod \"update\" for car by id:" + car.getId() + " was called");
        System.out.println(carService.get(car.getId()));
        carService.addDriverToCar(driver, car);
        System.out.println("\nMethod \"addDriverToCar\" for car by id:" + car.getId()
                + " and driver by id:" + driver.getId() + " was called");
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(driver, car);
        System.out.println("\nMethod \"removeDriverFromCar\" for car by id:" + car.getId()
                + " and driver by id:" + driver.getId() + " was called");
        System.out.println(carService.get(car.getId()));
        carService.delete(car.getId());
        System.out.println("\nMethod \"delete\" for car by id:" + car.getId() + " was called");
        System.out.println(carService.getAll());
    }
}
