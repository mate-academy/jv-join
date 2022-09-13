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

        System.out.println("Create: ");
        manufacturer.setName("Mercedes-Benz");
        manufacturer.setCountry("Germany");
        Manufacturer mercedes = manufacturerService.create(manufacturer);
        System.out.println(mercedes);

        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Bob");
        driver.setLicenseNumber("33333");
        Driver createdDriver = driverService.create(driver);
        System.out.println(createdDriver);

        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = new Car();
        car.setModel("C63AMG");
        car.setManufacturer(mercedes);
        car.setDrivers(List.of(driver));
        Car createdCar = carService.create(car);
        System.out.println(createdCar);

        Car getCarById = carService.get(createdCar.getId());
        System.out.println(getCarById);

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        getCarById.setModel("Maybach");
        createdDriver.setName("Alice");
        createdDriver.setLicenseNumber("44444");
        createdDriver = driverService.create(createdDriver);
        List<Driver> updateDrivers = getCarById.getDrivers();
        updateDrivers.add(createdDriver);
        System.out.println(carService.update(getCarById));

        List<Car> driversCars = carService.getAllByDriver(getCarById.getId());
        driversCars.forEach(System.out::println);

        Driver addedDriver = new Driver();
        addedDriver.setName("Sam");
        addedDriver.setLicenseNumber("55555");
        driverService.create(addedDriver);
        carService.addDriverToCar(addedDriver, getCarById);
        System.out.println(carService.get(getCarById.getId()));

        carService.removeDriverFromCar(addedDriver,getCarById);
        System.out.println(carService.get(getCarById.getId()));

        System.out.println(carService.delete(createdCar.getId()));
    }
}
