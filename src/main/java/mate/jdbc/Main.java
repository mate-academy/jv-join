package mate.jdbc;

import java.util.ArrayList;
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
        Car firstCar = new Car();
        firstCar.setModel("Kona");
        firstCar.setDrivers(new ArrayList<>());

        Car secondCar = new Car();
        secondCar.setModel("Focus");
        secondCar.setDrivers(new ArrayList<>());

        Driver driver1 = new Driver();
        driver1.setName("Danny");
        driver1.setLicenseNumber("123");

        Driver driver2 = new Driver();
        driver2.setName("Bob");
        driver2.setLicenseNumber("111");

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("Hyundai");
        manufacturer1.setCountry("South Korea");
        Manufacturer hyundaiManufacturer = manufacturerService.create(manufacturer1);
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("Ford");
        manufacturer2.setCountry("USA");
        Manufacturer fordManufacturer = manufacturerService.create(manufacturer2);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        firstCar.setManufacturer(hyundaiManufacturer);
        secondCar.setManufacturer(fordManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverDanny = driverService.create(driver1);
        Driver driverBob = driverService.create(driver2);

        carService.addDriverToCar(driverDanny, firstCar);
        carService.addDriverToCar(driverBob, firstCar);
        carService.removeDriverFromCar(driverBob, firstCar);
        Car hundaiKona = carService.create(firstCar);
        System.out.println(carService.get(hundaiKona.getId()));

        carService.addDriverToCar(driverDanny, secondCar);
        carService.addDriverToCar(driverBob, secondCar);
        Car fordFocus = carService.create(secondCar);
        System.out.println(carService.get(fordFocus.getId()));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driverDanny.getId()).forEach(System.out::println);

        carService.addDriverToCar(driverBob, hundaiKona);
        hundaiKona.setModel("Elantra");
        carService.update(hundaiKona);
        carService.getAllByDriver(driverBob.getId()).forEach(System.out::println);

        carService.delete(fordFocus.getId());
        carService.getAll().forEach(System.out::println);
    }
}
