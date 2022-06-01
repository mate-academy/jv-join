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
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        Manufacturer manufacturerTesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerTesla);

        Driver driverValera = new Driver("Valera", "123456789");
        Driver driverIvan = new Driver("Ivan", "987654321");
        Driver driverOleg = new Driver("Oleg", "777777777");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverValera);
        driverService.create(driverIvan);
        driverService.create(driverOleg);

        List<Driver> driverListToyota = new ArrayList<>();
        List<Driver> driverListTesla = new ArrayList<>();

        driverListToyota.add(driverValera);
        driverListToyota.add(driverOleg);
        driverListTesla.add(driverIvan);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carModelToyota = new Car("Prius", manufacturerToyota, driverListToyota);
        Car carModelTesla = new Car("Model S", manufacturerTesla, driverListTesla);
        carService.create(carModelToyota);
        carService.create(carModelTesla);

        carModelToyota.setModel("Avalon");
        carService.update(carModelToyota);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverOleg,carModelToyota);
        carService.getAllByDriver(driverValera.getId()).forEach(System.out::println);

        carService.delete(carModelTesla.getId());

    }
}
