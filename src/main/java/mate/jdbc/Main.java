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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer mtz = manufacturerService.get(20L);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver firsDriver = driverService.get(20L);
        Driver secondDriver = driverService.get(21L);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);

        List<Driver> drivers = new ArrayList<>(List.of(firsDriver, secondDriver));
        Car tractor = new Car("Tractor1", mtz, drivers);

        Car createdCar = carService.create(tractor);
        System.out.println("Created car = " + createdCar);
        System.out.println("Got car = " + carService.get(createdCar.getId()));
        createdCar.setModel("Tractor2");
        System.out.println("Updated car = " + carService.update(createdCar));
        Driver thirdDriver = driverService.get(19L);
        carService.addDriverToCar(thirdDriver, createdCar);
        System.out.println("UpdatedWithoutDriver = " + carService.get(createdCar.getId()));
        carService.removeDriverFromCar(firsDriver, createdCar);
        System.out.println("UpdatedWithNewDriver = " + carService.get(createdCar.getId()));
        System.out.println("Deleted = " + carService.delete(createdCar.getId()));
        System.out.println("All cars:");
        carService.getAll().forEach(System.out::println);
        System.out.println("All cars by driver:");
        carService.getAllByDriver(18L).forEach(System.out::println);
    }
}
