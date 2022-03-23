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
        List<Driver> hondaDrivers = List.of(driverService.get(2L), driverService.get(3L));
        Car hondaAccord = new Car();
        hondaAccord.setModel("Accord");
        hondaAccord.setManufacturer(hondaManufacturer);
        hondaAccord.setDrivers(hondaDrivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(hondaAccord);
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audiManufacturer);
        List<Driver> audiDrivers = List.of(driverService.get(4L), driverService.get(5L));
        Car audiA8 = new Car();
        audiA8.setModel("A8");
        audiA8.setManufacturer(audiManufacturer);
        audiA8.setDrivers(audiDrivers);
        carService.create(audiA8);
        System.out.println(carService.getAll());
        System.out.println(carService.get(2L));
        audiA8.setModel("a85");
        System.out.println(carService.getAllByDriver(4L));
        System.out.println(carService.update(audiA8));
        System.out.println(carService.delete(1L));
    }
}
