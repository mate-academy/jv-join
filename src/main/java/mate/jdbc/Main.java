package mate.jdbc;

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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("audi");
        manufacturer.setCountry("germany");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver();
        firstDriver.setName("bob");
        firstDriver.setLicenseNumber("BobLicenseNumber7");
        driverService.create(firstDriver);
        Driver secondDriver = new Driver();
        secondDriver.setName("alice");
        secondDriver.setLicenseNumber("AliceLicenseNumber7");
        driverService.create(secondDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car();
        firstCar.setModel("Q7");
        firstCar.setManufacturer(manufacturer);
        carService.create(firstCar);
        Car secondCar = new Car();
        secondCar.setModel("Q6");
        secondCar.setManufacturer(manufacturer);
        carService.create(secondCar);
        System.out.println(carService.getAll());
        carService.addDriverToCar(firstDriver, firstCar);
        carService.addDriverToCar(firstDriver, secondCar);
        carService.addDriverToCar(secondDriver, secondCar);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.removeDriverFromCar(firstDriver, secondCar);
        System.out.println(carService.get(secondCar.getId()));
        secondCar.setModel("Q5");
        carService.update(secondCar);
        System.out.println(carService.get(secondCar.getId()));
        carService.delete(firstCar.getId());
        System.out.println(carService.getAll());
    }
}
