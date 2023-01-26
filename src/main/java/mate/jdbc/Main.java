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
        Car car = new Car();
        car.setModel("Matiz");
        car.setManufacturer(manufacturerService.get(4L));
        car.setDrivers(List.of(driverService.get(1L),
                driverService.get(4L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(3L));
        System.out.println(carService.getAll());
        car = carService.get(3L);
        car.setModel("Rolls-Royce");
        car.setManufacturer(manufacturerService.get(1L));
        System.out.println(carService.update(car));
        System.out.println(carService.delete(2L));
        carService.addDriverToCar(driverService.get(5L), carService.get(3L));
        carService.removeDriverFromCar(driverService.get(5L), carService.get(3L));
        System.out.println(carService.getAllByDriver(1L));

    }
}
