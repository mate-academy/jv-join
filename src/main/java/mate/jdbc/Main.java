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
        ManufacturerService manufactureService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufactureService.create(bmw);
        Manufacturer porsche = new Manufacturer("Porsche", "Germany");
        manufactureService.create(porsche);
        System.out.println(manufactureService.getAll());
        System.out.println("-----------------------------------------------------");
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "497652");
        driverService.create(bob);
        Driver alice = new Driver("Alice", "845296");
        driverService.create(alice);
        Driver john = new Driver("John", "376028");
        driverService.create(john);
        Driver anna = new Driver("Anna", "589215");
        driverService.create(anna);
        System.out.println(driverService.getAll());
        System.out.println("-----------------------------------------------------");
        List<Driver> firstDriversList = new ArrayList<>();
        firstDriversList.add(bob);
        firstDriversList.add(alice);
        List<Driver> secondDriversList = new ArrayList<>();
        secondDriversList.add(john);
        secondDriversList.add(anna);
        Car bmwCar = new Car("M8 GTR", bmw, firstDriversList);
        Car porscheCar = new Car("Cayman GT4 RS", porsche, secondDriversList);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(bmwCar);
        carService.create(porscheCar);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(bob.getId()));
        carService.removeDriverFromCar(john, porscheCar);
        Driver bill = new Driver("Bill", "127634");
        driverService.create(bill);
        carService.addDriverToCar(bill, porscheCar);
        carService.update(porscheCar);
        System.out.println(carService.getAllByDriver(bill.getId()));
    }
}
