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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverFirst = new Driver("Sasha", "12345");
        Driver driverSecond = new Driver("Petro", "67890");
        Driver driverThird = new Driver("Vasia", "34567");

        driverFirst = driverService.create(driverFirst);
        driverSecond = driverService.create(driverSecond);
        driverThird = driverService.create(driverThird);

        System.out.println(driverService.get(driverThird.getId()));

        driverService.getAll().forEach(System.out::println);

        driverFirst.setLicenseNumber("54321");
        System.out.println(driverService.update(driverFirst));

        System.out.println(driverService.delete(driverSecond.getId()));
        driverSecond = driverService.create(driverSecond);

        Manufacturer manufacturerFirst = new Manufacturer();
        manufacturerFirst.setName("First");
        manufacturerFirst.setCountry("First");

        Manufacturer manufacturerSecond = new Manufacturer();
        manufacturerSecond.setName("Second");
        manufacturerSecond.setCountry("Second");

        Manufacturer manufacturerThird = new Manufacturer();
        manufacturerThird.setName("Third");
        manufacturerThird.setCountry("Third");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerFirst = manufacturerService.create(manufacturerFirst);
        manufacturerSecond = manufacturerService.create(manufacturerSecond);
        manufacturerThird = manufacturerService.create(manufacturerThird);

        System.out.println(manufacturerService.get(manufacturerFirst.getId()));

        manufacturerService.getAll().forEach(System.out::println);

        manufacturerSecond.setName("newSecond");
        manufacturerSecond.setCountry("newSecond");
        System.out.println(manufacturerService.update(manufacturerSecond));

        manufacturerService.delete(manufacturerFirst.getId());
        manufacturerFirst = manufacturerService.create(manufacturerFirst);

        Car carFirst = new Car();
        carFirst.setModel("BMW");
        carFirst.setManufacturer(manufacturerFirst);
        List<Driver> driverListForFirstCar = new ArrayList<>();
        driverListForFirstCar.add(driverFirst);
        driverListForFirstCar.add(driverSecond);
        carFirst.setDriverList(driverListForFirstCar);

        Car carSecond = new Car();
        carSecond.setModel("Volvo");
        carSecond.setManufacturer(manufacturerSecond);
        List<Driver> driverListForSecondCar = new ArrayList<>();
        driverListForSecondCar.add(driverFirst);
        driverListForSecondCar.add(driverSecond);
        carSecond.setDriverList(driverListForSecondCar);

        Car carThird = new Car();
        carThird.setModel("Audi");
        carThird.setManufacturer(manufacturerThird);
        List<Driver> driverListForThirdCar = new ArrayList<>();
        driverListForThirdCar.add(driverFirst);
        driverListForThirdCar.add(driverSecond);
        carThird.setDriverList(driverListForThirdCar);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carFirst = carService.create(carFirst);
        carSecond = carService.create(carSecond);
        carThird = carService.create(carThird);

        System.out.println(carService.get(carFirst.getId()));

        carService.getAll().forEach(System.out::println);

        carSecond.setModel("Volvo1");
        System.out.println(carService.update(carSecond));

        carService.delete(carSecond.getId());

        Driver newDriver = new Driver("Paulo", "76543");
        newDriver = driverService.create(newDriver);
        carService.addDriverToCar(newDriver, carThird);
        carService.removeDriverFromCar(driverFirst, carThird);

        carService.getAllByDriver(driverSecond.getId());
    }
}
