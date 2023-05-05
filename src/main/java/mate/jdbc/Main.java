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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturerHonda = new Manufacturer();
        manufacturerHonda.setName("Honda");
        manufacturerHonda.setCountry("Japan");

        Manufacturer manufacturerAudi = new Manufacturer();
        manufacturerAudi.setName("Audi");
        manufacturerAudi.setCountry("Germany");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerHonda);
        manufacturerService.create(manufacturerAudi);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> driversForHonda = new ArrayList<>();
        driversForHonda.add(driverService.get(1L));
        driversForHonda.add(driverService.get(2L));

        List<Driver> driversForAudi = new ArrayList<>();
        driversForAudi.add(driverService.get(3L));
        driversForAudi.add(driverService.get(4L));
        driversForAudi.add(driverService.get(5L));

        Car car1 = new Car();
        car1.setModel("Honda");
        car1.setManufacturer(manufacturerService.get(68L));
        car1.setDrivers(driversForHonda);

        Car car2 = new Car();
        car2.setModel("Audi");
        car2.setManufacturer(manufacturerService.get(69L));
        car2.setDrivers(driversForAudi);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(car1);
        carService.create(car2);
        carService.addDriverToCar(driverService.get(5L), carService.get(28L));
        carService.removeDriverFromCar(driverService.get(3L), carService.get(29L));

        Car carFromDb = carService.get(29L);
        carFromDb.setModel("Audi A1");
        System.out.println(carFromDb);
        carService.getAll().forEach(System.out::println);
        carService.delete(3L);
        carService.update(carFromDb);

    }
}
