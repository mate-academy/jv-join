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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverJim = new Driver("Jim", "911");
        Driver driverBob = new Driver("Bob", "102");
        Driver driverPetro = new Driver("Petro", "777");

        driverJim = driverService.create(driverJim);
        driverBob = driverService.create(driverBob);
        driverPetro = driverService.create(driverPetro);

        driverJim.setLicenseNumber("111");
        System.out.println(driverService.update(driverJim));

        System.out.println(driverService.get(driverPetro.getId()));
        System.out.println(driverService.getAll());
        driverJim.setLicenseNumber("666");
        System.out.println(driverService.update(driverJim));

        Manufacturer manufacturerFirst = new Manufacturer();
        manufacturerFirst.setName("First");
        manufacturerFirst.setCountry("First");

        Manufacturer manufacturerSecond = new Manufacturer();
        manufacturerSecond.setName("Second");
        manufacturerSecond.setCountry("Second");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerFirst = manufacturerService.create(manufacturerFirst);
        manufacturerSecond = manufacturerService.create(manufacturerSecond);

        System.out.println(manufacturerService.getAll());

        manufacturerSecond.setName("newSecond");
        manufacturerSecond.setCountry("newSecond");
        System.out.println(manufacturerService.update(manufacturerSecond));

        manufacturerService.delete(manufacturerFirst.getId());
        manufacturerFirst = manufacturerService.create(manufacturerFirst);

        Car carAudi = new Car();
        carAudi.setModel("Audi");
        carAudi.setManufacturer(manufacturerFirst);
        List<Driver> driverListForFirstCar = new ArrayList<>();
        driverListForFirstCar.add(driverJim);
        driverListForFirstCar.add(driverBob);
        carAudi.setDrivers(driverListForFirstCar);

        Car carVolvo = new Car();
        carVolvo.setModel("Volvo");
        carVolvo.setManufacturer(manufacturerSecond);
        List<Driver> driverListForSecondCar = new ArrayList<>();
        driverListForSecondCar.add(driverJim);
        driverListForSecondCar.add(driverBob);
        carVolvo.setDrivers(driverListForSecondCar);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carAudi = carService.create(carAudi);
        carVolvo = carService.create(carVolvo);

        System.out.println(carService.get(carAudi.getId()));

        System.out.println(carService.getAll());

        carVolvo.setModel("Turbo");
        System.out.println(carService.update(carVolvo));

        carService.delete(carVolvo.getId());

        Driver newDriver = new Driver("Hidja", "988");
        newDriver = driverService.create(newDriver);
        carService.addDriverToCar(newDriver, carVolvo);
        carService.removeDriverFromCar(driverJim, carVolvo);

        carService.getAllByDriver(driverBob.getId());
    }
}
