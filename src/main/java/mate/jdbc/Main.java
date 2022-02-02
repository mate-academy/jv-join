package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Driver diesel = new Driver();
        diesel.setName("Diesel");
        diesel.setLicenseNumber("TL000357UA");
        driverService.create(diesel);

        Driver senna = new Driver();
        senna.setName("Ayrton_Senna");
        senna.setLicenseNumber("TL000589UA");
        driverService.create(senna);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(diesel);

        Manufacturer ferrari = new Manufacturer();
        ferrari.setCountry("Italy");
        ferrari.setName("FerrariLTD");
        manufacturerService.create(ferrari);

        Car ferrariLaFerrari = new Car();
        ferrariLaFerrari.setModel("LaFerrari");
        ferrariLaFerrari.setManufacturer(ferrari);
        ferrariLaFerrari.setDrivers(drivers);
        carService.create(ferrariLaFerrari);

        System.out.println(carService.get(ferrariLaFerrari.getId()).getModel());

        System.out.println(carService.getAllByDriver(diesel.getId())
                .stream()
                .map(Car::getModel)
                .collect(Collectors.toList()));

        System.out.println(carService.getAll()
                .stream()
                .map(Car::getManufacturer)
                .map(Manufacturer::getName)
                .collect(Collectors.toList()));

        carService.addDriverToCar(senna,ferrariLaFerrari);
        carService.update(ferrariLaFerrari);
    }
}
