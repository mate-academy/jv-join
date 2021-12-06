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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer ferrari = new Manufacturer();
        ferrari.setCountry("Italy");
        ferrari.setName("Ferrari");
        manufacturerService.create(ferrari);

        Manufacturer lada = new Manufacturer();
        lada.setCountry("USSR");
        lada.setName("Lada");
        manufacturerService.create(lada);

        Manufacturer mercedes = new Manufacturer();
        mercedes.setCountry("Germany");
        mercedes.setName("Mercedes");
        manufacturerService.create(mercedes);

        manufacturerService.getAll().forEach(System.out::println);

        lada.setCountry("Ukraine");
        manufacturerService.update(lada);

        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver paul = new Driver();
        paul.setName("Paul");
        paul.setLicenseNumber("1");
        driverService.create(paul);

        Driver andrew = new Driver();
        andrew.setName("Andrew");
        andrew.setLicenseNumber("2");
        driverService.create(andrew);

        Driver eugene = new Driver();
        eugene.setName("Eugene");
        eugene.setLicenseNumber("3");
        driverService.create(eugene);

        Driver alexis = new Driver();
        alexis.setName("Sanyok");
        alexis.setLicenseNumber("4");
        driverService.create(alexis);

        Driver illya = new Driver();
        illya.setName("Illya");
        illya.setLicenseNumber("5");
        driverService.create(illya);

        Driver kate = new Driver();
        kate.setName("Kate");
        kate.setLicenseNumber("6");
        driverService.create(kate);

        System.out.println(driverService.get(2L));
        driverService.getAll().forEach(System.out::println);

        eugene.setLicenseNumber("777");
        driverService.update(eugene);

        driverService.getAll().forEach(System.out::println);

        List<Driver> zhigulDrivers = new ArrayList<>();
        zhigulDrivers.add(eugene);
        zhigulDrivers.add(andrew);
        zhigulDrivers.add(kate);

        List<Driver> ferrariDrivers = new ArrayList<>();
        ferrariDrivers.add(paul);
        ferrariDrivers.add(kate);
        ferrariDrivers.add(alexis);

        List<Driver> mercedesDrivers = new ArrayList<>();
        mercedesDrivers.add(eugene);
        mercedesDrivers.add(andrew);
        mercedesDrivers.add(illya);

        Car zhigulCar = new Car();
        zhigulCar.setManufacturer(lada);
        zhigulCar.setModel("Pyaterochka");
        zhigulCar.setDrivers(zhigulDrivers);

        Car ferrariCar = new Car();
        ferrariCar.setModel("SuperDuperFast");
        ferrariCar.setManufacturer(ferrari);
        ferrariCar.setDrivers(ferrariDrivers);

        Car mercedesCar = new Car();
        mercedesCar.setModel("Geländewagen");
        mercedesCar.setManufacturer(mercedes);
        mercedesCar.setDrivers(mercedesDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(zhigulCar);
        carService.create(ferrariCar);
        carService.create(mercedesCar);

        carService.getAll().forEach(System.out::println);

        zhigulCar.setModel("Kopeika");
        carService.update(zhigulCar);

        carService.delete(zhigulCar.getId());
        carService.removeDriverFromCar(eugene, mercedesCar);
        carService.addDriverToCar(paul, mercedesCar);
        carService.update(mercedesCar);

        carService.getAllByDriver(paul.getId());

        carService.getAll().forEach(System.out::println);

    }
}
