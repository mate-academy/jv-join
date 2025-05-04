package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
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
        System.out.println("--------------Test create car----------------");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Lada");
        manufacturer.setCountry("Ukraine");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);
        Driver driverFirst = new Driver();
        driverFirst.setName("Andriy");
        driverFirst.setLicenseNumber("OK-6574");
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverFirst);
        Driver driverSecond = new Driver();
        driverSecond.setName("Bohdan");
        driverSecond.setLicenseNumber("CK-8134");
        driverService.create(driverSecond);
        Driver driverThird = new Driver();
        driverThird.setName("Lisa");
        driverThird.setLicenseNumber("BM-1634");
        driverService.create(driverThird);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("Nubira");
        car.setDrivers(new ArrayList<>(Arrays.asList(driverFirst, driverSecond, driverThird)));
        System.out.println(car);
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(car);
        System.out.println("--------------Test get car by----------------");
        System.out.println(carService.get(car.getId()));
        System.out.println("--------------Test update car----------------");
        Car carFirst = new Car();
        carFirst.setModel("Corolla");
        Manufacturer manufacturerFirst = new Manufacturer();
        manufacturerFirst.setName("Toyota");
        manufacturerFirst.setCountry("Japan");
        carFirst.setManufacturer(
                manufacturerService.get(
                        manufacturerService.create(manufacturerFirst).getId()));
        Driver driverFourth = new Driver();
        driverFourth.setName("Petro");
        driverFourth.setLicenseNumber("VN-7491");
        driverService.create(driverFourth);
        carFirst.setDrivers(new ArrayList<>(Arrays.asList(driverFirst, driverSecond,
                driverThird, driverFourth)));
        System.out.println(carFirst);
        carService.create(carFirst);
        carService.get(carFirst.getId());
        carFirst.setModel("Brinson");
        Driver driverFifth = new Driver();
        driverFifth.setName("Mark");
        driverFifth.setLicenseNumber("MB-8246");
        driverService.create(driverFifth);
        List<Driver> drivers = carFirst.getDrivers();
        drivers.add(driverFifth);
        carFirst.setDrivers(drivers);
        carService.update(car);
        System.out.println("--------------Test get all cars----------------");
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test get all cars by driver----------------");
        System.out.println(carService.getAllByDriver(driverThird.getId()));
        System.out.println("--------------Test delete car----------------");
        carService.delete(carFirst.getId());
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test add new driver to car----------------");
        carService.getAll().forEach(System.out::println);
        Driver driverSixth = new Driver();
        driverSixth.setName("Natalia");
        driverSixth.setLicenseNumber("YU-8453");
        driverService.create(driverSixth);
        carService.addDriverToCar(driverSixth, car);
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test remove driver to car----------------");
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driverFourth, car);
        carService.getAll().forEach(System.out::println);
    }
}
