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
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer honda = new Manufacturer("Honda", "Japan");
        manufacturerService.create(audi);
        manufacturerService.create(honda);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver("John", "AF1325355");
        Driver bob = new Driver("Bob", "JD6645665");
        Driver sem = new Driver("Sem", "FR1355313");
        Driver ben = new Driver("Ben", "JU15655313");
        driverService.create(john);
        driverService.create(bob);
        driverService.create(sem);
        driverService.create(ben);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(john);
        drivers.add(bob);
        drivers.add(sem);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audiA4 = new Car("A4", audi, drivers);
        Car hondaCivic = new Car("Civic", honda, drivers);
        carService.create(audiA4);
        carService.create(hondaCivic);
        System.out.println(carService.get(audiA4.getId()));
        carService.getAll().forEach(System.out::println);
        hondaCivic.setModel("SRV");
        carService.update(hondaCivic);
        System.out.println(carService.get(hondaCivic.getId()));
        carService.delete(hondaCivic.getId());
        carService.addDriverToCar(ben, audiA4);
        carService.removeDriverFromCar(bob, audiA4);
        System.out.println(carService.get(audiA4.getId()));
        carService.getAllByDriver(sem.getId()).forEach(System.out::println);
    }
}
