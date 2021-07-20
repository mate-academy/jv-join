package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

//Test of new functionality (CarService, CarDao, Car)
public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        carService.create(new Car("Sony", new Manufacturer()));
        Car car = carService.get(1L);
        System.out.println(car.toString());
        Manufacturer manufacturer = new Manufacturer("Sony", "Japan");
        manufService.create(manufacturer);
        car.setManufacturer(manufacturer);
        carService.update(car);
        Driver driver = new Driver("Unnamed", "243");
        driverService.create(driver);
        carService.addDriverToCar(driver, car);
        System.out.println(car.toString());
        carService.delete(car.getId());
        System.out.println(carService.get(car.getId()));
    }
}
