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

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturer1 = new Manufacturer("Toyota", "Japan");
        manufacturer1.setId(1L);
        manufacturerService.create(manufacturer1);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        Driver driverAli = new Driver(1L, "Ali", "6677");
        Driver driverBibi = new Driver(2L, "Bibi", "5454");
        Driver driverCini = new Driver(3L,"Cini", "7777");
        driverService.create(driverAli);
        driverService.create(driverBibi);
        driverService.create(driverCini);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car car = new Car("Camry", manufacturer1, List.of(driverAli, driverBibi, driverCini));
        car.setId(7L);
        carService.create(car);
        System.out.println(carService.get(6L));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(6L));
        car.setModel("NEW CAMRY 3.5");
        carService.update(car);
        carService.getAll().forEach(System.out::println);

    }
}
