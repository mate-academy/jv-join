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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer peugeot = new Manufacturer("Peugeot", "France");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");

        System.out.println("ManufacturerService:");
        manufacturerService.create(peugeot);
        manufacturerService.create(toyota);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        System.out.println("DriverService:");
        Driver firstDriver = new Driver("Thomas Schmidt", "AA1234");
        Driver secondDriver = new Driver("Taras Melnyk", "BB2345");
        Driver thirdDriver = new Driver("Ivan Petrenko", "CC3456");

        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(secondDriver);
        List<Driver> peugeotDrivers = new ArrayList<>();
        peugeotDrivers.add(firstDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("CarService:");

        Car toyotaCamry = new Car("Camry", toyota, toyotaDrivers);
        Car peugeot508 = new Car("508", peugeot, peugeotDrivers);

        carService.create(toyotaCamry);
        carService.create(peugeot508);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(thirdDriver, toyotaCamry);
        carService.removeDriverFromCar(firstDriver, peugeot508);
    }
}
