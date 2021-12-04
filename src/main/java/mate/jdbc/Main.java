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
        firstDriver.setId(15L);

        Driver secondDriver = new Driver();
        secondDriver.setLicenseNumber("second");
        secondDriver.setName("Alex");
        secondDriver.setId(16L);

        Driver thirdDriver = new Driver();
        thirdDriver.setLicenseNumber("third");
        thirdDriver.setName("Serg");
        thirdDriver.setId(17L);

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
        manufacturerToyota.setCountry("Japan");
        manufacturerToyota.setName("Toyota");
        manufacturerToyota.setId(17L);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        manufacturerService.create(manufacturerZaz);
        manufacturerService.create(manufacturerDaewoo);
        manufacturerService.create(manufacturerToyota);

        List<Driver> car1Drivers = List.of(firstDriver,thirdDriver);

        Car car1 = new Car();
        car1.setModel("ToyotaAcura");
        car1.setManufacturer(manufacturerToyota);
        car1.setDrivers(car1Drivers);

        List<Driver> car2Drivers = List.of(firstDriver,secondDriver, thirdDriver);

        Car car2 = new Car();
        car2.setModel("DaewooLanos");
        car2.setManufacturer(manufacturerDaewoo);
        car2.setDrivers(car2Drivers);

        List<Driver> car3Drivers = List.of(thirdDriver);

        Car car3 = new Car();
        car3.setModel("Zaporozec");
        car3.setManufacturer(manufacturerZaz);
        car3.setDrivers(car3Drivers);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        carService.create(car1);
        carService.create(car2);
        carService.create(car3);

        System.out.println(carService.get(2L));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(17L).forEach(System.out::println);
        carService.delete(2L);

        List<Driver> car3DriversNew = List.of(firstDriver);

        Car car4 = new Car();
        car4.setModel("Zaporozec+++");
        car4.setManufacturer(manufacturerToyota);
        car4.setDrivers(car3DriversNew);
        car4.setId(3L);

        System.out.println(carService.get(3L));
        carService.removeDriverFromCar(secondDriver, carService.get(3L));
        System.out.println(carService.get(3L));
    }
}
