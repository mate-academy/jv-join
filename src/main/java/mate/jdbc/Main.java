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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver firstDriver = new Driver();
        firstDriver.setName("Alice Stewart");
        firstDriver.setLicenseNumber("5111110008");
        driverService.create(firstDriver);

        Driver secondDriver = new Driver();
        secondDriver.setName("Joe Smith");
        secondDriver.setLicenseNumber("911511112");
        driverService.create(secondDriver);

        Manufacturer firstManufacturer = new Manufacturer();
        firstManufacturer.setName("Lexus");
        firstManufacturer.setCountry("USA");
        manufacturerService.create(firstManufacturer);

        Manufacturer secondManufacturer = new Manufacturer();
        secondManufacturer.setName("Audi");
        secondManufacturer.setCountry("Germany");
        manufacturerService.create(secondManufacturer);

        Car firstCar = new Car();
        firstCar.setModel("2022 LQ");
        firstCar.setManufacturer(manufacturerService.get(manufacturerService.get(1L).getId()));
        firstCar.setDrivers(List.of(driverService.get(secondDriver.getId())));
        carService.create(firstCar);

        Car secondCar = new Car();
        secondCar.setModel("2020 S4");
        secondCar.setManufacturer(manufacturerService.get(secondManufacturer.getId()));
        secondCar.setDrivers(List.of(driverService.get(firstDriver.getId())));
        carService.create(secondCar);

        carService.addDriverToCar(firstDriver, firstCar);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.removeDriverFromCar(firstDriver, secondCar);
        System.out.println(carService.get(secondCar.getId()));
        carService.delete(secondCar.getId());
        carService.getAll().forEach(System.out::println);
    }
}
