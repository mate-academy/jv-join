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
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        bmw = manufacturerService.create(bmw);
        Driver driverOne = new Driver();
        driverOne.setLicenseNumber("228");
        driverOne.setName("Alexey");
        Driver driverTwo = new Driver();
        driverTwo.setLicenseNumber("322");
        driverTwo.setName("Slavik");
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverOne = driverService.create(driverOne);
        driverTwo = driverService.create(driverTwo);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverOne);
        Car bmwCar = new Car();
        bmwCar.setModel("x6");
        bmwCar.setManufacturer(bmw);
        bmwCar.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        bmwCar = carService.create(bmwCar);
        System.out.println(carService.get(bmwCar.getId()) + "\n");
        bmwCar.setModel("x7");
        bmwCar = carService.update(bmwCar);
        System.out.println(carService.get(bmwCar.getId()) + "\n");
        carService.addDriverToCar(driverTwo, bmwCar);
        System.out.println(carService.get(bmwCar.getId()) + "\n");
    }
}
