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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        //test method create
        Car carLada = new Car();
        Manufacturer manufacturerLada = manufacturerService
                .create(new Manufacturer("lada", "Ukraine"));
        Driver driverAlise = driverService.create(new Driver("Max", "1111"));
        Driver driverIra = driverService.create(new Driver("Ira", "2222"));
        List<Driver> drivers = List.of(driverAlise, driverIra);

        carLada.setManufacturer(manufacturerLada);
        carLada.setDriver(drivers);
        carLada.setModel("lada1000");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carLada);

        System.out.println("test method get() and create()");
        System.out.println(carService.get(carLada.getId()));

        System.out.println("test method getAll()");
        carService.getAll().forEach(System.out::println);

        System.out.println("test method update() and addDriverToCar()");
        Driver driverBob = driverService.create(new Driver("Bob", "3333"));
        carService.addDriverToCar(driverBob, carLada);
        carLada.setModel("newModelLada");
        System.out.println(carService.update(carLada));

        System.out.println("test method removeDriverFromCar");
        carService.removeDriverFromCar(driverAlise, carLada);
        carService.getAll().forEach(System.out::println);

        System.out.println("test method getAllCarByDriver()");
        List<Car> allCarByDriver = carService.getAllCarByDriver(driverBob.getId());
        allCarByDriver.forEach(System.out::println);

        System.out.println("test method delete()");
        carService.delete(carLada.getId());
        carService.getAll().forEach(System.out::println);
    }
}

