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

        printTableOf(manufacturerService);
        Manufacturer viper = new Manufacturer("Viper", "USA");
        System.out.println(manufacturerService.create(viper) + " is added to DB");
        System.out.println(manufacturerService.get(4L)
                + " is deleted: " + manufacturerService.delete(4L));
        Manufacturer mercedes = manufacturerService.get(5L);
        mercedes.setCountry("Germany");
        System.out.println(manufacturerService.update(mercedes) + " is updated");
        printDivider();
        printTableOf(manufacturerService);

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        printTableOf(driverService);
        Driver sofia = new Driver("Sofia", "HF3940184312042");
        System.out.println(driverService.create(sofia) + " is added to DB");
        Driver ksenia = new Driver("Ksenia", "TD3194032174312");
        System.out.println(driverService.create(ksenia) + " is added to DB");
        System.out.println(driverService.get(3L)
                + " is deleted: " + driverService.delete(3L));
        Driver johny = driverService.get(2L);
        johny.setName("Johny");
        System.out.println(driverService.update(johny) + " is updated");
        printDivider();
        printTableOf(driverService);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);

        printTableOf(carService);
        List<Driver> dodgeDriverList = new ArrayList<>();
        dodgeDriverList.add(sofia);
        dodgeDriverList.add(johny);
        Car dodge = new Car("Dodge", viper, dodgeDriverList);
        System.out.println(carService.create(dodge) + " is added to DB");
        System.out.println(carService.get(2L)
                + " is deleted: " + carService.delete(2L));
        Manufacturer mazda = new Manufacturer(3L, "Mazda", "Japan");
        List<Driver> rx5DriverList = new ArrayList<>();
        rx5DriverList.add(sofia);
        rx5DriverList.add(ksenia);
        Car rx5 = new Car(5L, "RX-5", mazda, rx5DriverList);
        System.out.println(carService.update(rx5) + " is updated");

        carService.addDriverToCar(johny, rx5);
        System.out.println(johny + " is added to the car");
        System.out.println(carService.get(rx5.getId()));

        carService.removeDriverFromCar(johny, rx5);
        System.out.println(johny + " is removed from the car");
        System.out.println(carService.get(5L));

        System.out.println("Cars owned by Sofia: ");
        System.out.println(carService.getAllByDriver(sofia.getId()));
        printDivider();
        printTableOf(carService);
    }

    private static void printDivider() {
        System.out.println("-----------------------------------");
    }

    private static void printTableOf(ManufacturerService manufacturerService) {
        System.out.println("Table of manufacturers: ");
        manufacturerService.getAll().forEach(System.out::println);
        printDivider();
    }

    private static void printTableOf(DriverService driverService) {
        System.out.println("Table of drivers: ");
        driverService.getAll().forEach(System.out::println);
        printDivider();
    }

    private static void printTableOf(CarService carService) {
        System.out.println("Table of cars: ");
        carService.getAll().forEach(System.out::println);
        printDivider();
    }
}
