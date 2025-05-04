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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver john = new Driver("John", "52631");
        Driver bill = new Driver("Bill", "1654");
        Driver den = new Driver("Den", "79546");
        driverService.create(john);
        driverService.create(bill);
        driverService.create(den);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManu = new Manufacturer("bmw", "Germany");
        Manufacturer ferrafiManu = new Manufacturer("ferrari", "Italy");
        manufacturerService.create(bmwManu);
        manufacturerService.create(ferrafiManu);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmw = new Car("BMW", manufacturerService.get(bmwManu.getId()),
                List.of(driverService.get(bill.getId()), driverService.get(john.getId())));
        Car ferrari = new Car("Ferrari", manufacturerService.get(ferrafiManu.getId()),
                List.of(driverService.get(bill.getId()), driverService.get(john.getId())));
        carService.create(bmw);
        carService.create(ferrari);
        System.out.println(carService.get(bmw.getId()));
        carService.addDriverToCar(den, ferrari);
        System.out.println(carService.getAllByDriver(ferrari.getId()));
        carService.delete(ferrari.getId());
    }
}
