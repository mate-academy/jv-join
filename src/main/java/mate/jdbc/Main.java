package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector
                .getInstance(CarService.class);

        System.out.println("Get car MethodTest");
        System.out.println(carService.get(1L));
        System.out.println();

        System.out.println("GetAll cars MethodTest");
        carService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("GetAllByDriver MethodTest");
        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println();

        System.out.println("Add AND delete MethodTest");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Car car = new Car();
        car.setModel("Seat");
        car.setManufacturer(manufacturerService.get(31L));
        car.setDrivers(new ArrayList<>());
        car = carService.create(car);
        System.out.println("Added new car: " + car);
        System.out.println("Current car list is:");
        carService.getAll().forEach(System.out::println);
        System.out.println("Try to delete car: " + carService.delete(car.getId()));
        System.out.println("Try to delete car again: " + carService.delete(car.getId()));
        System.out.println("Current car list is:");
        carService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("AddDriverToCar AND update MethodTest");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        System.out.println("Car before add driver" + carService.get(7L));
        driverService.get(5L);
        carService.get(7L);
        carService.addDriverToCar(driverService.get(5L),carService.get(7L));
        System.out.println("Car after add driver " + carService.get(7L));
        System.out.println();

        System.out.println("RemoveDriverFromCar AND update MethodTest");
        carService.removeDriverFromCar(driverService.get(5L),carService.get(7L));
        System.out.println("Car after remove driver " + carService.get(7L));
    }
}
