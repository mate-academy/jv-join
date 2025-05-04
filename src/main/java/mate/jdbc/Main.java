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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver firstDriver = driverService.get(2L);
        Driver secondDriver = driverService.get(3L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);
        Manufacturer getManufacturedDb = manufacturerService.get(1L);
        Car car = new Car();
        car.setModel("X4");
        car.setManufacturer(getManufacturedDb);
        car.setDrivers(drivers);
        car.setId(4L);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        carService.removeDriverFromCar(firstDriver, car);
        Driver addDriver = driverService.get(1L);
        carService.addDriverToCar(addDriver, car);
        Car createCar = carService.create(car);
        System.out.println(createCar);
        Car getCarById = carService.get(4L);
        System.out.println(getCarById);
        List<Car> allCarList = carService.getAll();
        allCarList.forEach(System.out::println);
        Car updateCar = carService.update(car);
        System.out.println(updateCar);
        boolean deleteCar = carService.delete(4L);
        System.out.println(deleteCar);
        List<Car> allByDriver = carService.getAllByDriver(2L);
        allByDriver.forEach(System.out::println);
    }
}
