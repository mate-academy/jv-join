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
        Driver driver1 = new Driver();
        driver1.setName("Dmitro");
        driver1.setLicenseNumber("AAA234333");
        Driver driver2 = new Driver();
        driver2.setName("Ivan");
        driver2.setLicenseNumber("ASD23423");
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("Renault");
        manufacturer1.setCountry("France");
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("Peugeot");
        manufacturer2.setCountry("France");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(
                      ManufacturerService.class);
        driverService.create(driver1);
        driverService.create(driver2);
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        Car car1 = new Car("Scenic");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        car1.setDrivers(drivers);
        car1.setManufacturer(manufacturer1);
        Car car2 = new Car("407");
        car2.setManufacturer(manufacturer2);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.create(car2);
        carService.addDriverToCar(driver2, car2);
        carService.removeDriverFromCar(driver1, car1);
        carService.addDriverToCar(driver2, car1);
        for (Car car : carService.getAll()) {
            System.out.println(car);
        }
        for (Car car : carService.getAllByDriver(driver2.getId())) {
            System.out.println(car);
        }
    }
}
