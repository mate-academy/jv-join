package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        // Create model Car
        Car car = carService.create(new Car(1L,"BMW X1 sDrive18i",manufacturerService.get(12L)));
        // Add driver
        carService.addDriverToCar(new Driver(15L, "Petro", "lic03044_333238cer"), car);
        carService.getAll().forEach(System.out::println);
        // Delete driver
        carService.removeDriverFromCar(driverService.get(15L), car);
        carService.getAll().forEach(System.out::println);
        // Get all drivers
        System.out.println(carService.getAllByDriver(1L));
        // Read data model Car
        System.out.println(carService.get(2L));
        // Update data model Car
        car.setId(1L);
        car.setModel("Audi Q7");
        car.setManufacturer(manufacturerService.get(12L));
        System.out.println(carService.update(car));
        // Delete model Car
        System.out.println(carService.delete(1L));
    }
}
