package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car car = new Car();
        Manufacturer manufacturer = manufacturerService.get(25L);
        car.setManufacturer(manufacturer);
        car.setModel("Lamborghini");
        car = carService.create(car);
        List<Driver> drivers = new ArrayList<>();
        car.setDrivers(drivers);
        System.out.println("Get car: ");
        System.out.println(carService.get(car.getId()));
        System.out.println("Get all cars: ");
        for (Car cars : carService.getAll()) {
            System.out.println(cars);
        }
        System.out.println("Update Labmo to Ferrari: ");
        car.setModel("Ferrari");
        System.out.println(carService.update(car));
        System.out.println("Add driver to car: ");
        Driver driver = new Driver();
        driver.setId(1L);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("Remove driver from car");
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("Get all cars by driverId: ");
        carService.getAllByDriver(1L);
    }
}
