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
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver pavlo = new Driver("Pavlo", "abc23456");
        Driver misha = new Driver("Misha", "abc2345");
        Driver anton = new Driver("Anton", "ab12");
        driverService.create(pavlo);
        driverService.create(misha);
        driverService.create(anton);
        Manufacturer volkswagen = new Manufacturer("volkswagen", "Germany");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(volkswagen);
        Car passat = new Car();
        passat.setModel("passat");
        passat.setManufacturer(volkswagen);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(pavlo);
        drivers.add(misha);
        passat.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(passat);
        System.out.println(passat);
        carService.removeDriverFromCar(pavlo, passat);
        carService.addDriverToCar(anton, passat);
        carService.removeDriverFromCar(misha, passat);
        System.out.println(passat.getDrivers());
        List<Car> cars = carService.getAllByDriver(anton.getId());
    }
}
