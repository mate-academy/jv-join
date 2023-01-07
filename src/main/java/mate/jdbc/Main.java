package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        //create method test (fully working (drivers & manufacturers))
        Car car = new Car();
        car.setModel("Matiz");
        car.setManufacturer(manufacturerService.get(4L));
        car.setDrivers(List.of(driverService.get(1L),
                driverService.get(4L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        //get 'matiz' method test (worked)
        System.out.println(carService.get(3L));
        //get all method test (worked)
        System.out.println(carService.getAll());
        //update method (upgrade to Rolls-Royce & new manufacturer
        // (we will check the driver list field
        // in the addDriverToCar and removeDriverFromCar methods)) (worked)
        car = carService.get(3L);
        car.setModel("Rolls-Royce");
        car.setManufacturer(manufacturerService.get(1L));
        System.out.println(carService.update(car));
        //delete method test (worked)
        System.out.println(carService.delete(2L));
        //add driver method test (worked)
        carService.addDriverToCar(driverService.get(5L), carService.get(3L));
        //remove driver method test (worked)
        carService.removeDriverFromCar(driverService.get(5L), carService.get(3L));
        //get cars by driver method test (worked)
        System.out.println(carService.getAllByDriver(1L));
    }
}
