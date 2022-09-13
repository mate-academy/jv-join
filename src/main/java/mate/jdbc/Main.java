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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println("### Initialize ###");
        Driver firstDriver = driverService.create(new Driver(1L,"Jack","123456"));
        Driver secondDriver = driverService.create(new Driver(2L, "Michael", "234567"));
        Driver thirdDriver = driverService.create(new Driver(3L, "Alex", "345678"));
        Driver fourthDriver = driverService.create(new Driver(4L, "Johny", "456789"));
        Driver fifthDriver = driverService.create(new Driver(5L, "Fred", "567891"));
        Driver sixthDriver = driverService.create(new Driver(5L, "Tony", "678912"));

        Manufacturer firstManufacturer = manufacturerService.create(
                new Manufacturer(1L,"Uber","USA"));
        Manufacturer secondManufacturer = manufacturerService.create(
                new Manufacturer(2L,"Gett","United Kingdom"));
        Manufacturer thirdManufacturer = manufacturerService.create(
                new Manufacturer(3L, "Uklon", "Ukraine"));

        Car firstCar = carService.create(
                new Car(1L, "Tesla",firstManufacturer, List.of(firstDriver,secondDriver)));
        Car secondCar = carService.create(
                new Car(2L, "Leaf", secondManufacturer, List.of(thirdDriver,fourthDriver)));
        Car thirdCar = carService.create(
                new Car(3L, "Passat", thirdManufacturer, List.of(fifthDriver,sixthDriver)));

        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Create and insert new car in DB: ###");
        Manufacturer fourthManufacturer = manufacturerService.create(
                new Manufacturer(4L, "OnTaxi", "Ukraine"));
        Car newCar = carService.create(
                new Car(4L, "Camry", fourthManufacturer, List.of(fourthDriver,sixthDriver)));
        System.out.println(newCar);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Delete car by ID = 2 from DB: ###");
        carService.delete(2L);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Get car by id = 4 from DB: ###");
        Car findingCar = carService.get(4L);
        System.out.println(findingCar);
        System.out.println("-----------------------------");
        System.out.println("### Update car by id = 4, update model to Camry Long ###");
        findingCar.setModel("Camry Long");
        carService.update(findingCar);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars by driver id = 6: ###");
        carService.getAllByDriver(6L).forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Add driver Jack to Car id = 3: ###");
        Car id3 = carService.get(3L);
        carService.addDriverToCar(firstDriver,id3);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
        System.out.println("### Delete driver Jack from Car id = 1: ###");
        Car id1 = carService.get(1L);
        carService.removeDriverFromCar(firstDriver,id1);
        System.out.println("-----------------------------");
        System.out.println("### Get all cars from DB: ###");
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------------------------");
    }
}
