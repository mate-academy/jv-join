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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer tesla = new Manufacturer("Tesla", "United States");
        manufacturerService.create(tesla);
        Manufacturer mazda = new Manufacturer("Mazda", "Italy");
        manufacturerService.create(mazda);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "AC912345");
        driverService.create(bob);
        Driver alice = new Driver("Alice", "NC917635");
        driverService.create(alice);
        Driver roma = new Driver("Roma", "AM567345");
        driverService.create(roma);
        List<Driver> meganDrivers = new ArrayList<>();
        meganDrivers.add(bob);
        meganDrivers.add(roma);
        List<Driver> x5Drivers = new ArrayList<>();
        x5Drivers.add(alice);
        x5Drivers.add(roma);
        List<Driver> modelXDrivers = new ArrayList<>();
        modelXDrivers.add(alice);
        modelXDrivers.add(bob);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car megan = new Car("Megan", mazda);
        megan.setDrivers(meganDrivers);
        carService.create(megan);
        Car x5 = new Car("X5", bmw);
        x5.setDrivers(x5Drivers);
        carService.create(x5);
        Car modelX = new Car("ModelX", tesla);
        modelX.setDrivers(modelXDrivers);
        carService.create(modelX);
        System.out.println("Initial cars DB : ");
        carService.getAll().forEach(System.out::println);
        System.out.println("All Roma cars : ");
        carService.getAllByDriver(roma.getId()).forEach(System.out::println);
        carService.addDriverToCar(roma, modelX);
        System.out.println("Updated ModelX (added driver) : ");
        System.out.println(carService.get(modelX.getId()));
        carService.removeDriverFromCar(bob, megan);
        System.out.println("Updated Megan (removed driver) : ");
        System.out.println(carService.get(megan.getId()));
        carService.delete(x5.getId());
        System.out.println("Cars DB after deleting X5");
        carService.getAll().forEach(System.out::println);
    }
}
