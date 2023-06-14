package mate.jdbc;

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
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("bmw");
        manufacturer1.setCountry("germany");
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("fiat");
        manufacturer2.setCountry("italy");

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);

        Car car1 = new Car();
        car1.setManufacturer(manufacturer1);
        car1.setModel("420d");
        Car car2 = new Car();
        car2.setManufacturer(manufacturer2);
        car2.setModel("500X");

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(car1);
        carService.create(car2);

        Driver bob = new Driver("bob", "b123");
        Driver alex = new Driver("alex", "a456");
        Driver john = new Driver("john", "j789");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        driverService.create(bob);
        driverService.create(alex);
        driverService.create(john);

        carService.addDriverToCar(bob, car1);
        carService.addDriverToCar(alex, car1);
        carService.addDriverToCar(alex, car2);
        carService.addDriverToCar(john, car2);

        carService.getAllByDriver(alex.getId()).forEach(System.out::println);

        carService.removeDriverFromCar(alex, car1);
    }
}
