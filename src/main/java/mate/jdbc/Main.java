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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        List<Driver> driversList = List.of(
                new Driver("Michael", "SC123456"),
                new Driver("Catherine", "AM654321"),
                new Driver("Janusz", "RP654841")
        );
        driversList.forEach(driver -> {
            driverService.create(driver);
            System.out.println(driver);
        });
        driverService.getAll().forEach(System.out::println);
        System.out.println(driverService.get(driversList.get(1).getId()));
        driversList.get(1).setLicenseNumber("SU132465");
        System.out.println(driverService.update(driversList.get(1)));
        System.out.println(driverService.get(driversList.get(2).getId()));
        System.out.println(driverService.delete(driversList.get(1).getId()));
        driverService.getAll().forEach(System.out::println);

        List<Manufacturer> manufacturersList = List.of(
                new Manufacturer("Toyota", "Japan"),
                new Manufacturer("Buick", "USA"),
                new Manufacturer("Aston Martin", "UK"),
                new Manufacturer("Tesla", "USA"),
                new Manufacturer("Porsche", "Germany"),
                new Manufacturer("Bugatti", "France"),
                new Manufacturer("Pagani Automobili", "Italy"),
                new Manufacturer("Seat", "Spain")
        );
        manufacturersList.forEach(manufacturer -> {
            manufacturerService.create(manufacturer);
            System.out.println(manufacturer);
        });
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println(manufacturerService
                .get(manufacturersList.get(1).getId()));
        manufacturersList.get(6).setName("Maserati");
        System.out.println(manufacturerService
                .update(manufacturersList.get(6)));
        System.out.println(manufacturerService
                .get(manufacturersList.get(6).getId()));
        System.out.println(manufacturerService
                .delete(manufacturersList.get(5).getId()));
        manufacturerService.getAll().forEach(System.out::println);

        List<Car> carsList = List.of(
                new Car("Supra", manufacturersList.get(0), driversList.get(0)),
                new Car("Enclave", manufacturersList.get(1), driversList.get(1)),
                new Car("DBS 770 Ultimate", manufacturersList.get(2), driversList.get(2)),
                new Car("Model S", manufacturersList.get(3), driversList.get(0)),
                new Car("718 Spyder", manufacturersList.get(4), driversList.get(1)),
                new Car("Ghibli", manufacturersList.get(6), driversList.get(2)),
                new Car("Leon", manufacturersList.get(7), driversList.get(0))
        );
        carsList.forEach(car -> {
            carService.create(car);
            System.out.println(car);
        });
        carService.getAll().forEach(System.out::println);
        System.out.println(carService
                .get(carsList.get(1).getId()));
        carsList.get(0).setDrivers(List.of(driversList.get(0)));
        carService.addDriverToCar(driversList.get(1),carsList.get(1));
        carService.update(carsList.get(0));
        driversList.forEach(driver -> carService.addDriverToCar(driver, carsList.get(2)));
        carService.update(carsList.get(2));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driversList.get(1).getId()).forEach(System.out::println);
        carService.removeDriverFromCar(driversList.get(0), carsList.get(2));
        carService.update(carsList.get(2));
        System.out.println(carService.get(carsList.get(2).getId()));
        carService.delete(carsList.get(2).getId());
        carService.update(carsList.get(2));
        carService.getAll().forEach(System.out::println);
    }
}
