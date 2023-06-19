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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturer);
        Driver alex = new Driver("Alex", "12356");
        Driver pavel = new Driver("Pavel", "45635");
        Driver anton = new Driver("Anton", "248694");
        Driver sergei = new Driver("Sergei", "245546");

        driverService.create(alex);
        driverService.create(pavel);
        driverService.create(anton);
        driverService.create(sergei);
        System.out.println(driverService.getAll());

        Car car = new Car("Mercedes", manufacturer, List.of(alex, pavel));
        car.setModel("Skoda");

        carService.create(car);
        car = carService.get(car.getId());
        carService.addDriverToCar(anton,car);
        carService.removeDriverFromCar(alex, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(alex.getId()).forEach(System.out::println);
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }
}
