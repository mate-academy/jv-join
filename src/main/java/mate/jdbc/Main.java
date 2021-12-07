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
        Driver firstDriver = new Driver();
        firstDriver.setLicenseNumber("first");
        firstDriver.setName("Ivan");

        Driver secondDriver = new Driver();
        secondDriver.setLicenseNumber("second");
        secondDriver.setName("Alex");

        Driver thirdDriver = new Driver();
        thirdDriver.setLicenseNumber("third");
        thirdDriver.setName("Serg");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);

        Manufacturer manufacturerZaz = new Manufacturer();
        manufacturerZaz.setCountry("Ukraine");
        manufacturerZaz.setName("ZAZ");

        Manufacturer manufacturerDaewoo = new Manufacturer();
        manufacturerDaewoo.setCountry("Ukraine");
        manufacturerDaewoo.setName("Daewoo");

        Manufacturer manufacturerToyota = new Manufacturer();
        manufacturerToyota.setCountry("Toyota");
        manufacturerToyota.setName("Acura");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        manufacturerService.create(manufacturerToyota);
        manufacturerService.create(manufacturerZaz);
        manufacturerService.create(manufacturerDaewoo);

        List<Driver> car1Drivers = List.of(driverService.get(firstDriver.getId()),
                driverService.get(thirdDriver.getId()));

        Car car1 = new Car();
        car1.setModel("ToyotaAcura");
        car1.setManufacturer(manufacturerService.get(manufacturerToyota.getId()));
        car1.setDrivers(car1Drivers);

        List<Driver> car2Drivers = List.of(driverService.get(firstDriver.getId()),
                driverService.get(secondDriver.getId()), driverService.get(thirdDriver.getId()));

        Car car2 = new Car();
        car2.setModel("DaewooLanos");
        car2.setManufacturer(manufacturerService.get(manufacturerDaewoo.getId()));
        car2.setDrivers(car2Drivers);

        List<Driver> car3Drivers = List.of(driverService.get(thirdDriver.getId()));

        Car car3 = new Car();
        car3.setModel("Zaporozec");
        car3.setManufacturer(manufacturerService.get(manufacturerZaz.getId()));
        car3.setDrivers(car3Drivers);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);

        System.out.println(carService.get(car1.getId()));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(secondDriver.getId()).forEach(System.out::println);

        List<Driver> car3DriversNew = List.of(driverService.get(firstDriver.getId()),
                driverService.get(secondDriver.getId()), driverService.get(thirdDriver.getId()));

        Car car4 = new Car();
        car4.setModel("Zaporozec+++");
        car4.setManufacturer(manufacturerService.get(manufacturerDaewoo.getId()));
        car4.setDrivers(car3DriversNew);
        car4.setId(car3.getId());

        carService.delete(car4.getId());

        carService.get(car1.getId());
        System.out.println(carService.update(car4));

        System.out.println(carService.get(car1.getId()));
        carService.addDriverToCar(driverService.get(secondDriver.getId()),
                carService.get(car1.getId()));
        System.out.println(carService.get(car1.getId()));
        carService.removeDriverFromCar(driverService.get(secondDriver.getId()),
                carService.get(car1.getId()));
        System.out.println(carService.get(car1.getId()));
    }
}
