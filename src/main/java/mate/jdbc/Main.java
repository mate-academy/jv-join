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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer someManufacturer = new Manufacturer("KIA", "South Korea");
        manufacturerService.create(someManufacturer);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car someCar = new Car(someManufacturer.getId(), "some car");
        someCar = carService.create(someCar);

        Car car = carService.get(someCar.getId());
        car.setModel("another model");
        carService.update(car);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver someDriver = new Driver("some driver", "12345");
        someDriver = driverService.create(someDriver);
        carService.addDriverToCar(someDriver, someCar);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(someDriver, someCar);
        carService.getAllByDriver(someDriver.getId()).forEach(System.out::println);
    }
}
