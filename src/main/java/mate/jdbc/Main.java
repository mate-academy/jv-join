package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        databaseFill();
        Long driverId = 4L;
        System.out.println("Get all cars by driver ID: " + driverId + ".");
        carService.getAllByDriver(driverId).forEach(System.out::println);
        System.out.println("------------------");
        Long carId = 3L;
        System.out.println("Get car by ID: " + carId + ".");
        Car car = carService.get(carId);
        System.out.println(car);
        System.out.println("------------------");
        System.out.println("Get all cars.");
        carService.getAll().forEach(System.out::println);
        System.out.println("------------------");
        System.out.println("Update car: " + car + ".");
        Car carExample = carService.get(2L);
        carExample.setModel("TEST1");
        System.out.println("Updated car" + carService.update(carExample));
        System.out.println("------------------");
        System.out.println("Delete car: " + carExample.getId() + ".");
        System.out.println("Car is Deleted - " + carService.delete(carExample.getId()));
        System.out.println("------------------");
        Car car1 = carService.get(3L);
        System.out.println("Add driver to car" + car1);
        Driver driver = driverService.create(new Driver(null, "Misha", "8888"));
        carService.addDriverToCar(driver,car1);
        System.out.println("------------------");
        System.out.println("Remove driver from car" + car1);
        carService.removeDriverFromCar(driver,car1);
    }

    private static void databaseFill() {
        List<Driver> initDrivers = List.of(
                new Driver(null, "Maksym", "1111"),
                new Driver(null, "Maryna", "2222"),
                new Driver(null, "Sergey", "3333"),
                new Driver(null, "Svitlana", "4444"),
                new Driver(null, "Olena", "5555")
        );
        List<Manufacturer> initManufacturers = List.of(
                new Manufacturer(null, "Mercedes-Benz", "Germany"),
                new Manufacturer(null, "Volkswagen", "Germany"),
                new Manufacturer(null, "Jeep", "USA"),
                new Manufacturer(null, "Hyundai", "S.Korea"),
                new Manufacturer(null, "Nissan", "Japan")
        );
        initDrivers.forEach(driverService::create);
        initManufacturers.forEach(manufacturerService::create);
        List<Driver> drivers = driverService.getAll();
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        List<Car> initCars = new ArrayList<>();
        Car car1 = new Car();
        car1.setModel("A170");
        car1.setManufacturer(manufacturers.get(0));
        initCars.add(car1);
        Car car2 = new Car();
        car2.setModel("JETTA");
        car2.setManufacturer(manufacturers.get(1));
        initCars.add(car2);
        Car car3 = new Car();
        car3.setModel("RENEGAGE");
        car3.setManufacturer(manufacturers.get(2));
        initCars.add(car3);
        Car car4 = new Car();
        car4.setModel("SONATA");
        car4.setManufacturer(manufacturers.get(3));
        initCars.add(car4);
        Car car5 = new Car();
        car5.setModel("LEAF");
        car5.setManufacturer(manufacturers.get(4));
        initCars.add(car5);
        Random random = new Random();
        for (final Car car : initCars) {
            List<Driver> carDrivers = new ArrayList<>();
            carDrivers.add(drivers.get(random.nextInt(5)));
            carDrivers.add(drivers.get(random.nextInt(5)));
            car.setDrivers(carDrivers);
            carService.create(car);
        }
    }
}
