package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
             = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer("Sony", "Japan");
        manufService.create(manufacturer);
        Car car2 = new Car("Sony", manufacturer);
        carService.create(car2);
        car2.setManufacturer(manufacturer);
        System.out.println(car2.toString());
        carService.update(car2);
        Driver driver = new Driver("Unnamed", "243");
        driverService.create(driver);
        carService.addDriverToCar(driver, car2);
        System.out.println(car2.toString());
        //carService.delete(car2.getId());
        System.out.println(carService.get(car2.getId()));
        Car car = carService.get(2L);
    }
}
