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
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println(carService.get(1L));
        System.out.println();
        carService.delete(12L);
        List<Car> carList = carService.getAllByDriver(1L);
        System.out.println(carList);
        System.out.println();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Toyota");
        manufacturer.setCountry("Japan");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(1L);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driver);
        Car car = new Car();
        car.setModel("Camry");
        car.setDriverList(driverList);
        car.setManufacturer(manufacturer);
        carService.create(car);
        carService.addDriverToCar(driverService.get(4L), carService.get(13L));
    }
}
