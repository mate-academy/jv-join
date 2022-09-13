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
    private static final String MAIN_PACKAGE_NAME = "mate.jdbc";
    private static final Injector injector = Injector.getInstance(MAIN_PACKAGE_NAME);

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("Germany");
        manufacturerService.create(audi);

        Manufacturer bmw = new Manufacturer();
        bmw.setName("bmw");
        bmw.setCountry("Germany");
        manufacturerService.create(bmw);

        Manufacturer tesla = new Manufacturer();
        tesla.setName("Tesla");
        tesla.setCountry("USA");
        manufacturerService.create(tesla);

        Manufacturer ford = new Manufacturer();
        ford.setName("Ford");
        ford.setCountry("USA");
        manufacturerService.create(ford);

        Manufacturer renault = new Manufacturer();
        renault.setName("Renault");
        renault.setCountry("France");
        manufacturerService.create(renault);
        System.out.println("*************** Added: audi, bmw, tesla, ford, renault");

        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        System.out.println("*************** all Manufacturers from db was printed");

        audi.setName("Porsche");
        audi = manufacturerService.update(audi);
        System.out.println("Audi was changed to Porsche");

        System.out.println(manufacturerService.get(audi.getId()));
        System.out.println("*************** Manufacturer with id = "
                + audi.getId() + " was printed");

        manufacturerService.delete(renault.getId());
        System.out.println("Renault was deleted");

        manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        System.out.println("*************** all Manufacturers from db was printed");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver audiDriver = new Driver();
        audiDriver.setName("AudiDriver");
        audiDriver.setLicenseNumber("1");
        driverService.create(audiDriver);

        Driver bmwDriver = new Driver();
        bmwDriver.setName("bmwDriver");
        bmwDriver.setLicenseNumber("2");
        driverService.create(bmwDriver);

        Driver teslaDriver = new Driver();
        teslaDriver.setName("TeslaDriver");
        teslaDriver.setLicenseNumber("3");
        driverService.create(teslaDriver);

        Driver fordDriver = new Driver();
        fordDriver.setName("FordDriver");
        fordDriver.setLicenseNumber("4");
        driverService.create(fordDriver);

        Driver renaultDriver = new Driver();
        renaultDriver.setName("RenaultDriver");
        renaultDriver.setLicenseNumber("5");
        driverService.create(renaultDriver);
        System.out.println("*************** Added: audiDriver, bmwDriver, "
                + "teslaDriver, fordDriver, renaultDriver");

        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        System.out.println("*************** all drivers from db was printed");

        audiDriver.setName("PorscheDriver");
        audiDriver = driverService.update(audiDriver);
        System.out.println("AudiDriver was changed to PorscheDriver");

        System.out.println(driverService.get(audiDriver.getId()));
        System.out.println("*************** Driver with id = "
                + audiDriver.getId() + " was printed");

        driverService.delete(renaultDriver.getId());
        System.out.println("RenaultDriver was deleted");

        drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        System.out.println("*************** all drivers from db was printed");

        Car panamera = new Car();
        panamera.setManufacturer(audi);
        panamera.setModel("Panamera");
        panamera.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(panamera);

        Car focus = new Car();
        focus.setManufacturer(ford);
        focus.setModel("Focus");
        focus.setDrivers(new ArrayList<>());
        carService.create(focus);

        Car edge = new Car();
        edge.setManufacturer(ford);
        edge.setModel("Edge");
        edge.setDrivers(new ArrayList<>());
        carService.create(edge);

        System.out.println("*************** Cars panamera, focus and edge was added");

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("*************** all cars from db was printed");

        carService.addDriverToCar(audiDriver, focus);
        carService.removeDriverFromCar(audiDriver,panamera);
        System.out.println("audiDriver added  to focus and removed from panamera");

        System.out.println(carService.get(focus.getId()));
        System.out.println("*************** Car with id = "
                + focus.getId() + " was printed");

        driverService.delete(edge.getId());
        System.out.println("Edge was deleted");

        cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("*************** all cars from db was printed");

        cars = carService.getAllByDriver(audiDriver.getId());
        cars.forEach(System.out::println);
        System.out.println("*************** all cars by driver "
                + audiDriver.getId() + " was printed");
    }
}
