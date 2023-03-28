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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        //Manufacturers
        Manufacturer manufacturerFerrari = new Manufacturer("Ferrari", "Italy");
        Manufacturer manufacturerMercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer manufacturerCarUpdateTest = new Manufacturer("BMW", "Germany");
        manufacturerService.create(manufacturerFerrari);
        manufacturerService.create(manufacturerMercedes);
        manufacturerService.create(manufacturerCarUpdateTest);
        //Manufacturers getAll
        System.out.println(manufacturerService.getAll());
        //Manufacturers get
        System.out.println(manufacturerService.get(manufacturerFerrari.getId()));
        System.out.println(manufacturerService.get(manufacturerMercedes.getId()));
        //Manufacturers update
        manufacturerFerrari.setName("Lamborghini");
        manufacturerService.update(manufacturerFerrari);
        System.out.println(manufacturerService.getAll());
        //Drivers
        Driver driverBob = new Driver("Bob","56755");
        Driver driverJohn = new Driver("John", "890098");
        Driver driverRon = new Driver("Ron", "1234554321");
        driverService.create(driverBob);
        driverService.create(driverJohn);
        driverService.create(driverRon);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverBob);
        driverList.add(driverJohn);
        driverList.add(driverRon);
        //Drivers getAll
        System.out.println(driverService.getAll());
        //Drivers get
        System.out.println(driverService.get(driverBob.getId()));
        System.out.println(driverService.get(driverJohn.getId()));
        //Drivers update
        driverBob.setName("Chris");
        driverService.update(driverBob);
        System.out.println(manufacturerService.getAll());
        //Cars
        Car carFerrari = new Car("GT5", manufacturerFerrari, List.of(driverBob, driverRon));
        Car carMercedes = new Car("Benz", manufacturerMercedes, List.of(driverJohn));
        carService.create(carFerrari);
        carService.create(carMercedes);
        //Cars getAll
        System.out.println(carService.getAll());
        //Cars get
        System.out.println(carService.get(carMercedes.getId()));
        System.out.println(carService.get(carFerrari.getId()));
        //Cars getAllByDriver
        System.out.println(carService.getAllByDriver(driverJohn.getId()));
        //Cars update
        carFerrari.setManufacturer(manufacturerCarUpdateTest);
        carService.update(carFerrari);
        carService.delete(carMercedes.getId());
        System.out.println(carService.getAll());
    }
}
