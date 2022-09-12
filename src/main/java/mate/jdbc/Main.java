package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer skoda = new Manufacturer("Skoda", "Czech Republic");
        manufacturerService.create(skoda);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> bmwDrivers = new ArrayList<>();
        Driver oleh = new Driver("Oleh", "123804");
        driverService.create(oleh);
        bmwDrivers.add(oleh);
        Driver nikita = new Driver("Nikita", "578504");
        driverService.create(nikita);
        bmwDrivers.add(nikita);
        List<Driver> skodaDrivers = new ArrayList<>();
        Driver kate = new Driver("Kate", "8654612");
        driverService.create(kate);
        skodaDrivers.add(kate);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car bmwCar = new Car("BMW", bmw, bmwDrivers);
        carService.create(bmwCar);
        Car skodaCar = new Car("Sedan", skoda, skodaDrivers);
        carService.create(skodaCar);
        carService.getAll().forEach(System.out::println);
        System.out.println("-------------");
        carService.addDriverToCar(kate, bmwCar);
        System.out.println(carService.get(bmwCar.getId()));
    }
}
