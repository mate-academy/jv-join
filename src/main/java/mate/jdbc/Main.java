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
        Driver driverLeo = new Driver("Leo", "T646");
        driverService.create(driverLeo);
        Driver driverBob = new Driver("Bob", "8754");
        driverService.create(driverBob);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverLeo);
        driverList.add(driverBob);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "China");
        manufacturerService.create(manufacturerToyota);

        Car toyotaCar = new Car();
        toyotaCar.setModel("ToyotaRav4");
        toyotaCar.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        toyotaCar.setDrivers(driverList);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(toyotaCar));
        //get
        System.out.println("Get");
        System.out.println(carService.get(toyotaCar.getId()));
        //update
        System.out.println("update");
        Car volvoCar = new Car();
        volvoCar.setId(toyotaCar.getId());
        volvoCar.setModel("Volvo TT");
        volvoCar.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        volvoCar.setDrivers(driverList);
        System.out.println(carService.update(volvoCar));
        //delete
        System.out.println("delete");
        System.out.println(carService.delete(volvoCar.getId()));
        //get all by driver
        System.out.println("get all cars");
        System.out.println(carService.getAllByDriver(driverBob.getId()));
        //add a driver
        System.out.println("add a driver");
        carService.addDriverToCar(driverBob, toyotaCar);
        //remove a driver
        System.out.println("remove a driver");
        carService.removeDriverFromCar(driverLeo, toyotaCar);
        //get all
        System.out.println("get all");
        carService.getAll().forEach(System.out::println);
    }
}
