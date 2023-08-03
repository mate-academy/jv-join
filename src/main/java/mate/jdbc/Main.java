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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        // Created manufacturer
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);

        //Created drivers
        Driver driver = new Driver();
        driver.setName("Mad Max");
        driver.setLicenseNumber("TR376542");
        driverService.create(driver);
        Driver driver2 = new Driver();
        driver2.setName("Daniel");
        driver2.setLicenseNumber("TY423476");
        driverService.create(driver2);
        Driver driver3 = new Driver();
        driver3.setName("Bill");
        driver3.setLicenseNumber("KI435781");


        //Created cars
        List<Driver> driversList = new ArrayList<>();
        driversList.add(driver);
        Car car = new Car();
        car.setDrivers(driversList);
        car.setManufacturer(manufacturer);
        car.setModel("M340");

        carService.create(car);
        carService.get(car.getId());
        carService.update(car);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driver3, car);
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.getAllByDriver(driver2.getId()));
        carService.delete(car.getId());
    }
}
