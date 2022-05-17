package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerBmw = new Manufacturer("BMW", "USA");
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerBmw);
        manufacturerService.create(manufacturerToyota);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverDaniel = new Driver("Daniel", "142623");
        Driver driverBob = new Driver("Bob", "623879");
        driverService.create(driverBob);
        driverService.create(driverDaniel);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carBmw = new Car("pl12567", manufacturerBmw);
        Car carToyota = new Car("ct51667", manufacturerToyota);
        carBmw.setDrivers(new ArrayList<>());
        carToyota.setDrivers(new ArrayList<>());
        carService.create(carBmw);
        carService.create(carToyota);

        carService.addDriverToCar(driverBob, carBmw);
        carService.addDriverToCar(driverDaniel, carBmw);
        System.out.println("Get: ");
        System.out.println(driverService.get(driverBob.getId()));
        System.out.println("GetAll: ");
        carService.getAll().forEach(System.out::println);
        carToyota.setModel("xx33355");
        carService.update(carToyota);
        carService.delete(carToyota.getId());
        carService.removeDriverFromCar(driverDaniel,carBmw);
        System.out.println("GetAllByDriver: ");
        carService.getAllByDriver(driverBob.getId()).forEach(System.out::println);
    }
}
