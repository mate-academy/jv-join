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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver("Lightning McQueen", "st69");
        Driver driver2 = new Driver("Yarik Vodila", "ba40k");
        Driver driver3 = new Driver("Vin Diesel", "777");
        driverService.create(driver);
        driverService.create(driver2);
        driverService.create(driver3);

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Mitsubishi", "Japan");
        Manufacturer manufacturer2 = new Manufacturer("Audi", "Germany");
        manufacturerService.create(manufacturer);
        manufacturerService.create(manufacturer2);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driver);
        Car car = new Car("Lancer Evolution 6", manufacturer, driverList);
        carService.create(car);
        System.out.println("----GET ALL CARS----");
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(car.getId()));

        car.setModel("RS 6");
        driverList.add(driver2);
        car.setManufacturer(manufacturer2);
        System.out.println("----GET UPDATED CAR----");
        System.out.println(carService.update(car));

        System.out.println("----GET ALL BY DRIVER----");
        carService.getAllByDriver(driver2.getId()).forEach(System.out::println);

        carService.addDriverToCar(driver3, car);
        System.out.println("----ADDED DRIVER TO CAR----");
        System.out.println(carService.get(car.getId()));

        carService.removeDriverFromCar(driver3, car);
        System.out.println("----REMOVED DRIVER FROM CAR----");
        System.out.println(carService.get(car.getId()));

        carService.delete(car.getId());
        System.out.println("----REMOVED CAR----");
        System.out.println(carService.getAll());
    }
}
