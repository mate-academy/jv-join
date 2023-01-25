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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);

        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("Opel");
        manufacturer1.setCountry("Germany");
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("Ford");
        manufacturer2.setCountry("USA");
        Manufacturer manufacturer3 = new Manufacturer();
        manufacturer3.setName("Fiat");
        manufacturer3.setCountry("Italy");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);

        Driver driver1 = new Driver();
        driver1.setName("Lucky");
        driver1.setLicenseNumber("777");
        Driver driver2 = new Driver();
        driver2.setName("Shumaher");
        driver2.setLicenseNumber("111");
        Driver driver3 = new Driver();
        driver3.setName("Dark");
        driver3.setLicenseNumber("999");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);

        Car car1 = new Car();
        car1.setModel("Vectra");
        car1.setManufacturer(manufacturer1);
        car1.setDrivers(List.of(driver1, driver2));
        Car car2 = new Car();
        car2.setModel("Focus");
        car2.setManufacturer(manufacturer2);
        car2.setDrivers(List.of(driver2, driver3));
        Car car3 = new Car();
        car3.setModel("Punto");
        car3.setManufacturer(manufacturer1);
        car3.setDrivers(List.of(driver3, driver1));
        carService.create(car1);
        carService.create(car2);
        carService.create(car3);

        System.out.println(carService.get(car1.getId()));
        System.out.println(carService.get(car2.getId()));
        carService.getAll().forEach(System.out::println);

        manufacturer1.setName("Renault");
        manufacturer1.setCountry("France");
        car1.setModel("Laguna");
        car1.setManufacturer(manufacturer1);
        car1.setDrivers(List.of(driver1, driver2, driver3));
        carService.update(car1);
        carService.delete(car3.getId());

        Driver driver4 = new Driver();
        driver4.setName("Newby");
        driver4.setLicenseNumber("000");
        driverService.create(driver4);
        carService.addDriverToCar(driver4, car2);
        carService.removeDriverFromCar(driver1, car1);

        System.out.println(carService.getAllByDriver(driver1.getId()));
        System.out.println(carService.getAllByDriver(driver2.getId()));
    }
}
