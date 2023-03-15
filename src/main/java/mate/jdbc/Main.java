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
    private static final Injector injector =
            Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer ford = manufacturerService.get(6L);
        List<Driver> drivers = driverService.getAll();

        Car fordFocus = new Car("Focus", ford, drivers);
        carService.create(fordFocus);

        fordFocus.getDrivers().remove(3);
        fordFocus.setModel("Focus GT");
        carService.update(fordFocus);

        System.out.println(carService.get(fordFocus.getId()));

        carService.delete(4L);

        System.out.println("All cars by driver id 2: ");
        List<Car> allCarsByDriver = carService.getAllByDriver(2L);
        allCarsByDriver.forEach(System.out::println);

        Driver johnSmith = driverService.get(1L);
        Car toyotaCorolla = carService.get(6L);
        carService.addDriverToCar(johnSmith, toyotaCorolla);

        Car bmwX5 = carService.get(1L);
        carService.removeDriverFromCar(johnSmith, bmwX5);

        List<Car> allCars = carService.getAll();
        System.out.println("Print the list of all cars after all operations: ");
        allCars.forEach(System.out::println);

    }
}
