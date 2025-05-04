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
        Driver romaDriver = driverService.create(new Driver("Roma", "371"));
        Driver andreyDriver = driverService.create(new Driver("Andrey", "894"));
        driverService.getAll().forEach(System.out::println);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(romaDriver);
        drivers.add(andreyDriver);

        Manufacturer toyotaManufacturer
                = manufacturerService.create(new Manufacturer("Toyota", "Japan"));
        Manufacturer fordManufacturer
                = manufacturerService.create(new Manufacturer("Ford", "USA"));
        manufacturerService.getAll().forEach(System.out::println);

        Car toyotaCar = new Car("Toyota Mark 2", toyotaManufacturer, drivers);
        Car fordCar = new Car("Ford Focus 2", fordManufacturer, new ArrayList<>());
        carService.create(toyotaCar);
        carService.create(fordCar);
        carService.getAll().forEach(System.out::println);

        System.out.println(carService.get(toyotaCar.getId()));

        carService.addDriverToCar(romaDriver, fordCar);
        System.out.println(carService.get(fordCar.getId()));
        System.out.println(carService.getAllByDriver(romaDriver.getId()));

        carService.removeDriverFromCar(romaDriver, toyotaCar);
        carService.getAll().forEach(System.out::println);

        Manufacturer renaultManufacturer
                = manufacturerService.create(new Manufacturer("Renault", "France"));
        toyotaCar.setModel("Toyota Corolla");
        toyotaCar.setManufacturer(renaultManufacturer);
        carService.update(toyotaCar);
        carService.getAll().forEach(System.out::println);

        carService.delete(toyotaCar.getId());
        carService.getAll().forEach(System.out::println);
    }
}
