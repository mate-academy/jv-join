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
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("The result of the method getAll() "
                + "from CarServiceImpl Class: ");
        carService.getAll().stream().forEach(System.out::println);

        Car receivedCar = carService.get(3L);
        System.out.println("The result of the method get() from CarServiceImpl Class: "
                + receivedCar);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Anduin");
        driver.setLicenseNumber("12346895");
        driverService.create(driver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Germany");
        manufacturer.setName("Volkswagen AG");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Car car = new Car();
        car.setModel("Golf");
        car.setDrivers(drivers);
        car.setManufacturer(manufacturerService.get(4L));
        Car golf = carService.create(car);
        System.out.println("The result of the method create() from CarServiceImpl Class: "
                + golf);

        golf.setModel("Golf-531");
        Car updatedGolf = carService.update(car);
        System.out.println("The result of the method update() from CarServiceImpl Class: "
                + updatedGolf);

        boolean deletedCar = carService.delete(car.getId());
        System.out.println("The result of the method delete() from CarServiceImpl Class: "
                + deletedCar);

        List<Car> allCarsByDriver = carService.getAllByDriver(1L);
        System.out.println("The result of the method getAllByDriver() from CarServiceImpl Class: "
                + allCarsByDriver);
    }
}
