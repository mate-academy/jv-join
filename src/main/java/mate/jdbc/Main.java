package mate.jdbc;

import java.util.Set;
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

    public static void main(String[] args) {
        Manufacturer manufacturer1 = new Manufacturer("bmv", "germany");
        Manufacturer manufacturer2 = new Manufacturer("honda", "japan");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturer1.setCountry("Ukraine");
        manufacturerService.update(manufacturer1);

        Driver driver1 = new Driver("Oleg", "aa1234bb");
        Driver driver2 = new Driver("Petro", "bb4344cc");
        driverService.create(driver1);
        driverService.create(driver2);
        driver1.setLicenseNumber("newNumber");
        driverService.update(driver1);

        Car car1 = new Car(manufacturer1, "model1", Set.of(driver1));
        Car car2 = new Car(manufacturer2, "model2", Set.of(driver1, driver2));
        carService.create(car1);
        carService.create(car2);
        carService.addDriverToCar(car1, driver2);
        carService.get(car1.getId());
        carService.removeDriverFromCar(car2, driver1);
        carService.delete(car1.getId());
    }
}
