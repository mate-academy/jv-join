package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        carService.create(new Car(null, "Q7", manufacturerService.get(1L),
                List.of(driverService.get(2L), driverService.get(3L))));
        System.out.println(carService.get(5L));
        System.out.println(carService.getAll());

        System.out.println(carService.delete(10L));
        System.out.println(carService.getAll());

        System.out.println(carService.getAllByDriver(2L));

        carService.update(new Car(1L, "R8", manufacturerService.get(1L),
                List.of(driverService.get(5L))));

        carService.addDriverToCar(driverService.get(2L), carService.get(1L));
        carService.removeDriverFromCar(driverService.get(2L), carService.get(1L));
    }
}
