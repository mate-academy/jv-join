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
        //create manufacturers
        Manufacturer fiat = new Manufacturer("Fiat", "Italy");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(fiat);
        manufacturerService.create(ford);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        //create drivers
        Driver mary = new Driver("Mary", "LN-1234");
        Driver alice = new Driver("Alice", "LN-8778");
        Driver bob = new Driver("Bob", "LN-4690");
        Driver jhon = new Driver("Jhon", "LN-1001");
        driverService.create(mary);
        driverService.create(alice);
        driverService.create(bob);
        driverService.create(jhon);

        List<Driver> firstPair = new ArrayList<>();
        List<Driver> secondPair = new ArrayList<>();
        firstPair.add(mary);
        firstPair.add(bob);
        secondPair.add(alice);
        secondPair.add(jhon);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        //create cars
        Car modelTipo = new Car("Tipo", fiat, firstPair);
        Car modelEdge = new Car("Edge", ford, secondPair);
        carService.create(modelTipo);
        carService.create(modelEdge);
        //add and remove driver from car
        carService.addDriverToCar(jhon, modelTipo);
        carService.removeDriverFromCar(jhon, modelEdge);
        //get car and driver by id
        System.out.println(carService.get(modelEdge.getId()));
        System.out.println(carService.getAllByDriver(alice.getId()));
        //update car
        fiat.setCountry("Poland");
        carService.update(modelTipo);
        //delete car and get all
        carService.delete(modelEdge.getId());
        carService.getAll().forEach(System.out::println);
    }
}
