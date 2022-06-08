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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverAlex = new Driver("Alex", "1234");
        driverService.create(driverAlex);
        Driver driverPetro = new Driver("Petro", "1235");
        driverService.create(driverPetro);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverAlex);
        drivers.add(driverPetro);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerToyota);

        Car toyotaCar = new Car();
        toyotaCar.setModel("Dragon");
        toyotaCar.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        toyotaCar.setDrivers(drivers);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        //create
        System.out.println(carService.create(toyotaCar));
        //get
        System.out.println("Get");
        System.out.println(carService.get(toyotaCar.getId()));
        //update
        System.out.println("update");
        Car ferrariCar = new Car();
        ferrariCar.setId(toyotaCar.getId());
        ferrariCar.setModel("Tiger");
        ferrariCar.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        ferrariCar.setDrivers(drivers);
        System.out.println(carService.update(ferrariCar));
        //delete
        System.out.println("delete");
        System.out.println(carService.delete(ferrariCar.getId()));
        //get all by driver
        System.out.println("get all by driver");
        System.out.println(carService.getAllByDriver(driverPetro.getId()));
        //add driver
        System.out.println("Add driver");
        carService.addDriverToCar(driverAlex, toyotaCar);
        //remove driver
        System.out.println("Remove driver");
        carService.removeDriverFromCar(driverPetro, toyotaCar);
        //get all
        System.out.println("get all");
        carService.getAll().forEach(System.out::println);
    }
}
