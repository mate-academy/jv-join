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
        List<Manufacturer> manufacturers = getManufacturers();
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturers.forEach(manufacturerService::create);
        System.out.println(manufacturerService.get(manufacturers.get(0).getId()));
        manufacturerService.getAll().forEach(System.out::println);
        manufacturers.forEach(manufacturerService::update);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = getDrivers();
        drivers.forEach(driverService::create);
        System.out.println(driverService.get(drivers.get(0).getId()));
        manufacturerService.getAll().forEach(System.out::println);
        drivers.forEach(driverService::update);
        Driver newDriver = getNewDriver();
        driverService.create(newDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = getCars(manufacturers, drivers);
        cars.forEach(carService::create);
        carService.addDriverToCar(newDriver, cars.get(0));
        System.out.println(carService.get(cars.get(0).getId()));
        carService.removeDriverFromCar(newDriver, cars.get(0));
        manufacturerService.getAll().forEach(System.out::println);
        cars.forEach(carService::update);

        manufacturers.forEach(m -> manufacturerService.delete(m.getId()));
        drivers.forEach(d -> driverService.delete(d.getId()));
        cars.forEach(c -> carService.delete(c.getId()));
    }

    private static Driver getNewDriver() {
        return new Driver(null, "Statham", "UK-19450509");
    }

    private static List<Car> getCars(List<Manufacturer> manufacturers, List<Driver> drivers) {
        int idx = 0;
        Car toyota = new Car();
        toyota.setModel("Camri");
        toyota.setManufacturer(manufacturers.get(idx));
        toyota.setDrivers(new ArrayList<>(List.of(drivers.get(idx), drivers.get(++idx))));
        Car audi = new Car();
        audi.setModel("E-Tron");
        audi.setManufacturer(manufacturers.get(idx));
        audi.setDrivers(new ArrayList<>(List.of(drivers.get(idx), drivers.get(++idx))));
        Car tesla = new Car();
        tesla.setModel("Model S");
        tesla.setManufacturer(manufacturers.get(idx));
        tesla.setDrivers(new ArrayList<>(List.of(drivers.get(0), drivers.get(idx))));
        return List.of(toyota, audi, tesla);
    }

    private static List<Driver> getDrivers() {
        return List.of(
                new Driver(null, "Dominic", "DF-123098"),
                new Driver(null, "Michael", "MS-238900"),
                new Driver(null, "Brianna", "BL-983405")
        );
    }

    private static List<Manufacturer> getManufacturers() {
        return List.of(
                new Manufacturer(null, "Toyota", "Japan"),
                new Manufacturer(null, "Audi", "Germany"),
                new Manufacturer(null, "Tesla", "USA")
        );
    }
}
