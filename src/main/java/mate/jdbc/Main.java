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
        Manufacturer mercedes = new Manufacturer();
        mercedes.setName("Mercedes");
        mercedes.setCountry("Germany");

        Manufacturer lada = new Manufacturer();
        lada.setName("Lada");
        lada.setCountry("Russia");

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("DL#24151");

        Driver bill = new Driver();
        bill.setName("Bill");
        bill.setLicenseNumber("DL#5123b");

        List<Driver> drivers = new ArrayList<>();
        drivers.add(bill);
        drivers.add(bob);

        Car w201 = new Car();
        w201.setModel("W201");
        w201.setManufacturer(mercedes);
        w201.setDriver(drivers);

        Car priora = new Car();
        priora.setModel("W201");
        priora.setManufacturer(mercedes);
        priora.setDriver(drivers);

        ManufacturerService manufactureService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        //C
        Manufacturer mercedesManufacturer = manufactureService.create(mercedes);
        Manufacturer ladaManufacturer = manufactureService.create(lada);
        System.out.println("Create manufacturers: " + mercedesManufacturer
                + " " + ladaManufacturer);

        Driver bobDriver = driverService.create(bob);
        Driver billDriver = driverService.create(bill);
        System.out.println("Create drivers: " + bobDriver + " " + billDriver);

        Car mercedesCar = carService.create(w201);
        carService.addDriverToCar(bob,w201);
        carService.removeDriverFromCar(bill,w201);
        System.out.println("Create car: " + mercedesCar);

        Car ladaCar = carService.create(priora);
        carService.addDriverToCar(bill,priora);
        carService.removeDriverFromCar(bob,priora);
        System.out.println("Create car: " + priora);
        //R
        List<Manufacturer> allManufacturers = manufactureService.getAll();
        System.out.println("Get all manufacturers from DB: " + allManufacturers);
        Manufacturer getLada = manufactureService.get(lada.getId());
        System.out.println(getLada);

        List<Driver> allDrivers = driverService.getAll();
        System.out.println("Get all drivers from DB: " + allDrivers);
        Driver getBill = driverService.get(bill.getId());
        System.out.println(getBill);

        List<Car> allCar = carService.getAll();
        System.out.println("Get all car from DB: " + allCar);
        Car getCar = carService.get(w201.getId());

        Long driverId = bill.getId();
        List<Car> allByDriver = carService.getAllByDriver(driverId);
        System.out.println(allByDriver);
        //U
        mercedes.setCountry("Ukraine");
        Manufacturer updateMercedes = manufactureService.update(mercedes);
        System.out.println("Update data to DB: " + updateMercedes);

        bob.setLicenseNumber("#$2512agS");
        Driver updateBob = driverService.update(bob);
        System.out.println("Update data to DB: " + updateBob);

        priora.setModel("2108");
        Car updateLada = carService.update(priora);
        System.out.println("Update car to DB: " + priora);
        //D
        boolean deletedManufacturer = manufactureService.delete(mercedes.getId());
        System.out.println(deletedManufacturer);

        boolean deletedDriver = driverService.delete(bob.getId());
        System.out.println(deletedDriver);

        boolean deletedPriora = carService.delete(priora.getId());
        System.out.println(deletedPriora);
    }
}
