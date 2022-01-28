package mate.jdbc;

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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer zazUkraine = new Manufacturer("ZAZ", "Ukraine");
        manufacturerService.create(zazUkraine);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);
        List<Manufacturer> manufacturerList = manufacturerService.getAll();
        manufacturerList.forEach(System.out::println);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver bart = new Driver("Bart Simpson", "dafaomx1");
        driverService.create(bart);
        Driver lisa = new Driver("Lisa Simpson", "mcqpm1m");
        driverService.create(lisa);
        Driver homer = new Driver("Homer Simpson", "mcian3");
        driverService.create(homer);
        List<Driver> driverList = driverService.getAll();
        driverList.forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car teslaByZaz = new Car("TeslaX30", zazUkraine);
        teslaByZaz.setDriverList(driverList);
        teslaByZaz = carService.create(teslaByZaz);
        carService.getAll().forEach(System.out::println);

        teslaByZaz.getDriverList().add(homer);

        carService.addDriverToCar(bart, teslaByZaz);
        carService.addDriverToCar(lisa, teslaByZaz);
        carService.getAll().forEach(System.out::println);

        teslaByZaz.setModel("Tesla2000");
        carService.update(teslaByZaz);
        System.out.println(carService.get(teslaByZaz.getId()));
        carService.removeDriverFromCar(bart, teslaByZaz);
        carService.getAllByDriver(lisa.getId()).forEach(System.out::println);
        carService.delete(teslaByZaz.getId());
    }
}

