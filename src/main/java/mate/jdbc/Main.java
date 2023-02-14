package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> firstShiftDrivers = List.of(driverService.get(1L), driverService.get(3L));
        List<Driver> secondShiftDrivers = List.of(driverService.get(2L), driverService.get(6L));
        List<Driver> thirdShiftDrivers = List.of(driverService.get(5L), driverService.get(7L));
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car logan = new Car(null, "Logan", manufacturerService.get(1L), firstShiftDrivers);
        Car scala = new Car(null, "Scala", manufacturerService.get(2L), secondShiftDrivers);
        Car leaf = new Car(null, "Leaf", manufacturerService.get(3L), thirdShiftDrivers);
        carService.create(logan);
        carService.create(scala);
        carService.create(leaf);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(5L));
        Car elantra = new Car(leaf.getId(), "Elantra",
                manufacturerService.get(4L), thirdShiftDrivers);
        carService.update(elantra);
        System.out.println(carService.get(elantra.getId()));
        carService.removeDriverFromCar(driverService.get(6L), carService.get(scala.getId()));
        System.out.println(carService.get(scala.getId()));
        carService.addDriverToCar(driverService.get(6L), carService.get(elantra.getId()));
        System.out.println(carService.get(elantra.getId()));
    }
}
