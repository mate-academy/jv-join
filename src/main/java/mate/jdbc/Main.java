package mate.jdbc;

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
        // test your code here
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        Manufacturer manufacturerCreateAudi = manufacturerService
                .create(new Manufacturer("audi","german1"));
        Manufacturer manufacturerCreateVW = manufacturerService
                .create(new Manufacturer("vw","german2"));
        Manufacturer manufacturerCreateShcoda = manufacturerService
                .create(new Manufacturer("Shcoda","german3"));

        Car carAudi = new Car("Audi", manufacturerCreateAudi);
        Car carShcoda = new Car("Shcoda", manufacturerCreateShcoda);
        Car carVw = new Car("Vw", manufacturerCreateVW);

        carService.create(carAudi);
        carService.create(carShcoda);
        carService.create(carVw);

        Driver driverNazar = new Driver("Nazar","23");
        Driver driverIvan = new Driver("Ivan","28");
        Driver driverPetro = new Driver("Petro","40");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverIvan);
        driverService.create(driverNazar);
        driverService.create(driverPetro);

        carService.addDriverToCar(driverIvan, carAudi);
        carService.addDriverToCar(driverNazar, carVw);
        carService.addDriverToCar(driverNazar, carAudi);
        carService.addDriverToCar(driverPetro, carShcoda);

        System.out.println(carService.getAll());
        carService.getAll().forEach(System.out::println);
    }
}
