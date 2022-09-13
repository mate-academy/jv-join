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

        System.out.println("Create manufacturer");
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        Manufacturer createdManufacturer = manufacturerService.create(bmw);
        System.out.println(createdManufacturer);

        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);

        System.out.println("Create driver");
        Driver valera = new Driver(null, "Valera", "12118213");
        Driver createdDriver = driverService.create(valera);
        System.out.println(createdDriver);

        final CarService carService = (CarService) injector
                .getInstance(CarService.class);

        System.out.println("Create car");
        Car car = new Car();
        car.setModel("330i");
        car.setManufacturer(createdManufacturer);
        car.setDrivers(List.of(valera));
        Car createdCar = carService.create(car);
        System.out.println("Created car: " + createdCar);

        System.out.println("Get car by id");
        Car getCarById = carService.get(createdCar.getId());
        System.out.println(getCarById);

        System.out.println("Get all car");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        getCarById.setModel("Audi");
        createdDriver.setName("Igor");
        createdDriver.setLicenseNumber("3128131223");

        System.out.println("Update car");
        createdDriver = driverService.create(createdDriver);
        List<Driver> updateDrivers = getCarById.getDrivers();
        updateDrivers.add(createdDriver);
        System.out.println(carService.update(getCarById));

        System.out.println("Get all by driver");
        List<Car> driversCars = carService.getAllByDriver(getCarById.getId());
        driversCars.forEach(System.out::println);

        System.out.println("Add new driver to car");
        Driver addedDriver = new Driver(null, "Mykola", "112181223123");
        driverService.create(addedDriver);
        carService.addDriverToCar(addedDriver, getCarById);
        System.out.println(carService.get(getCarById.getId()));

        System.out.println("Removed driver from car");
        carService.removeDriverFromCar(addedDriver,getCarById);
        System.out.println(carService.get(getCarById.getId()));

        System.out.println("Deleted car");
        System.out.println(carService.delete(createdCar.getId()));
    }
}
