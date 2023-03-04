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
        manufacturer.setName("name");
        manufacturer.setCountry("country");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);

        Driver firstDriver = new Driver();
        firstDriver.setName("firstName");
        firstDriver.setLicenseNumber("firstNumber");
        Driver secondDriver = new Driver();
        secondDriver.setName("secondName");
        secondDriver.setLicenseNumber("secondNumber");
        Driver thirdDriver = new Driver();
        thirdDriver.setName("thirdName");
        thirdDriver.setLicenseNumber("thirdNumber");

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);

        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("model");
        car.setDrivers(List.of(firstDriver, secondDriver, thirdDriver));

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(1L));
        carService.getAll().forEach(System.out::println);
        car.setModel("new model");
        carService.update(car);
        System.out.println(carService.getAllByDriver(1L));
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
