package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver firstDriver = new Driver();
        firstDriver.setName("Kirk Hammet");
        firstDriver.setLicenseNumber("315131541");
        driverService.create(firstDriver);

        Driver secondDriver = new Driver();
        secondDriver.setName("Steve Vai");
        secondDriver.setLicenseNumber("845965789");
        driverService.create(secondDriver);

        Manufacturer firstManufacturer = new Manufacturer();
        firstManufacturer.setName("Ford");
        firstManufacturer.setCountry("USA");
        manufacturerService.create(firstManufacturer);

        Manufacturer secondManufacturer = new Manufacturer();
        secondManufacturer.setName("Honda");
        secondManufacturer.setCountry("Japan");
        manufacturerService.create(secondManufacturer);

        Car firstCar = new Car();
        firstCar.setModel("F150");
        firstCar.setManufacturer(manufacturerService.get(manufacturerService.get(17L).getId()));
        firstCar.setDrivers(new ArrayList<>());
        carService.create(firstCar);

        Car secondCar = new Car();
        secondCar.setModel("Accord");
        secondCar.setManufacturer(manufacturerService.get(secondManufacturer.getId()));
        secondCar.setDrivers(new ArrayList<>());
        carService.create(secondCar);

        carService.addDriverToCar(firstDriver, firstCar);
        carService.addDriverToCar(secondDriver, firstCar);
        carService.addDriverToCar(firstDriver, secondCar);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.removeDriverFromCar(firstDriver, secondCar);
        System.out.println(carService.get(secondCar.getId()));
        carService.delete(secondCar.getId());
        carService.getAll().forEach(System.out::println);
    }
}
