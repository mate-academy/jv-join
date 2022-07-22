package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(2L));
        Car bmwX5 = new Car("BMW X5", manufacturerService.get(1L), drivers);
        bmwX5.setId(20L);
        carService.create(bmwX5);

        List<Driver> driversList = new ArrayList<>();
        driversList.add(driverService.get(5L));
        driversList.add(driverService.get(6L));
        Car skodaFabia = new Car("Skoda Fabia", manufacturerService.get(4L), driversList);
        skodaFabia.setId(5L);
        carService.update(skodaFabia);

        carService.delete(2L);

        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(6L).forEach(System.out::println);

        carService.addDriverToCar(driverService.get(3L), carService.get(2L));

        carService.removeDriverFromCar(driverService.get(3L), carService.get(2L));
    }
}
