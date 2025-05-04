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
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {

        Driver driverOne = new Driver();
        driverOne.setName("Vadym");
        driverOne.setLicenseNumber("PL345664434");
        driverOne = driverService.create(driverOne);
        Driver driverTwo = new Driver();
        driverTwo.setName("Sviatoslav");
        driverTwo.setLicenseNumber("NeyroNet343535665");
        driverTwo = driverService.create(driverTwo);
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setName("BMW");
        bmwManufacturer.setCountry("Germany");
        bmwManufacturer = manufacturerService.create(bmwManufacturer);
        Manufacturer mazdaManufacturer = new Manufacturer();
        mazdaManufacturer.setName("Mazda");
        mazdaManufacturer.setCountry("USA");
        mazdaManufacturer = manufacturerService.create(mazdaManufacturer);

        Car carBmw = new Car();
        carBmw.setModel("AnyModel");
        carBmw.setManufacturer(bmwManufacturer);
        List<Driver> driversBmw = new ArrayList<>();
        driversBmw.add(driverOne);
        carBmw.setDrivers(driversBmw);
        carBmw = carService.create(carBmw);
        System.out.println(carBmw);
        carBmw.setModel("M5");
        carBmw = carService.update(carBmw);
        System.out.println(carBmw);

        Car carMazda = new Car();
        carMazda.setModel("CX-7");
        carMazda.setManufacturer(mazdaManufacturer);
        List<Driver> driversMazda = new ArrayList<>();
        driversMazda.add(driverOne);
        driversMazda.add(driverTwo);
        carMazda.setDrivers(driversMazda);

        carMazda = carService.create(carMazda);
        boolean wasDeleted = carService.delete(carBmw.getId());
        System.out.println("Deleted " + carBmw.getModel() + " " + wasDeleted);
        carService.getAll().forEach(System.out::println);
        System.out.println("Driver " + driverOne);
        carService.getAllByDriver(driverOne.getId()).forEach(System.out::println);
        System.out.println();
        carService.addDriverToCar(driverTwo, carMazda);
        System.out.println(carService.get(carMazda.getId()));
        carService.removeDriverFromCar(driverTwo, carMazda);
        System.out.println(carService.get(carMazda.getId()));
    }
}

