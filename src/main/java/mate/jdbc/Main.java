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
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerToyota);

        Manufacturer manufacturerMitsubishi = new Manufacturer("Mitsubishi", "Japan");
        manufacturerService.create(manufacturerMitsubishi);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverAndrii = new Driver("Andrii", "324324");
        Driver driverOleh = new Driver("Oleh", "435354");
        Driver driverYevhenii = new Driver("Yevhenii", "54645");
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driverAndrii);
        driverList.add(driverOleh);
        driverList.add(driverYevhenii);
        driverList.forEach(driverService::create);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car hondaCar = new Car("honda", manufacturerToyota, driverList);
        System.out.println(carService.create(hondaCar));

        Car suzukiCar = new Car("BMW", manufacturerMitsubishi, driverList);
        System.out.println(carService.create(suzukiCar));

        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(20L));
        System.out.println(carService.delete(22L));

        System.out.println(carService.getAllByDriver(15L));

        Driver driverIvan = new Driver("Ivan", "325235");

        driverService.create(driverIvan);
        carService.removeDriverFromCar(driverAndrii, hondaCar);
        carService.addDriverToCar(driverIvan, hondaCar);
        System.out.println(carService.update(hondaCar));
    }
}
