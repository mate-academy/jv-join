package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver testDriver = new Driver();
        testDriver.setId(2L);
        testDriver.setName("Bob");
        testDriver.setLicenseNumber("575757");

        List<Driver> driversForTestCar = new ArrayList<>();
        driversForTestCar.add(testDriver);

        Manufacturer testManufacturer = new Manufacturer(1L,"Audi","Germany");

        Car testCar = new Car();
        testCar.setModel("Kona");
        testCar.setManufacturer(testManufacturer);
        testCar.setDrivers(driversForTestCar);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);

        Car createdCar = carService.create(testCar);
        System.out.println(createdCar);
        System.out.println(carService.get(1L));
        carService.getAll()
                .forEach(System.out::println);

        Car updatedTestCar = new Car(5L,"A4",testManufacturer,driversForTestCar);
        System.out.println(carService.update(updatedTestCar));
        System.out.println(carService.delete(10L));
        carService.addDriverToCar(testDriver,testCar);
        carService.removeDriverFromCar(testDriver,testCar);
        carService.getAllByDriver(1L)
                .forEach(System.out::println);
    }
}
