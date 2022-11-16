package mate.jdbc;

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
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("All cars:");
        carService.getAll().stream().forEach(System.out::println);

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Volkswagen");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Petrenko Viktoria");
        driver.setLicenseNumber("011209");
        driverService.create(driver);

        Car car = new Car();
        car.setModel("Tiguan");
        car.setManufacturer(manufacturer);
        carService.create(car);
        carService.addDriverToCar(driver, car);
        System.out.println("New car:" + System.lineSeparator() + carService.get(car.getId()));

        Car car2 = new Car();
        car2.setModel("Polo");
        car2.setManufacturer(manufacturer);
        carService.create(car2);
        carService.addDriverToCar(driver, car2);
        System.out.println("Cars for " + driver.getName() + ":"
                + System.lineSeparator() + carService.getAllByDriver(driver.getId()));

        car2.setModel("Pololo");
        carService.update(car2);
        carService.removeDriverFromCar(driver, car2);
        System.out.println("Updated car: " + car2);
        carService.delete(car2.getId());
    }
}
