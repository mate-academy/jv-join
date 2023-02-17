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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver firstDriver = new Driver();
        firstDriver.setName("Andrii");
        firstDriver.setLicenseNumber("0000");
        Driver secondDriver = new Driver();
        secondDriver.setName("Liuba");
        secondDriver.setLicenseNumber("7777");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.create(firstDriver);
        driverService.create(secondDriver);

        List<Driver> firstList = new ArrayList<>();
        firstList.add(firstDriver);
        firstList.add(secondDriver);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Opel");
        manufacturer.setCountry("Germany");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);

        Car carVectra = new Car();
        carVectra.setModel("Vectra");
        carVectra.setManufacturer(manufacturer);
        carVectra.setDrivers(firstList);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carVectra);

        Driver thirdDriver = new Driver();
        thirdDriver.setName("Dima");
        thirdDriver.setLicenseNumber("9999");
        driverService.create(thirdDriver);

        carService.addDriverToCar(thirdDriver, carVectra);
        carService.removeDriverFromCar(secondDriver, carVectra);

        manufacturer.setName("Fiat");
        manufacturer.setCountry("Italy");
        manufacturerService.create(manufacturer);

        List<Driver> secondList = new ArrayList<>();
        secondList.add(secondDriver);
        secondList.add(thirdDriver);

        Car carDoblo = new Car();
        carDoblo.setModel("Doblo");
        carDoblo.setManufacturer(manufacturer);
        carDoblo.setDrivers(secondList);
        carService.create(carDoblo);

        carDoblo.setModel("Punto");
        carService.update(carDoblo);
        carService.getAllByDriver(75L).forEach(System.out::println);
    }
}
