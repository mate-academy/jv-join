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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("WV", "GERMANY");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("Ivan", "839481");
        Driver secondDriver = new Driver("Max", "382481");
        Driver thirdDriver = new Driver("Tim", "030331");
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);

        CarService carService = (CarService) 
                injector.getInstance(CarService.class);
        Car car = new Car("Passat", manufacturer, drivers);
        carService.create(car);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.addDriverToCar(thirdDriver, car);
        carService.getAll().forEach(System.out::println);
        car.setModel("Golf");
        carService.update(car);
        carService.removeDriverFromCar(secondDriver, car);
        carService.delete(car.getId());
    }
}
