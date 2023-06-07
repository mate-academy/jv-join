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
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final Manufacturer manufacturer = new Manufacturer("zaporojec", "Ukraine");
    private static final Driver driver = new Driver("marco", "222");
    private static final Car car = new Car(9L,"neOchenGorbatiy",manufacturer, List.of(driver));

    public static void main(String[] args) {
        manufacturerService.create(manufacturer);
        driverService.create(driver);
        carService.update(car);

        for (Car carz : carService.getAllByDriver(2L)) {
            System.out.println(carz);
        }

        for (Car car1 : carService.getAll()) {
            System.out.println(car1);
        }

        //carService.removeDriverFromCar(driverService.get(9L), carService.get(6L));
    }
}
