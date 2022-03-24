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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driver0001 = driverService.create(new Driver("Igor", "0001"));
        System.out.println(driver0001);
        Driver driver0002 = driverService.create(new Driver("Alex", "0002"));
        System.out.println(driver0002);
        Driver driver = driverService.get(driver0001.getId());
        System.out.println(driver);
        System.out.println(driverService.delete(driver0001.getId()));
        driver0002.setName("Roman");
        System.out.println(driverService.update(driver0002));
        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManufacturer = manufacturerService
                .create(new Manufacturer("BMW", "Germany"));
        System.out.println(bmwManufacturer);
        Manufacturer mercedesManufacturer = manufacturerService
                .create(new Manufacturer("Mercedes", "Germany"));
        System.out.println(mercedesManufacturer);
        Manufacturer fordManufacturer = manufacturerService
                .create(new Manufacturer("FORD", "USA"));
        System.out.println(fordManufacturer);
        manufacturerService.delete(mercedesManufacturer.getId());
        fordManufacturer.setCountry("Japan");
        fordManufacturer.setName("Toyota");
        manufacturerService.update(fordManufacturer);
        System.out.println(manufacturerService.get(fordManufacturer.getId()));
        List<Manufacturer> allManufacturer = manufacturerService.getAll();
        allManufacturer.forEach(System.out::println);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver0001);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("x1", bmwManufacturer, drivers);
        car = carService.create(car);
        System.out.println(car);
        car.setModel("x3");
        car = carService.update(car);
        System.out.println(car);
        Car carToyota = new Car("supra", fordManufacturer);
        List<Driver> driversToyota = new ArrayList<>();
        driversToyota.add(driver0001);
        driversToyota.add(driver0002);
        carToyota.setDrivers(driversToyota);
        carToyota = carService.create(carToyota);
        System.out.println(carService.delete(car.getId()));
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driver0002, carToyota);
        System.out.println(carService.get(carToyota.getId()));
        carService.getAllByDriver(driver0002.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(driver0002, carToyota);
        System.out.println(carService.get(carToyota.getId()));
    }
}
