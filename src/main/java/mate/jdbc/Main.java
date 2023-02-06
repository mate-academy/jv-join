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
        Manufacturer manufacturerJeep = new Manufacturer();
        manufacturerJeep.setName("Jeep");
        manufacturerJeep.setCountry("USA");
        Manufacturer manufacturerTesla = new Manufacturer();
        manufacturerTesla.setName("Tesla");
        manufacturerTesla.setCountry("USA");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerJeep);
        manufacturerService.create(manufacturerTesla);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = new Driver();
        driverService.create(driverBob);
        Driver driverAlice = new Driver();
        driverService.create(driverAlice);
        Driver driverBill = new Driver();
        driverBill.setName("Bill");
        driverBill.setLicenseNumber("KJ55880");
        driverService.create(driverBill);
        List<Driver> driverListJeep = new ArrayList<>();
        driverListJeep.add(driverAlice);
        driverListJeep.add(driverBob);
        driverListJeep.add(driverBill);
        List<Driver> driversList = driverService.getAll();
        Car carJeep = new Car();
        carJeep.setModel("Wrangler");
        carJeep.setManufacturer(manufacturerJeep);
        carJeep.setDrivers(driversList);
        Car modelX = new Car();
        modelX.setManufacturer(manufacturerTesla);
        modelX.setModel("X");
        modelX.setDrivers(driverListJeep);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(modelX);
        List<Car> carList = carService.getAll();
    }
}
