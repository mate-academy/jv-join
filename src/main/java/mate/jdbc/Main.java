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
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lexus");
        manufacturer.setCountry("Japan");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturer = manufacturerService.create(manufacturer);
        Driver andrey = new Driver();
        andrey.setName("Andrey");
        andrey.setLicenseNumber("HU45RE345467");
        Driver oleg = new Driver();
        oleg.setName("Oleg");
        oleg.setLicenseNumber("JUT45356iI96");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        andrey = driverService.create(andrey);
        oleg = driverService.create(oleg);
        Car car = new Car();
        car.setModel("UNN77");
        List<Driver> drivers = List.of(oleg, andrey);
        car.setDrivers(drivers);
        car.setManufacturer(manufacturer);
        Manufacturer chevrolet = new Manufacturer();
        chevrolet.setName("Chevrolet");
        chevrolet.setCountry("Germany");
        chevrolet = manufacturerService.create(chevrolet);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmw = carService.create(car);
        bmw.setManufacturer(chevrolet);
        bmw.setModel("bmw");
        bmw.setDrivers(drivers);
        Car opelCar = carService.update(bmw);
        if (!opelCar.equals(bmw)) {
            System.out.println("Cars are not equals: " + opelCar + bmw);
        }
        List<Car> cars = carService.getAll();
        System.out.println(cars);
        List<Car> carsByDriver = carService.getAllByDriver(andrey.getId());
        System.out.println(carsByDriver);
        if (!carService.delete(bmw.getId())) {
            throw new RuntimeException("Can't delete car by id " + bmw.getId());
        }
        carService.delete(1L);
    }
}
