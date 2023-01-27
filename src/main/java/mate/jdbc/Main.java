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
        Manufacturer optiTaxi = new Manufacturer("Opti Taxi", "Ukraine");
        Manufacturer uber = new Manufacturer("Uber", "USA");
        manufacturerService.create(optiTaxi);
        manufacturerService.create(uber);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver yura = new Driver("Yura", "123456");
        Driver otto = new Driver("Otto", "456789");
        Driver hirohito = new Driver("Hirohito", "789123");
        System.out.println(driverService.create(yura));
        System.out.println(driverService.create(otto));
        List<Driver> optiTaxiDrivers = new ArrayList<>();
        optiTaxiDrivers.add(yura);
        List<Driver> uberDrivers = new ArrayList<>();
        uberDrivers.add(otto);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car zaz = new Car("Daewoo Lanos", optiTaxi, optiTaxiDrivers);
        Car mercedes = new Car("Mercedes Benz", uber, uberDrivers);
        //create
        carService.create(zaz);
        carService.create(mercedes);
        //get
        carService.get(zaz.getId());
        //getAll
        carService.getAll().forEach(System.out::println);
        //update
        zaz.setModel("Daewoo Sens");
        System.out.println(carService.update(zaz));
        //delete
        carService.delete(mercedes.getId());
        carService.getAll().forEach(System.out::println);
        //addDriverToCar
        carService.addDriverToCar(otto, zaz);
        //removeDriverFromCar
        carService.removeDriverFromCar(otto, zaz);
        //getAllByDriver
        carService.getAllByDriver(yura.getId()).forEach(System.out::println);
    }
}
