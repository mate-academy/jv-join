package mate.jdbc;

import java.util.ArrayList;
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

        Manufacturer lada = new Manufacturer("Lada", "Ukraine");
        manufacturerService.create(lada);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("driver1", "789");
        Driver driver2 = new Driver("driver2", "124");
        driverService.create(driver1);
        driverService.create(driver2);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Lada Sedan", lada, new ArrayList<>());
        carService.create(car);
        carService.addDriverToCar(driver1, car);
        carService.addDriverToCar(driver2, car);
        carService.getAllByDriver(driver1.getId());
        carService.getAll();
        car.setModel("Lada New");
        carService.update(car);
        carService.get(car.getId());
        carService.removeDriverFromCar(driver2, car);
        carService.delete(car.getId());
    }
}
