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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer teslaManufacturer = new Manufacturer("Tesla","USA");
        Manufacturer jeepManufacturer = new Manufacturer("Jeep","USA");
        Manufacturer bentleyManufacturer = new Manufacturer("Bentley","England");
        manufacturerService.create(teslaManufacturer);
        manufacturerService.create(jeepManufacturer);
        manufacturerService.create(bentleyManufacturer);
        System.out.println(manufacturerService.getAll());

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = driverService.create(new Driver("John", "675782777"));
        Driver secondDriver = driverService.create(new Driver("Elza", "23553457"));
        Driver thirdDriver = driverService.create(new Driver("Kit", "843336"));
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        System.out.println(driverService.getAll());

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Model X", teslaManufacturer, (List<Driver>) secondDriver);
        Car secondCar = new Car("Model S", teslaManufacturer, (List<Driver>) thirdDriver);
        Car thirdCar = new Car("Сontinental GT", bentleyManufacturer, (List<Driver>) firstDriver);
        Car fourthCar = new Car("Cherokee", jeepManufacturer, (List<Driver>) firstDriver);
        carService.create(firstCar);
        carService.create(secondCar);
        carService.create(thirdCar);
        carService.create(fourthCar);
        System.out.println(carService.getAll());
    }
}
