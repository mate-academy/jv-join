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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Car car = new Car();
        car.setModel("Model1");
        car.setManufacturer(manufacturerService.create(new Manufacturer("GMC", "USA")));
        car.setDrivers(List.of(driverService.create(new Driver("Johnson", "222"))));
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        car = carService.create(car);
        System.out.println(car);
        System.out.println(carService.get(car.getId()));
        Driver driver = driverService.create(new Driver("Xavi", "345"));
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));
        car.setModel("Model2");
        car.setManufacturer(manufacturerService.create(new Manufacturer("Bentley", "UK")));
        car.setDrivers(List.of(driverService
                        .create(new Driver("Kawabata", "123")),
                driverService.create(new Driver("Alonso", "321"))));
        System.out.println(carService.update(car));
        System.out.println(carService.delete(car.getId()));
        System.out.println(carService.getAll());
        driver = driverService.create(new Driver("Gonzales", "777"));
        Car car1 = new Car();
        car1.setModel("Model3");
        car1.setManufacturer(manufacturerService
                .create(new Manufacturer("Ford", "USA")));
        car1.setDrivers(List.of(driverService
                .create(new Driver("Peterson", "333"))));
        car1 = carService.create(car1);
        carService.addDriverToCar(driver, car1);
        Car car2 = new Car();
        car2.setModel("Model4");
        car2.setManufacturer(manufacturerService.create(new Manufacturer("BMW", "Germany")));
        car2.setDrivers(List.of(driverService
                .create(new Driver("Robinson", "444"))));
        car2 = carService.create(car2);
        carService.addDriverToCar(driver, car2);
        System.out.println(carService.getAllByDriver(driver.getId()));
    }
}
