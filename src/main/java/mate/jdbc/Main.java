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
    private static final Injector injector = Injector.getInstance("mate");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver bob = driverService.create(new Driver("bob", "123"));
        Driver john = driverService.create(new Driver("john", "456"));
        Driver richard = driverService.create(new Driver("richard", "789"));
        Manufacturer toyota = manufacturerService.create(new Manufacturer("toyota", "japan"));
        Manufacturer volkswagen = manufacturerService
                .create(new Manufacturer("volkswagen", "germany"));
        Manufacturer peugeot = manufacturerService.create(new Manufacturer("peugeot", "france"));

        Car car1 = carService.create(new Car("toyota corolla", toyota, List.of(bob)));
        Car car2 = carService.create(new Car("volkswagen passat", volkswagen, List.of(bob, john)));
        Car car3 = carService.create(new Car("peugeot 508", peugeot, List.of(john, richard)));

        System.out.println("creating");
        System.out.println(car1);
        System.out.println(car2);
        System.out.println(car3);
        System.out.println(carService.getAll());
        System.out.println("----------------------");
        System.out.println("getting");
        System.out.println(carService.get(car1.getId()));
        System.out.println("----------------------");
        System.out.println("getting all");
        System.out.println(carService.getAll());
        System.out.println("----------------------");
        System.out.println("updating");
        car3.setModel("peugeot 406");
        System.out.println(carService.getAll());
        System.out.println(carService.update(car3));
        System.out.println("----------------------");
        System.out.println("deleting");
        Car car4 = carService.create(new Car("peugeot 108", peugeot, List.of()));
        System.out.println(carService.delete(car4.getId()));
        System.out.println(carService.getAll());
        System.out.println("----------------------");
        System.out.println("adding driver to car");
        carService.addDriverToCar(john, car1);
        System.out.println(carService.getAll());
        System.out.println("----------------------");
        System.out.println("removing driver from car");
        carService.removeDriverFromCar(bob, car1);
        System.out.println(carService.getAll());
        System.out.println("----------------------");
        System.out.println("getting all cars by driver");
        System.out.println(carService.getAllByDriver(john.getId()));
    }
}
