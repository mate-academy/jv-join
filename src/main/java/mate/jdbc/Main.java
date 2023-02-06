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
        Driver polina = new Driver(0L, "Polina", "69265064");
        Driver roman = new Driver(0L, "Roman", "43961965");
        Driver olena = new Driver(0L, "Olena", "74308735");
        List<Driver> drivers = List.of(polina, roman, olena);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);
        System.out.println(driverService.getAll());
        Manufacturer volvo = new Manufacturer(0L, "Volvo Cars", "Sweden");
        Manufacturer mini = new Manufacturer(0L, "Mini", "United Kingdom");
        Manufacturer skoda = new Manufacturer(0L, "Skoda", "Czech Republic");
        List<Manufacturer> manufacturers = List.of(volvo, mini, skoda);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturers.forEach(manufacturerService::create);
        System.out.println(manufacturerService.getAll());
        List<Car> cars = List.of(
                new Car(0L, "MY23", volvo, List.of(polina)),
                new Car(0L, "Countryman", mini, List.of(roman)),
                new Car(0L, "Octavia", skoda, drivers)
        );
        CarService carService = (CarService) injector.getInstance(CarService.class);
        cars.forEach(carService::create);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(1L));
        carService.addDriverToCar(polina, carService.get(2L));
        carService.removeDriverFromCar(polina, carService.get(3L));
        carService.delete(1L);
        System.out.println(carService.getAll());
    }
}
