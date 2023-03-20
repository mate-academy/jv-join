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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJohn = new Driver("John", "12345");
        Driver driverJimmy = new Driver("Jimmy", "67890");
        Driver driverJack = new Driver("Jack", "100100");
        driverService.create(driverJohn);
        driverService.create(driverJimmy);
        driverService.create(driverJack);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverJohn);
        drivers.add(driverJimmy);

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(mercedes);
        manufacturerService.create(ford);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car mercedesCar = new Car("GLS", mercedes, drivers);
        Car fordCar = new Car("Escape", ford, drivers);
        carService.create(mercedesCar);
        carService.create(fordCar);
        carService.getAll().forEach(System.out::println);

        System.out.println(carService.get(mercedesCar.getId()));
        carService.delete(fordCar.getId());

        System.out.println(carService.getAllByDriver(driverJimmy.getId()));

        carService.removeDriverFromCar(driverJohn, mercedesCar);
        drivers.add(driverJack);
        carService.addDriverToCar(driverJack, mercedesCar);
        mercedesCar.setModel("GLA");
        carService.update(mercedesCar);

    }
}
