package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService = (ManufacturerServiceImpl)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverServiceImpl) injector
            .getInstance(DriverService.class);
    private static final CarService carService = (CarServiceImpl) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        final Manufacturer firstManufacturer = new Manufacturer("first", "Ukraine");
        final Manufacturer secondManufacturer = new Manufacturer("second", "Spain");
        final Manufacturer thirdManufacturer = new Manufacturer("third", "Lithuania");
        final Manufacturer fourthManufacturer = new Manufacturer("fourth", "USA");
        final Manufacturer fifthManufacturer = new Manufacturer("fifth", "South Korea");

        final Driver bobDriver = new Driver("Bob", "bobNumber");
        final Driver aliceDriver = new Driver("Alice", "aliceNumber");
        final Driver alexDriver = new Driver("Alex", "alexNumber");
        final Driver michaelDriver = new Driver("Michael", "michaelNumber");
        final Driver saraDriver = new Driver("Sara", "saraNumber");
        final Driver meryDriver = new Driver("Mery", "meryNumber");
        final Driver christineDriver = new Driver("Christine", "christineNumber");
        final Driver vitoDriver = new Driver("Vito", "vitoNumber");
        final Driver joeDriver = new Driver("Joe", "joeNumber");
        final Driver barakDriver = new Driver("Barak", "barakNumber");

        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);
        manufacturerService.create(thirdManufacturer);
        manufacturerService.create(fourthManufacturer);
        manufacturerService.create(fifthManufacturer);
        System.out.println("TESTING CREATE MANUFACTURERS METHOD");

        driverService.create(bobDriver);
        driverService.create(aliceDriver);
        driverService.create(alexDriver);
        driverService.create(michaelDriver);
        driverService.create(saraDriver);
        driverService.create(meryDriver);
        driverService.create(christineDriver);
        driverService.create(vitoDriver);
        driverService.create(joeDriver);
        driverService.create(barakDriver);
        System.out.println("TESTING CREATE DRIVERS METHOD");

        Car firstCar = new Car("firstModel", firstManufacturer, new ArrayList<>());
        firstCar.getDrivers().add(bobDriver);
        firstCar.getDrivers().add(aliceDriver);

        Car secondCar = new Car("secondModel", secondManufacturer, new ArrayList<>());
        secondCar.getDrivers().add(alexDriver);
        secondCar.getDrivers().add(michaelDriver);

        Car thirdCar = new Car("thirdModel", thirdManufacturer, new ArrayList<>());
        thirdCar.getDrivers().add(saraDriver);
        thirdCar.getDrivers().add(meryDriver);

        Car fourthCar = new Car("fourthModel", fourthManufacturer, new ArrayList<>());
        fourthCar.getDrivers().add(christineDriver);
        fourthCar.getDrivers().add(vitoDriver);

        Car fifthCar = new Car("fifthModel", fifthManufacturer, new ArrayList<>());
        fifthCar.getDrivers().add(joeDriver);
        fifthCar.getDrivers().add(barakDriver);

        carService.create(firstCar);
        carService.create(secondCar);
        carService.create(thirdCar);
        carService.create(fourthCar);
        carService.create(fifthCar);
        System.out.println("TESTING CREATE CARS METHOD");

        System.out.println(carService.getAll());
        System.out.println("TESTING GET ALL CARS");

        carService.addDriverToCar(meryDriver, fifthCar);
        carService.addDriverToCar(alexDriver, fifthCar);
        System.out.println(carService.getAll());
        System.out.println("TESTING ADD DRIVER TO CAR METHOD");

        carService.removeDriverFromCar(bobDriver, firstCar);
        carService.removeDriverFromCar(michaelDriver, secondCar);
        System.out.println(carService.getAll());
        System.out.println("TESTING REMOVE DRIVER FROM CAR METHOD");

        firstCar.setModel("newModelForFirstCar");
        carService.update(firstCar);
        fourthCar.setModel("newModelForFourthCar");
        carService.update(fourthCar);
        System.out.println("TESTING UPDATE METHOD");

        System.out.println(carService.getAllByDriver(meryDriver.getId()));
        System.out.println(carService.getAllByDriver(alexDriver.getId()));
        System.out.println("TESTING GET ALL CARS BY DRIVER ID METHOD");

        carService.delete(thirdCar.getId());
        carService.delete(fifthCar.getId());
        System.out.println(carService.getAll());
        System.out.println("TESTING DELETE METHOD");

        System.out.println(carService.get(secondCar.getId()));
        System.out.println("TESTING GET CAR BY ITS ID METHOD");
    }
}
