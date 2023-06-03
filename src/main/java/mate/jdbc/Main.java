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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturerAudi = new Manufacturer(null, "Audi", "Germany");
        manufacturerAudi = manufacturerService.create(manufacturerAudi);

        Driver firstDriver = driverService.create(new Driver(1L, "Dmytro", "6392121"));
        Driver secondDriver = driverService.create(new Driver(1L, "Bob", "0963431"));

        Car car = new Car();
        car.setModel("A8");
        car.setManufacturer(manufacturerAudi);
        car.setDrivers(List.of(firstDriver, secondDriver));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("AQ");
        carService.removeDriverFromCar(secondDriver, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
