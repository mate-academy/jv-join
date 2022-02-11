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
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        driverService = (DriverService) injector
                .getInstance(DriverService.class);
        carService = (CarService) injector
                .getInstance(CarService.class);

        addDefaultManufacturersToDb();

        System.out.println("------Get all Manufacturers-----");
        List<Manufacturer> allManufacturers = manufacturerService.getAll();
        printAll(allManufacturers);

        addDefaultDriversToDb();

        System.out.println("------Get all Drivers-----");
        List<Driver> allDrivers = driverService.getAll();
        printAll(allDrivers);

        Car firstCar = new Car("modelS", allManufacturers.get(2),
                List.of(allDrivers.get(0), allDrivers.get(3)));
        Car secondCar = new Car("Slavuta", allManufacturers.get(0),
                List.of(allDrivers.get(1), allDrivers.get(2)));
        Car thirdCar = new Car("Hurricane", allManufacturers.get(1),
                 List.of(allDrivers.get(4), allDrivers.get(5)));
        carService.create(firstCar);
        carService.create(secondCar);
        carService.create(thirdCar);

        System.out.println("------Get by id 2-----");
        System.out.println(carService.get(2L));
        System.out.println("------Get all Cars-----");
        System.out.println(carService.getAll());

        System.out.println("------Update car id 2-----");
        Car newSecondCar = carService.get(2L);
        newSecondCar.setManufacturer(manufacturerService.get(2L));
        newSecondCar.setDrivers(List.of(driverService.get(1L), driverService.get(2L)));
        carService.update(newSecondCar);
        System.out.println(carService.get(2L));

        System.out.println("------add Driver id 5 To Car id 2-----");
        carService.addDriverToCar(driverService.get(5L), newSecondCar);
        System.out.println(carService.get(2L));

        System.out.println("------remove Driver id 5 on Car id 2-----");
        carService.removeDriverFromCar(driverService.get(5L), newSecondCar);
        System.out.println(carService.get(2L));

        System.out.println("------Get all for Driver id 1-----");
        List<Car> carsByDriver = carService.getAllByDriver(1L);
        printAll(carsByDriver);

        System.out.println("------Delete car id 3-----");
        carService.delete(3L);
        printAll(carService.getAll());

    }

    private static void addDefaultManufacturersToDb() {
        Manufacturer firstManufacturer = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer secondManufacturer = new Manufacturer("Lamborghini", "Italy");
        Manufacturer thirdManufacturer = new Manufacturer("Tesla", "USA");

        List<Manufacturer> listManufacturers = List.of(firstManufacturer,
                secondManufacturer, thirdManufacturer);

        listManufacturers.forEach(l -> manufacturerService.create(l));

    }

    private static void addDefaultDriversToDb() {
        Driver firstDriver = new Driver("Mikola", "AB234324ER");
        Driver secondDriver = new Driver("Nick", "AS3242343LN");
        Driver thirdDriver = new Driver("Rob", "DF6768880VB");
        Driver fourthDriver = new Driver("Vasil", "AB1231987ER");
        Driver fifthDriver = new Driver("Jack", "AS0909090LN");
        Driver sixthDriver = new Driver("Gleb", "DF234444VB");

        List<Driver> driversList = List.of(firstDriver, secondDriver, thirdDriver,
                fourthDriver, fifthDriver, sixthDriver);

        driversList.forEach(l -> driverService.create(l));
    }

    private static <T> void printAll(List<T> data) {
        data.forEach(System.out::println);
    }
}
