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
        /**
         * Create all drivers;
         */
        Driver lena = new Driver();
        lena.setLicenseNumber("123_853_3382");
        lena.setName("Lena");
        Driver anna = new Driver();
        anna.setLicenseNumber("332_823_1242");
        anna.setName("Anna");
        Driver gala = new Driver();
        gala.setLicenseNumber("537_234_8763");
        gala.setName("Gala");
        Driver vlad = new Driver();
        vlad.setLicenseNumber("537_234_8763");
        vlad.setName("Vlad");
        Driver valera = new Driver();
        valera.setLicenseNumber("537_234_8763");
        valera.setName("Valera");
        /**
         * create all manufacture;
         */
        Manufacturer toyota = new Manufacturer();
        toyota.setName("Toyota");
        toyota.setCountry("Japan");
        Manufacturer tesla = new Manufacturer();
        tesla.setName("Tesla");
        tesla.setCountry("Usa");
        Manufacturer audi = new Manufacturer();
        audi.setName("Audi");
        audi.setCountry("germany");
        Manufacturer volvo = new Manufacturer();
        volvo.setName("Volvo");
        volvo.setCountry("Sweden");
        /**
         * Create all cars:
         */
        Car camry = new Car();
        camry.setModel("Camry");
        camry.setManufacturer(toyota);
        List<Driver> camryDrivers = new ArrayList<>();
        camryDrivers.add(vlad);
        camryDrivers.add(gala);
        camryDrivers.add(lena);
        camry.setDrivers(camryDrivers);
        Car modelX = new Car();
        modelX.setModel("Model_X");
        modelX.setManufacturer(tesla);
        List<Driver> modelXDrivers = new ArrayList<>();
        modelXDrivers.add(valera);
        modelXDrivers.add(anna);
        modelXDrivers.add(gala);
        modelX.setDrivers(modelXDrivers);
        Car a3 = new Car();
        a3.setModel("A3");
        a3.setManufacturer(audi);
        List<Driver> a3Drivers = new ArrayList<>();
        a3Drivers.add(vlad);
        a3Drivers.add(lena);
        a3Drivers.add(gala);
        a3.setDrivers(a3Drivers);
        Car v90 = new Car();
        v90.setModel("V90");
        v90.setManufacturer(volvo);
        List<Driver> v90Drivers = new ArrayList<>();
        v90Drivers.add(lena);
        v90Drivers.add(gala);
        v90Drivers.add(valera);
        v90Drivers.add(anna);
        v90Drivers.add(vlad);
        v90.setDrivers(v90Drivers);
        /**
         * Add manufacture to database;
         */
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(toyota);
        manufacturerService.create(tesla);
        manufacturerService.create(audi);
        manufacturerService.create(volvo);
        /**
         * Add drivers to database;
         */
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(lena);
        driverService.create(anna);
        driverService.create(vlad);
        driverService.create(gala);
        driverService.create(valera);
        /**
         * Add cars to database by method 'create',
         * and check it with method 'getAll';
         */
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(camry);
        carService.create(modelX);
        carService.create(a3);
        carService.create(v90);
        System.out.println("Get all cars from database after added");
        for (Car car:carService.getAll()) {
            System.out.println(car);
        }
        System.out.println(System.lineSeparator());
        /**
         * Get one car by id
         */
        System.out.println("Get one car by id " + modelX.getId());
        System.out.println(carService.get(modelX.getId()));
        System.out.println(System.lineSeparator());
        /**
         * Update data 'car' by id using method update,
         * and check it with method 'get';
         */
        System.out.println("Create new 'car' with exist id "
                + a3.getId()
                + ". after update data check data by this id.");
        Car cyberTruck = new Car();
        cyberTruck.setId(a3.getId());
        cyberTruck.setModel("Cyber_Truck");
        cyberTruck.setManufacturer(tesla);
        cyberTruck.setDrivers(modelX.getDrivers());
        carService.update(cyberTruck);
        System.out.println(carService.get(a3.getId()));
        System.out.println(System.lineSeparator());
        /**
         * Remove 'car' from database
         * by id using method 'delete',
         * and check it using 'getAll';
         */
        System.out.println("Remove 'car' from database by id "
                + "using method 'delete',and check it using 'getAll'.");
        carService.delete(v90.getId());
        for (Car car:carService.getAll()) {
            System.out.println(car);
        }
        System.out.println(System.lineSeparator());
        /**
         * Get all 'cars' which 'driver' can use
         */
        System.out.println("Get all cars which driver "
                + anna + " can use");
        carService.getAllByDriver(anna.getId());
        for (Car car:carService.getAllByDriver(anna.getId())) {
            System.out.println(car);
        }
        System.out.println(System.lineSeparator());
        /**
         * Add 'driver' to list drivers in this 'car',
         * using method 'addDriverToCar'.
         */
        System.out.println("Add 'driver' "
                + valera + " to list drivers in this 'car' "
                + camry + ", using method 'addDriverToCar'");
        System.out.println(carService.get(camry.getId()));
        carService.addDriverToCar(valera,camry);
        System.out.println(carService.get(camry.getId()));
        System.out.println(System.lineSeparator());
        /**
         * Remove 'driver' from list drivers in this 'car',
         * using method 'removeDriverFromCar'.
         */
        System.out.println("Remove 'driver'"
                + vlad + " from list drivers in this 'car' "
                + camry + ", using method 'removeDriverFromCar'.");
        System.out.println(carService.get(camry.getId()));
        carService.removeDriverFromCar(vlad,camry);
        System.out.println(carService.get(camry.getId()));
    }
}
