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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driverList = new ArrayList<>();
        Driver driver = new Driver("Anna", "235-89");
        driver = driverService.create(driver);
        driverList.add(driver);
        Manufacturer manufacturer = new Manufacturer("Bentley", "England");
        manufacturer = manufacturerService.create(manufacturer);
        Car car = new Car("Continental", manufacturer, driverList);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car = carService.create(car);
        carService.get(car.getId());
        carService.getAll();
        car.setModel("DBX-2021");
        manufacturer.setName("AstonMartin");
        carService.update(car);
        carService.getAllByDriver(driver.getId());
        Driver driver2 = new Driver("Max", "235-89");
        driver2 = driverService.create(driver2);
        carService.addDriverToCar(driver2, car);
        carService.removeDriverFromCar(driver2, car);
        carService.delete(car.getId());
    }
}
