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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Manufacturer hondaManufacturer = new Manufacturer("Honda", "Japan");
        manufacturerService.create(hondaManufacturer);
        Driver bob = new Driver("bob", "12345");
        Driver alice = new Driver("alice", "56789");
        Driver tom = new Driver("tom", "34567");
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(tom);
        List<Driver> hondaDrivers = List.of(driverService.get(bob.getId()),
                driverService.get(alice.getId()));
        Car hondaAccord = new Car();
        hondaAccord.setModel("Accord");
        hondaAccord.setManufacturer(hondaManufacturer);
        hondaAccord.setDrivers(hondaDrivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(hondaAccord);
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audiManufacturer);
        List<Driver> audiDrivers = List.of(driverService.get(alice.getId()),
                driverService.get(tom.getId()));
        Car audiA8 = new Car();
        audiA8.setModel("A8");
        audiA8.setManufacturer(audiManufacturer);
        audiA8.setDrivers(audiDrivers);
        carService.create(audiA8);
        System.out.println(carService.getAll());
        System.out.println(carService.get(tom.getId()));
        audiA8.setModel("a85");
        System.out.println(carService.getAllByDriver(alice.getId()));
        System.out.println(carService.update(audiA8));
        System.out.println(carService.delete(hondaAccord.getId()));
    }
}
