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
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);

        Driver petro = new Driver("Petro", "4131");
        Driver createdPetro = driverService.create(petro);
        Driver createdIvan = driverService.create(new Driver("Ivan", "3122"));
        Driver newIvan = driverService.get(createdIvan.getId());

        System.out.println("Petro: " + driverService.get(createdPetro.getId()));
        System.out.println("Ivan: " + newIvan);
        List<Driver> puntoDrivers = new ArrayList<>();
        puntoDrivers.add(createdIvan);
        puntoDrivers.add(createdPetro);

        ManufacturerService manufacturerService
                = (ManufacturerService) INJECTOR.getInstance((ManufacturerService.class));
        Manufacturer createdFiat = manufacturerService.create(new Manufacturer("Fiat", "Italy"));
        Manufacturer newFiat = manufacturerService.get(createdFiat.getId());
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        Car punto = new Car("Punto",newFiat, puntoDrivers);

        carService.create(punto);
        System.out.println(carService.get(punto.getId()));
        punto.setModel("Punto Grande");
        carService.update(punto);
        printAllCars(carService);
        Driver createdSemen = driverService.create(new Driver("Semen", "1324"));
        carService.addDriverToCar(createdSemen, punto);

        printAllCars(carService);
        carService.removeDriverFromCar(petro, punto);
        printAllCars(carService);
        System.out.println(carService.getAllByDriver(newIvan.getId()));
        carService.delete(punto.getId());
        printAllCars(carService);
    }

    private static void printAllCars(CarService carService) {
        System.out.println("All cars: ");
        carService.getAll().forEach(System.out::println);
    }
}
