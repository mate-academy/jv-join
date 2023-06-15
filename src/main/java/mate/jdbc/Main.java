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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        final Driver drJoshua = driverService.create(new Driver("Joshua", "DL00237"));
        final Driver drNick = driverService.create(new Driver("Nick", "DL00525"));
        final Manufacturer mnToyota
                = manufacturerService.create(new Manufacturer("Toyota", "Japan"));
        final Manufacturer mnFord
                = manufacturerService.create(new Manufacturer("Ford", "USA"));
        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(drJoshua);
        toyotaDrivers.add(drNick);

        Car carola = new Car("Carola", mnToyota, toyotaDrivers);
        Car mustang = new Car("Mustang", mnFord, new ArrayList<>());

        carService.create(carola);
        carService.create(mustang);

        System.out.println("Car: " + mustang.getId());
        System.out.println(carService.get(mustang.getId()));
        System.out.println("Driver: " + drNick.getId());
        carService.addDriverToCar(drNick, mustang);
        System.out.println(carService.get(mustang.getId()));

        System.out.println("Driver " + drNick.getName() + " binds with cars: ");
        System.out.println(carService.getAllByDriver(drNick.getId()));

        System.out.println(System.lineSeparator() + "Remove driver with id: "
                + drNick.getId() + " from car with id: " + mustang.getId());
        carService.removeDriverFromCar(drNick, mustang);
        carService.getAll().forEach(System.out::println);

        System.out.println("Update car with ID: " + mustang.getId());
        mustang.setModel("Mustang 3000");
        mustang.setManufacturer(mnFord);
        carService.update(mustang);
        carService.getAll().forEach(System.out::println);

        System.out.println("Delete car with ID: " + carola.getId());
        carService.delete(carola.getId());
        carService.getAll().forEach(System.out::println);
    }
}
