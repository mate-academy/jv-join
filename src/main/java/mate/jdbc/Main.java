package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {

    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(
                ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.removeDriverFromCar(driverService.get(2L), carService.get(1L));
        List<Car> cars = carService.getAll();
        for (Car car : cars) {
            System.out.println(car);
        }
    }
}
