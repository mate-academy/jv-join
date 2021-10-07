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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("BMW", "German");
        manufacturer = manufacturerService.create(manufacturer);
        Driver firstDriver = new Driver("Vito", "32532");
        Driver secondDriver = new Driver("Mark", "3252312");
        firstDriver = driverService.create(firstDriver);
        secondDriver = driverService.create(secondDriver);
        List<Driver> driversList = new ArrayList<>();
        driversList.add(firstDriver);
        driversList.add(secondDriver);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("M4", manufacturer);
        car.setDrivers(driversList);
        carService.create(car);
        System.out.println("Get car: ");
        System.out.println(carService.get(car.getId()));
        System.out.println("Get all car: ");
        for (Car cars: carService.getAll()) {
            System.out.println(cars);
        }
        car.setModel("X6");
        carService.update(car);
        System.out.println("Updated car: ");
        System.out.println(carService.get(car.getId()));
        carService.delete(1L);
        carService.addDriverToCar(secondDriver, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("Remove driver from car");
        carService.removeDriverFromCar(secondDriver, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("Get all cars by driverId: ");
        carService.getAllByDriver(1L);
    }
}
