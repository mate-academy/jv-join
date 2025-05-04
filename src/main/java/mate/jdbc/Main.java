package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBob = new Driver(null, "Bob", "A11");
        System.out.println(driverService.create(driverBob) + " - driver Bob created.");
        Driver driverAlex = new Driver(null, "Alex", "B22");
        System.out.println(driverService.create(driverAlex) + " - driver Alex created.");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverBob);
        drivers.add(driverAlex);
        Manufacturer manufacturer = new Manufacturer(null, "nissan", "japan");
        ManufacturerDao manufacturerDao = (ManufacturerDao) injector
                .getInstance(ManufacturerDao.class);
        System.out.println(manufacturerDao.create(manufacturer) + " manufacturer created");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car(null, "Tundra", manufacturer, drivers);
        System.out.println(carService.create(car) + " car is created");
        System.out.println(carService.get(car.getId()) + " method get executed");
        carService.getAll().forEach(System.out::println);
        manufacturer.setName("ford");
        manufacturer.setCountry("USA");
        car.setManufacturer(manufacturer);
        car.setModel("Shelby GT");
        System.out.println(carService.update(car) + " car is updated");
        Driver nicolasCage = new Driver(null, "Nicolas", "Unlimited");
        driverService.create(nicolasCage);
        carService.addDriverToCar(nicolasCage, car);
        System.out.println(car + System.lineSeparator()
                + "Nicolas cage is added as a driver for " + car.getModel());
        carService.removeDriverFromCar(driverAlex, car);
        System.out.println(car + System.lineSeparator()
                + "Alex has ruined all hopes and was removed as driver for " + car.getModel());
        carService.getAllByDriver(nicolasCage.getId()).forEach(System.out::println);
        System.out.println("Car with id " + car.getId() + " is deleted = "
                + carService.delete(car.getId()) + ". Goodbye Eleonor....");
    }
}
