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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) INJECTOR.getInstance((ManufacturerService.class));
        Driver petro = driverService.get(1L);
        Driver ivan = driverService.get(2L);
        List<Driver> puntoDrivers = new ArrayList<>();
        puntoDrivers.add(petro);
        puntoDrivers.add(ivan);
        Manufacturer fiat = manufacturerService.get(1L);
        Car punto = new Car("Punto",fiat, puntoDrivers);
        carService.create(punto);
        System.out.println(carService.get(punto.getId()));
        punto.setModel("Punto Grande");
        carService.update(punto);
        printAllCars(carService);
        Driver semen = driverService.get(3L);
        carService.addDriverToCar(semen, punto);
        printAllCars(carService);
        carService.removeDriverFromCar(petro, punto);
        printAllCars(carService);
        System.out.println(carService.getAllByDriver(ivan.getId()));
        carService.delete(punto.getId());
        printAllCars(carService);
    }

    private static void printAllCars(CarService carService) {
        System.out.println("All cars: ");
        carService.getAll().forEach(System.out::println);
    }
}
