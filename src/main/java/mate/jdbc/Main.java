package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        carService.getAll().forEach(System.out::println);

        Car car = carService.get(1L);
        System.out.println(car.getDrivers());

        Driver driver = driverService.get(3L);
        System.out.println(carService.getAllByDriver(driver.getId()));

        Driver driver1 = driverService.get(1L);
        Car car1 = carService.get(2L);
        System.out.println(car1);

        System.out.println(car1.getDrivers());
        carService.addDriverToCar(driver1, car1);
        System.out.println(car1.getDrivers());
        carService.removeDriverFromCar(driver1, car1);
        System.out.println(car1.getDrivers());

        car1.setModel("Pasta");
        Car updatedCar = carService.update(car1);
        System.out.println(carService.get(updatedCar.getId()));

        carService.delete(car1.getId());
        carService.getAll().forEach(System.out::println);
    }
}
