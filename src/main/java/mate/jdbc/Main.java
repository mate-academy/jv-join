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

    private static final Injector injector = Injector.getInstance("mate");

    public static void main(String[] args) {
        Long manufacturerId = 1L;
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(manufacturerId);

        Long driver1Id = 4L;
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver driver1 = driverService.get(driver1Id);

        Car newCar = new Car();
        newCar.setModel("Fiesta");
        newCar.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        newCar.setDrivers(drivers);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        //Create
        carService.create(newCar);
        System.out.println("create new car: " + newCar);
        //Read
        List<Car> allCars = carService.getAll();
        System.out.println("all cars:");
        for (Car car : allCars) {
            System.out.println(car);
        }
        List<Car> allCarsByDriver = carService.getAllByDriver(driver1Id);
        System.out.println("all cars by driver (driverId = " + driver1Id + "):");
        for (Car car : allCarsByDriver) {
            System.out.println(car);
        }
        Long carId = newCar.getId();
        Car car = carService.get(carId);
        if (car != null) {
            System.out.println("car by id: " + car);
        } else {
            System.out.println("There is no car with such id (id = " + carId + ")");
        }
        //Update
        newCar.setModel("Fusion");
        carService.update(newCar);
        System.out.println("update car: " + newCar);

        carService.removeDriverFromCar(driver1, newCar);
        System.out.println("remove driver: " + newCar);

        Long driver2Id = 5L;
        Driver driver2 = driverService.get(driver2Id);
        carService.addDriverToCar(driver2, newCar);
        System.out.println("add driver: " + newCar);

        //Delete
        carService.delete(newCar.getId());
    }
}
