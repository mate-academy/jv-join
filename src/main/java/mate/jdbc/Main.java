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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer mitsubishi = new Manufacturer("Mitsubishi", "Japan");
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(nissan);
        manufacturerService.create(mitsubishi);
        manufacturerService.create(bmw);
        manufacturerService.create(tesla);
        System.out.println("Manufacturers Nissan, BMW, Mitsubishi and Tesla were created...");
        System.out.println("---------------------------------------------------------------------");
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");
        Driver ilona = new Driver("Ilona", "123456");
        Driver jora = new Driver("Jora", "234567");
        Driver zoya = new Driver("Zoya", "345678");
        Driver kolya = new Driver("Kolya", "456789");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(ilona);
        driverService.create(jora);
        driverService.create(zoya);
        driverService.create(kolya);
        System.out.println("Drivers Ilona, Jora, Zoya and Kolya were created...");
        System.out.println("---------------------------------------------------------------------");
        driverService.getAll().forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");
        //crete Tesla modelS
        Car modelS = new Car();
        modelS.setModel("modelS");
        modelS.setManufacturer(tesla);
        List<Driver> modelSDrivers = new ArrayList<>();
        modelSDrivers.add(ilona);
        modelS.setDrivers(modelSDrivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(modelS);
        //create BMW 525
        Car bmw525 = new Car();
        bmw525.setModel("525");
        bmw525.setManufacturer(bmw);
        List<Driver> bmw525Drivers = new ArrayList<>();
        bmw525Drivers.add(jora);
        bmw525.setDrivers(bmw525Drivers);
        carService.create(bmw525);
        //create Mitsubishi L200
        Car l200 = new Car();
        l200.setModel("L200");
        l200.setManufacturer(mitsubishi);
        List<Driver> l200Drivers = new ArrayList<>();
        l200Drivers.add(zoya);
        l200.setDrivers(l200Drivers);
        carService.create(l200);
        //crete Nissan Patrol 2020
        Car patrol2020 = new Car();
        patrol2020.setModel("Patrol");
        patrol2020.setManufacturer(nissan);
        List<Driver> patrol2020Drivers = new ArrayList<>();
        patrol2020Drivers.add(zoya);
        patrol2020Drivers.add(kolya);
        patrol2020.setDrivers(patrol2020Drivers);
        carService.create(patrol2020);
        System.out.println("Cars Tesla modelS, BMW 525, Mitsubishi L200 "
                + "and Nissan Patrol were created...");
        System.out.println("---------------------------------------------------------------------");
        carService.getAll().forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Car by index 2 before update ");
        System.out.print("---------------------------------------------------------------------");
        System.out.println(System.lineSeparator() + carService.get(2L));
        Manufacturer ford = new Manufacturer("Ford", "Germany");
        manufacturerService.create(ford);
        Driver bill = new Driver("Bill", "555555");
        driverService.create(bill);
        //crete Ford Focus
        Car focus = new Car("Focus", ford);
        List<Driver> focusDrivers = new ArrayList<>();
        focusDrivers.add(bill);
        focus.setDrivers(focusDrivers);
        focus.setId(2L);
        carService.update(focus);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Car by index 2 after update");
        System.out.print("---------------------------------------------------------------------");
        System.out.println(System.lineSeparator() + carService.get(2L));
        System.out.println("---------------------------------------------------------------------");
        Driver bob = new Driver("Bob", "777777");
        driverService.create(bob);
        carService.addDriverToCar(bob, focus);
        System.out.println("Car by index 2 after add one driver Bob");
        System.out.print("---------------------------------------------------------------------");
        System.out.println(System.lineSeparator() + carService.get(2L));
        System.out.println("---------------------------------------------------------------------");
        carService.removeDriverFromCar(bill, focus);
        System.out.println("Car by index 2 after remove driver Bill ");
        System.out.print("---------------------------------------------------------------------");
        System.out.println(System.lineSeparator() + carService.get(2L));
        System.out.print("---------------------------------------------------------------------");
    }
}
