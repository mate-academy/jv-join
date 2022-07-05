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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        manufacturerService.create(mercedes);
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyota);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> mercedesDrivers = new ArrayList<>();
        Driver driverAlex = new Driver("Alex","123456");
        driverService.create(driverAlex);
        mercedesDrivers.add(driverAlex);

        Driver driverKate = new Driver("Kate", "789123");
        driverService.create(driverKate);
        mercedesDrivers.add(driverKate);

        List<Driver> toyotaDrivers = new ArrayList<>();
        Driver driverBob = new Driver("Bob", "567234");
        driverService.create(driverBob);
        toyotaDrivers.add(driverBob);

        Driver driverAlice = new Driver("Alice", "987654");
        driverService.create(driverAlice);
        toyotaDrivers.add(driverAlice);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car mercedesCar = new Car("C200", mercedes, mercedesDrivers);
        carService.create(mercedesCar);

        Car toyotaCar = new Car("RAV4", toyota, toyotaDrivers);
        carService.create(toyotaCar);

        System.out.println(carService.getAll());
        System.out.println(System.lineSeparator());

        carService.addDriverToCar(driverBob, mercedesCar);
        System.out.println(carService.get(mercedesCar.getId()));
        System.out.println(System.lineSeparator());
        System.out.println(carService.getAllByDriver(driverBob.getId()));
        System.out.println(System.lineSeparator());

        carService.removeDriverFromCar(driverBob, toyotaCar);
        System.out.println(carService.get(toyotaCar.getId()));
        System.out.println(System.lineSeparator());

        mercedesCar.setManufacturer(toyota);
        carService.update(mercedesCar);
        System.out.println(carService.get(mercedesCar.getId()));
        System.out.println(System.lineSeparator());

        carService.delete(toyotaCar.getId());
        System.out.println(carService.getAll());
        System.out.println(System.lineSeparator());
    }
}
