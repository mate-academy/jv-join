package mate.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car audi = new Car();
        audi.setId(2L);
        audi.setModel("R8");
        audi.setManufacturer(manufacturerService.get(2L));
        audi.setDrivers(new ArrayList<>(Collections.singletonList(driverService.get(1L))));
        List<Car> allCar = carService.getAll();
        for (Car car: allCar) {
            System.out.println(car);
        }
        carService.addDriverToCar(driverService.get(3L), audi);
        carService.removeDriverFromCar(driverService.get(1L), audi);
        allCar = carService.getAll();
        for (Car car: allCar) {
            System.out.println(car);
        }
    }
}
