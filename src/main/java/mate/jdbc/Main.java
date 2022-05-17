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
        // test your code here
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(new Driver("Sam", "BD-1154"));
        driverService.create(new Driver("Alise", "HT-8655"));
        driverService.create(new Driver("Anna", "VF-0909"));
        List<Driver> drivers = new ArrayList<Driver>();
        drivers.add(driverService.get(1L));
        drivers.add(driverService.get(2L));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(new Manufacturer("Toyota", "Japan"));
        manufacturerService.create(new Manufacturer("Renault", "France"));

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("Camry", manufacturerService.get(1L), drivers));
        carService.create(new Car("Clio", manufacturerService.get(2L), drivers));

        System.out.println(carService.get(carService.get(1L).getId()));//GET CAR BY ID
        carService.addDriverToCar(drivers.get(0), carService.get(1L));//ADD DRIVER-CAR
        System.out.println(carService.getAllByDriver(carService.get(2L).getId()));//GET ALL CARS
        carService.delete(carService.get(2L).getId());//MARK CAR AS DELETED
    }
}
