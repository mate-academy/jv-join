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
        Manufacturer manufacturerBmw = new Manufacturer("Honda", "Japan");
        Manufacturer manufacturerToyota = new Manufacturer("Infinity", "Japan");
        manufacturerService.create(manufacturerBmw);
        manufacturerService.create(manufacturerToyota);
        System.out.println(manufacturerBmw.getId());

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverSarah = new Driver("Sarah", "80-900-444-55");
        Driver driverGeorge = new Driver("George", "03-424-399-441");
        driverService.create(driverGeorge);
        driverService.create(driverSarah);

        Car carHonda = new Car("Civic", manufacturerBmw);
        Car carInfinity = new Car("Q50", manufacturerToyota);
        carHonda.setDrivers(new ArrayList<>());
        carInfinity.setDrivers(new ArrayList<>());

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carHonda);
        carService.create(carInfinity);
        carService.addDriverToCar(driverGeorge, carHonda);
        carService.addDriverToCar(driverSarah, carHonda);

        System.out.println("Get: ");
        System.out.println(driverService.get(driverGeorge.getId()));
        System.out.println("GetAll: ");
        carService.getAll().forEach(System.out::println);
        carInfinity.setModel("Q90");
        carService.update(carInfinity);
        carService.delete(carInfinity.getId());
        carService.removeDriverFromCar(driverSarah, carHonda);

        System.out.println("GetAllByDriver: ");
        carService.getAllByDriver(driverGeorge.getId()).forEach(System.out::println);
    }
}
