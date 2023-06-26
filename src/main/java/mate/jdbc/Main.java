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
        System.out.println("App.start");
        System.out.println("<-----------TEST DRIVER SERVICE----------------->");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.getAll().forEach(System.out::println);
        Driver firstDriver = new Driver("Іванов Іван Іванович","3222233322");
        System.out.print(firstDriver + " -> ");
        Driver testDriver = driverService.create(firstDriver);
        System.out.println(testDriver);
        System.out.println(driverService.get(testDriver.getId()));
        Driver secondDriver = new Driver("Мазепа Т.П.","777");
        testDriver = driverService.create(secondDriver);
        System.out.print(secondDriver + " -> ");
        System.out.println(driverService.get(testDriver.getId()));
        System.out.print("Now update it to -> ");
        Driver updatebleDriver = testDriver;
        updatebleDriver.setName("П.Т. Мазепа");
        driverService.update(updatebleDriver);
        System.out.println(driverService.get(updatebleDriver.getId()));
        System.out.println("\nNow delete Driver id = "
                + testDriver.getId() + "  from DB ");
        if (driverService.delete(testDriver.getId())) {
            System.out.println("Driver id = " + testDriver.getId()
                    + " deleted successfully");
        }
        driverService.getAll().forEach(System.out::println);
        System.out.println("<----------TEST MANUFACTURER SERVICE------------------>");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        Manufacturer firstInputManufacturer = new Manufacturer("IBM2","USA");
        Manufacturer testManufacturer = manufacturerService.create(firstInputManufacturer);
        System.out.print("\n" + firstInputManufacturer + " -> ");
        System.out.println(manufacturerService.get(testManufacturer.getId()));
        Manufacturer secondInputManufacturer = new Manufacturer("BMV2","Germany");
        testManufacturer = manufacturerService.create(secondInputManufacturer);
        System.out.print(secondInputManufacturer + " -> ");
        System.out.println(manufacturerService.get(testManufacturer.getId()));
        System.out.print("Now update it to -> ");
        Manufacturer updatebleManufacturer = testManufacturer;
        updatebleManufacturer.setName("VW");
        manufacturerService.update(updatebleManufacturer);
        System.out.println(manufacturerService.get(updatebleManufacturer.getId()));
        System.out.println("\nNow delete Manufacturer id = "
                + testManufacturer.getId() + "  from DB ");
        if (manufacturerService.delete(testManufacturer.getId())) {
            System.out.println("Manufacturer id = " + testManufacturer.getId()
                    + " deleted successfully");
        }
        manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        System.out.println("<--------TEST CAR SERVICE-------------------->");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        Manufacturer newManufacturer = firstInputManufacturer;
        List<Driver> newListOfDriver = new ArrayList<>();
        newListOfDriver.add(firstDriver);
        newListOfDriver.add(secondDriver);
        Car firstCar = new Car("AUDI",newManufacturer,newListOfDriver);
        System.out.print(firstCar + " -> ");
        Car testCar = carService.create(firstCar);
        System.out.println(testCar);
        System.out.println(carService.get(testCar.getId()));
        Manufacturer newSecondManufacturer = secondInputManufacturer;
        List<Driver> newSecondListOfDriver = new ArrayList<>();
        newSecondListOfDriver.add(firstDriver);
        newSecondListOfDriver.add(secondDriver);
        Car secondCar = new Car("VW",newSecondManufacturer,newSecondListOfDriver);
        testCar = carService.create(secondCar);
        System.out.print(secondCar + " -> ");
        System.out.println(carService.get(testCar.getId()));
        System.out.print("Now update it to -> ");
        Car updatebleCar = testCar;
        updatebleCar.setModel("New Model");
        updatebleCar.setManufacturer(newSecondManufacturer);
        updatebleCar.setDrivers(newSecondListOfDriver);
        carService.update(updatebleCar);
        System.out.println(carService.get(updatebleCar.getId()));
        System.out.println("\nNow delete Car id = "
                + testCar.getId() + "  from DB ");
        if (carService.delete(testCar.getId())) {
            System.out.println("Car id = " + testCar.getId()
                    + " deleted successfully");
        }
        cars = carService.getAll();
        cars.forEach(System.out::println);

        carService.addDriverToCar(testDriver, testCar);
        carService.getAllByDriver(testDriver.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(testDriver, testCar);
        carService.getAllByDriver(testDriver.getId()).forEach(System.out::println);
        System.out.println("App.finish");
    }
}
