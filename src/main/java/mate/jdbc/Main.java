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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Ford");
        manufacturer.setCountry("USA");
        manufacturer = manufacturerService.create(manufacturer);
        Car car = new Car();
        car.setModel("Lanos");
        car.setManufacturer(manufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver();
        firstDriver.setName("Pavlo");
        firstDriver.setLicenseNumber("121116");
        firstDriver = driverService.create(firstDriver);
        Driver secondDriver = new Driver();
        secondDriver.setName("Brad");
        secondDriver.setLicenseNumber("111116");
        secondDriver = driverService.create(secondDriver);
        car.setDrivers(List.of(firstDriver, secondDriver));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car = carService.create(car);
        System.out.println(car);
        car.setModel("Cuga");
        System.out.println(carService.update(car));
        System.out.println(carService.get(car.getId()));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        Driver thirdDriver = new Driver();
        thirdDriver.setName("Sofia");
        thirdDriver.setLicenseNumber("999992");
        thirdDriver = driverService.create(thirdDriver);
        carService.addDriverToCar(thirdDriver, car);
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(thirdDriver, car);
        System.out.println(carService.get(car.getId()));
    }
}
