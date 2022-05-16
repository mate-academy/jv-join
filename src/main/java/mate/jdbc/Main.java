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
        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.getAll();

        carService.delete(7L);

        carService.getAllByDriver(13L);

        Car carToUpdate = carService.get(1L);
        carToUpdate.setModel("Updated");
        carService.update(carToUpdate);

        carService.addDriverToCar(getDriver(), carToUpdate);

        carService.removeDriverFromCar(getDriver(), carToUpdate);
    }

    private static Driver getDriver() {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        return driverService.get(15L);
    }
}
