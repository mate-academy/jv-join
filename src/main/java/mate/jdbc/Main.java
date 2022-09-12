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
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Ford");
        manufacturer.setCountry("USA");
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        System.out.println(createdManufacturer);
        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Chris");
        driver.setLicenseNumber("us12th3r56");
        Driver createdDriver = driverService.create(driver);
        System.out.println(createdDriver);

        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = new Car();
        car.setModel("Fiesta");
        car.setManufacturer(createdManufacturer);
        car.setDrivers(List.of(driver));
        Car createdCar = carService.create(car);
        System.out.println("Created car: " + createdCar);

        Car getCarById = carService.get(createdCar.getId());
        System.out.println("Get car by id: " + getCarById);

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        getCarById.setModel("Lamborghini");
        createdDriver.setName("Phil");
        createdDriver.setLicenseNumber("gg8798");
        createdDriver = driverService.create(createdDriver);
        List<Driver> updateDrivers = getCarById.getDrivers();
        updateDrivers.add(createdDriver);
        System.out.println("Updated car: " + carService.update(getCarById));

        List<Car> driversCars = carService.getAllByDriver(getCarById.getId());
        driversCars.forEach(System.out::println);

        Driver addedDriver = new Driver();
        addedDriver.setName("Paul");
        addedDriver.setLicenseNumber("12345");
        driverService.create(addedDriver);
        carService.addDriverToCar(addedDriver, getCarById);
        System.out.println("Added driver to this car: "
                + carService.get(getCarById.getId()));

        carService.removeDriverFromCar(addedDriver,getCarById);
        System.out.println("Removed driver from this car: "
                + carService.get(getCarById.getId()));

        System.out.println("Deleted car: " + carService.delete(createdCar.getId()));
    }
}
